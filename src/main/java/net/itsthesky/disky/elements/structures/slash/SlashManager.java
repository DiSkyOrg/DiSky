package net.itsthesky.disky.elements.structures.slash;

import ch.njol.skript.lang.Trigger;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.itsthesky.disky.DiSky;
import net.itsthesky.disky.core.Bot;
import net.itsthesky.disky.core.SkriptUtils;
import net.itsthesky.disky.elements.events.rework.CommandEvents;
import net.itsthesky.disky.elements.events.rework.custom.SlashCooldownEvent;
import net.itsthesky.disky.elements.structures.slash.models.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public final class SlashManager extends ListenerAdapter {

    // Static Management
    private static final Map<Bot, SlashManager> MANAGERS = new HashMap<>();

    public static SlashManager getManager(Bot bot) {
        return MANAGERS.computeIfAbsent(bot, SlashManager::new);
    }

    public static void shutdownAll() {
        MANAGERS.values().forEach(SlashManager::shutdown);
        MANAGERS.clear();
        retryScheduler.shutdown();
    }

    // Instance Fields
    private final Map<String, List<Runnable>> waitingGuildCommands = new HashMap<>();
    private final Map<String, AtomicInteger> guildRetryAttempts = new ConcurrentHashMap<>();
    private final Set<String> readyGuilds = new HashSet<>();
    private final List<Runnable> waitingGlobalCommands = new ArrayList<>();
    private final List<RegisteredGroup> registeredGroups = new ArrayList<>();
    private final Map<String, CommandGroup> commandGroups = new ConcurrentHashMap<>();
    private final Bot bot;
    private boolean readyGlobal = false;

    // Retry configuration
    private static final int MAX_RETRY_ATTEMPTS = 5;
    private static final long BASE_RETRY_DELAY_MS = 1000; // 1 second
    private static final ScheduledExecutorService retryScheduler = Executors.newScheduledThreadPool(2);

    private SlashManager(Bot bot) {
        this.bot = bot;
        DiSky.debug("Created SlashManager for bot " + bot.getName());
        this.bot.getInstance().addEventListener(this);
    }

    // Main Registration Methods
    public void registerCommand(ParsedCommand command) {
        String[] parts = command.getName().split(" ");
        String baseCommandName = parts[0];

        // Get or create the command group
        CommandGroup group = commandGroups.compute(baseCommandName, (name, existingGroup) -> {
            CommandType type = parts.length == 1 ? CommandType.SINGLE : CommandType.GROUP;
            return existingGroup != null ? existingGroup : new CommandGroup(name, type);
        });

        // Add the command to the group
        group.addSubCommand(command);

        // Register the group
        if (command.getGuilds().isEmpty()) {
            registerGlobalCommandGroup(group);
        } else {
            handleGuildSpecificCommandGroup(group, command.getGuilds());
        }
    }

    private void registerGlobalCommandGroup(CommandGroup group) {
        DiSky.debug("Registering command group " + group.getName() + " globally");

        final Runnable registrationTask = () -> {
            final SlashCommandData commandData = group.buildCommandData();

            bot.getInstance().upsertCommand(commandData).queue(
                    cmd -> registerCommandGroupSuccess(group, cmd, null),
                    error -> DiSky.debug("Failed to register command group " + group.getName() + ": " + error.getMessage())
            );
        };

        if (readyGlobal) {
            registrationTask.run();
        } else {
            waitingGlobalCommands.add(registrationTask);
            DiSky.debug("Bot " + bot.getName() + " is not ready yet, waiting for ready event to register command group");
        }
    }

    private void handleGuildSpecificCommandGroup(CommandGroup group, List<String> guildIds) {
        for (String guildId : guildIds) {
            final Runnable registrationTask = () -> {
                final Guild guild = bot.getInstance().getGuildById(guildId);
                if (guild == null) {
                    DiSky.debug("Guild " + guildId + " not found, skipping command group registration");
                    return;
                }

                final SlashCommandData commandData = group.buildCommandData();
                guild.upsertCommand(commandData).queue(
                        cmd -> registerCommandGroupSuccess(group, cmd, guildId),
                        error -> DiSky.debug("Failed to register command group " + group.getName() + " in guild " + guildId + ": " + error.getMessage())
                );
            };

            if (isGuildReady(guildId)) {
                registrationTask.run();
            } else {
                waitingGuildCommands.computeIfAbsent(guildId, k -> new ArrayList<>()).add(registrationTask);
                DiSky.debug("Guild " + guildId + " is not ready yet, waiting for ready event to register command group");

                // Schedule a retry attempt
                scheduleGuildRegistrationRetry(guildId);
            }
        }
    }

    private void registerCommandGroupSuccess(CommandGroup group, Command cmd, @Nullable String guildId) {
        // Remove any existing group with the same name and guild
        registeredGroups.removeIf(rg ->
                rg.getCommandGroup().getName().equals(group.getName()) &&
                        Objects.equals(rg.getGuildId(), guildId)
        );

        // Create and add the new registered group
        RegisteredGroup registeredGroup = new RegisteredGroup(
                group,
                cmd.getIdLong(),
                bot.getName(),
                guildId
        );

        registeredGroups.add(registeredGroup);

        DiSky.debug("Successfully registered command group " + group.getName() +
                (guildId != null ? " in guild " + guildId : " globally"));
    }

    // Command Management Methods
    public RegisteredGroup findGroup(String commandName, String guildId) {
        String baseName = commandName.split(" ")[0];

        return registeredGroups.stream()
                .filter(group ->
                        group.getCommandGroup().getName().equals(baseName) &&
                                Objects.equals(group.getGuildId(), guildId)
                )
                .findFirst()
                .orElse(null);
    }

    public RegisteredGroup findGroup(String commandName) {
        String baseName = commandName.split(" ")[0];

        return registeredGroups.stream()
                .filter(group ->
                        group.getCommandGroup().getName().equals(baseName) &&
                                group.getGuildId() == null
                )
                .findFirst()
                .orElse(null);
    }

    // Event Handlers
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        // Build the full command name
        StringBuilder fullCommandName = new StringBuilder(event.getName());

        if (event.getSubcommandGroup() != null) {
            fullCommandName.append(" ").append(event.getSubcommandGroup());
        }
        if (event.getSubcommandName() != null) {
            fullCommandName.append(" ").append(event.getSubcommandName());
        }

        String commandString = fullCommandName.toString();

        // Find the registered group
        RegisteredGroup group = event.isGlobalCommand() ?
                findGroup(event.getName()) :
                findGroup(event.getName(), Objects.requireNonNull(event.getGuild()).getId());

        if (group == null) {
            DiSky.debug("Received unregistered command '" + fullCommandName + "' for execution (global: " + event.isGlobalCommand() + ")");
            return;
        }

        // Find the specific command within the group
        ParsedCommand command = group.findCommand(commandString);
        if (command == null) {
            DiSky.debug("Command '" + commandString + "' not found in group " + group.getCommandGroup().getName());
            return;
        }

        // Execute the command
        tryExecute(group, command, event);
    }

    @Override
    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event) {
        // Build the full command name
        StringBuilder fullCommandName = new StringBuilder(event.getName());

        if (event.getSubcommandGroup() != null) {
            fullCommandName.append(" ").append(event.getSubcommandGroup());
        }
        if (event.getSubcommandName() != null) {
            fullCommandName.append(" ").append(event.getSubcommandName());
        }

        String commandString = fullCommandName.toString();

        // Find the registered group
        RegisteredGroup group = event.isGlobalCommand() ?
                findGroup(event.getName()) :
                findGroup(event.getName(), Objects.requireNonNull(event.getGuild()).getId());

        if (group == null) {
            DiSky.debug("Received unregistered command '" + event.getName() + "' for auto-completion");
            return;
        }

        // Find the specific command within the group
        ParsedCommand command = group.findCommand(commandString);
        if (command == null) {
            DiSky.debug("Command '" + commandString + "' not found in group " + group.getCommandGroup().getName());
            return;
        }

        // Handle auto-completion
        handleAutoComplete(command, event);
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        readyGlobal = true;
        DiSky.debug("Bot " + bot.getName() + " is ready, registering commands (" + waitingGlobalCommands.size() + ")");
        waitingGlobalCommands.forEach(Runnable::run);
        waitingGlobalCommands.clear();
    }

    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {
        final String guildId = event.getGuild().getId();
        processGuildReady(guildId);
    }

    /**
     * Checks if a guild is ready for command registration
     * Uses both cached state and active verification
     */
    private boolean isGuildReady(String guildId) {
        // First check our cached state
        if (readyGuilds.contains(guildId)) {
            return true;
        }

        // Active verification - check if guild exists and is accessible
        Guild guild = bot.getInstance().getGuildById(guildId);
        if (guild != null) {
            // Guild exists and can be accessed, consider it ready
            readyGuilds.add(guildId);
            DiSky.debug("Guild " + guildId + " was ready but not in cache, adding to ready set");
            return true;
        }

        return false;
    }

    /**
     * Processes a guild becoming ready and executes pending commands
     */
    private void processGuildReady(String guildId) {
        if (!waitingGuildCommands.containsKey(guildId)) {
            readyGuilds.add(guildId);
            return;
        }

        readyGuilds.add(guildId);
        guildRetryAttempts.remove(guildId); // Clear retry attempts
        final List<Runnable> tasks = waitingGuildCommands.remove(guildId);
        DiSky.debug("Guild " + guildId + " is ready, registering commands (" + tasks.size() + ")");
        tasks.forEach(Runnable::run);
    }

    /**
     * Schedules a retry attempt for guild command registration
     */
    private void scheduleGuildRegistrationRetry(String guildId) {
        AtomicInteger attempts = guildRetryAttempts.computeIfAbsent(guildId, k -> new AtomicInteger(0));
        int currentAttempt = attempts.incrementAndGet();

        if (currentAttempt >= MAX_RETRY_ATTEMPTS) {
            DiSky.debug("Max retry attempts (" + MAX_RETRY_ATTEMPTS + ") reached for guild " + guildId + ", giving up");
            return;
        }

        long delay = BASE_RETRY_DELAY_MS * (1L << (currentAttempt - 1)); // Exponential backoff
        DiSky.debug("Scheduling retry attempt " + currentAttempt + " for guild " + guildId + " in " + delay + "ms");

        retryScheduler.schedule(() -> {
            try {
                if (isGuildReady(guildId)) {
                    DiSky.debug("Guild " + guildId + " became ready during retry attempt " + currentAttempt);
                    processGuildReady(guildId);
                } else {
                    DiSky.debug("Guild " + guildId + " still not ready on retry attempt " + currentAttempt);
                    scheduleGuildRegistrationRetry(guildId);
                }
            } catch (Exception e) {
                DiSky.debug("Error during retry attempt for guild " + guildId + ": " + e.getMessage());
            }
        }, delay, TimeUnit.MILLISECONDS);
    }

    // Command Execution Methods
    private void tryExecute(RegisteredGroup group, ParsedCommand command, SlashCommandInteractionEvent event) {
        SkriptUtils.sync(() -> {
            if (handleCooldown(group, command, event)) {
                return;
            }
            executeCommand(command, event);
        });
    }

    private boolean handleCooldown(RegisteredGroup group, ParsedCommand command, SlashCommandInteractionEvent event) {
        if (command.hasCooldown()) {
            String commandPath = command.getOriginalName();
            if (group.isInCooldown(event.getUser(), commandPath)) {
                if (command.getOnCooldown() != null) {
                    final var jdaEvent = new SlashCooldownEvent(event,
                            group.getCooldown(event.getUser(), commandPath));
                    final var bukkitEvent = CommandEvents.SLASH_COOLDOWN_EVENT.createBukkitInstance(jdaEvent);
                    command.prepareArguments(event);
                    command.getOnCooldown().execute(bukkitEvent);

                    return !jdaEvent.isCancelled();
                }
                return true; // Default behavior if no cooldown handler
            }
            group.setCooldown(event.getUser(), commandPath, command.getCooldown());
        }
        return false;
    }

    private void executeCommand(ParsedCommand command, SlashCommandInteractionEvent event) {
        command.prepareArguments(event);
        final Trigger trigger = command.getTrigger();
        final var bukkitEvent = CommandEvents.SLASH_COMMAND_EVENT.createBukkitInstance(event);
        trigger.execute(bukkitEvent);
    }

    private void handleAutoComplete(ParsedCommand command, CommandAutoCompleteInteractionEvent event) {
        final var focusedArgument = event.getFocusedOption().getName();
        final var focusArg = command.getArguments()
                .stream()
                .filter(arg -> arg.getName().equals(focusedArgument))
                .findFirst()
                .orElse(null);

        if (focusArg == null) {
            DiSky.debug("No completion trigger for argument " + focusedArgument);
            return;
        }

        if (focusArg.getCustomArgument() == null) {
            command.prepareArguments(event);
            final var bukkitEvent = CommandEvents.SLASH_COMPLETION_EVENT.createBukkitInstance(event);
            final var trigger = focusArg.getOnCompletionRequest();
            if (trigger == null) {
                DiSky.debug("No completion trigger for argument " + focusedArgument);
                return;
            }

            trigger.execute(bukkitEvent);
        } else {
            final var customArgument = focusArg.getCustomArgument();
            final var input = event.getFocusedOption().getValue();
            final var choices = customArgument.handleAutoCompletion(event, input);
            if (choices != null) {
                event.replyChoices(choices).queue();
            } else {
                DiSky.debug("No choices for argument " + focusedArgument);
            }
        }
    }

    // Cleanup Methods
    public void shutdown() {
        bot.getInstance().removeEventListener(this);
        cleanupRegisteredGroups();
        registeredGroups.clear();
        commandGroups.clear();

        // Clear retry tracking
        guildRetryAttempts.clear();
    }

    private void cleanupRegisteredGroups() {
        Set<String> guilds = new HashSet<>();
        registeredGroups.forEach(group -> {
            if (group.getGuildId() != null)
                guilds.add(group.getGuildId());
        });

        for (String guildId : guilds) {
            // delete every command with a reasonable timeout (3s)
            var guild = bot.getInstance().getGuildById(guildId);
            if (guild == null) {
                DiSky.debug("Guild " + guildId + " not found, skipping command deletion");
                continue;
            }

            try {
                CompletableFuture<List<Command>> futureCommands = new CompletableFuture<>();
                guild.retrieveCommands().queue(
                        futureCommands::complete,
                        futureCommands::completeExceptionally
                );

                List<Command> commands = futureCommands.get(3, TimeUnit.SECONDS);
                for (Command cmd : commands) {
                    CompletableFuture<Void> future = new CompletableFuture<>();
                    guild.deleteCommandById(cmd.getId()).queue(
                            v -> future.complete(null),
                            future::completeExceptionally
                    );
                    future.get(3, TimeUnit.SECONDS);
                }
            } catch (Exception e) {
                DiSky.debug("Failed to delete guild commands: " + e.getMessage());
                for (var trace : e.getStackTrace())
                    DiSky.debug("  " + trace);
            }
        }

        // same for global commands
        try {
            CompletableFuture<List<Command>> futureCommands = new CompletableFuture<>();
            bot.getInstance().retrieveCommands().queue(
                    futureCommands::complete,
                    futureCommands::completeExceptionally
            );

            List<Command> commands = futureCommands.get(3, TimeUnit.SECONDS);
            for (Command cmd : commands) {
                CompletableFuture<Void> future = new CompletableFuture<>();
                bot.getInstance().deleteCommandById(cmd.getId()).queue(
                        v -> future.complete(null),
                        future::completeExceptionally
                );
                future.get(3, TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            DiSky.debug("Failed to delete global commands: " + e.getMessage());
            for (var trace : e.getStackTrace())
                DiSky.debug("  " + trace);
        }
    }

    // Methods for deleting groups
    public void deleteLocalGroup(RegisteredGroup group) {
        registeredGroups.remove(group);
        final Guild guild = bot.getInstance().getGuildById(group.getGuildId());
        if (guild == null) {
            DiSky.debug("Guild " + group.getGuildId() + " is not available, skipping command deletion");
            return;
        }
        guild.deleteCommandById(group.getCommandId()).complete();
    }

    public void deleteGlobalGroup(RegisteredGroup group) {
        registeredGroups.remove(group);
        bot.getInstance().deleteCommandById(group.getCommandId()).complete();
    }

    // Debug Methods
    public String getCommandDebugInfo() {
        StringBuilder debug = new StringBuilder();
        debug.append("Command Groups (").append(commandGroups.size()).append("):\n");

        for (CommandGroup group : commandGroups.values()) {
            debug.append("\nGroup: ").append(group.getName())
                    .append(" (Type: ").append(group.getType()).append(")\n");

            if (group.getType() == CommandType.SINGLE) {
                ParsedCommand cmd = group.getSingleCommand();
                if (cmd != null) {
                    debug.append("  Single Command: ").append(cmd.getName())
                            .append(" (").append(cmd.getDescription()).append(")\n");
                }
            }

            if (!group.getSubCommands().isEmpty()) {
                debug.append("  Subcommands:\n");
                group.getSubCommands().forEach((name, cmd) ->
                        debug.append("    - ").append(name)
                                .append(" (").append(cmd.getDescription()).append(")\n")
                );
            }

            if (!group.getSubGroups().isEmpty()) {
                debug.append("  Subgroups:\n");
                group.getSubGroups().forEach((name, subgroup) -> {
                    debug.append("    + ").append(name).append(":\n");
                    subgroup.getSubCommands().forEach((subname, cmd) ->
                            debug.append("      - ").append(subname)
                                    .append(" (").append(cmd.getDescription()).append(")\n")
                    );
                });
            }
        }

        debug.append("\nRegistered Groups (").append(registeredGroups.size()).append("):\n");
        registeredGroups.forEach(group ->
                debug.append("  - ").append(group.getCommandGroup().getName())
                        .append(" (ID: ").append(group.getCommandId()).append(")")
                        .append(group.getGuildId() != null ? " [Guild: " + group.getGuildId() + "]" : " [Global]")
                        .append("\n")
        );

        return debug.toString();
    }

    public Map<String, CommandGroup> getCommandGroups() {
        return Collections.unmodifiableMap(commandGroups);
    }

    public List<RegisteredGroup> getRegisteredGroups() {
        return Collections.unmodifiableList(registeredGroups);
    }
}