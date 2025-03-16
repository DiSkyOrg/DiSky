package net.itsthesky.disky.elements.structures.slash;

import ch.njol.skript.lang.Trigger;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.itsthesky.disky.DiSky;
import net.itsthesky.disky.core.Bot;
import net.itsthesky.disky.core.SkriptUtils;
import net.itsthesky.disky.elements.events.interactions.SlashCommandReceiveEvent;
import net.itsthesky.disky.elements.events.interactions.SlashCompletionEvent;
import net.itsthesky.disky.elements.structures.slash.elements.OnCooldownEvent;
import net.itsthesky.disky.elements.structures.slash.models.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class SlashManager extends ListenerAdapter {

    // Static Management
    private static final Map<Bot, SlashManager> MANAGERS = new HashMap<>();

    public static SlashManager getManager(Bot bot) {
        return MANAGERS.computeIfAbsent(bot, SlashManager::new);
    }

    public static void shutdownAll() {
        MANAGERS.values().forEach(SlashManager::shutdown);
        MANAGERS.clear();
    }

    // Instance Fields
    private final Map<String, List<Runnable>> waitingGuildCommands = new HashMap<>();
    private final Set<String> readyGuilds = new HashSet<>();
    private final List<Runnable> waitingGlobalCommands = new ArrayList<>();
    private final List<RegisteredGroup> registeredGroups = new ArrayList<>();
    private final Map<String, CommandGroup> commandGroups = new ConcurrentHashMap<>();
    private final Bot bot;
    private boolean readyGlobal = false;

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

            if (readyGuilds.contains(guildId)) {
                registrationTask.run();
            } else {
                waitingGuildCommands.computeIfAbsent(guildId, k -> new ArrayList<>()).add(registrationTask);
                DiSky.debug("Guild " + guildId + " is not ready yet, waiting for ready event to register command group");
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
        if (!waitingGuildCommands.containsKey(guildId)) {
            return;
        }

        readyGuilds.add(guildId);
        final List<Runnable> tasks = waitingGuildCommands.remove(guildId);
        DiSky.debug("Guild " + guildId + " is ready, registering commands (" + tasks.size() + ")");
        tasks.forEach(Runnable::run);
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
                    final OnCooldownEvent.BukkitCooldownEvent bukkitEvent = new OnCooldownEvent.BukkitCooldownEvent(
                            new OnCooldownEvent(),
                            group.getCooldown(event.getUser(), commandPath)
                    );
                    bukkitEvent.setJDAEvent(event);
                    command.prepareArguments(event);
                    command.getOnCooldown().execute(bukkitEvent);

                    return !bukkitEvent.isCancelled();
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
        final SlashCommandReceiveEvent.BukkitSlashCommandReceiveEvent bukkitEvent =
                new SlashCommandReceiveEvent.BukkitSlashCommandReceiveEvent(new SlashCommandReceiveEvent());
        bukkitEvent.setJDAEvent(event);
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
            SlashCompletionEvent.BukkitSlashCompletionEvent bukkitEvent =
                    new SlashCompletionEvent.BukkitSlashCompletionEvent(new SlashCompletionEvent());
            bukkitEvent.setJDAEvent(event);

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
    }

    private void cleanupRegisteredGroups() {
        if (true)
            return; // TODO: Fix this method. It's so fucking hard to both handle JDA's queue system while being stuck on bukkit's single thread system

        // gather all guilds, if any, to clear
        Set<String> guilds = new HashSet<>();
        registeredGroups.forEach(group -> {
            if (group.getGuildId() != null)
                guilds.add(group.getGuildId());
        });

        // delete all commands
        try {
            for (String guildId : guilds) {
                var guild = bot.getInstance().getGuildById(guildId);
                if (guild == null) {
                    DiSky.debug("Guild " + guildId + " not found, skipping command deletion");
                    continue;
                }

                var commands = guild.retrieveCommands().complete(true);
                for (Command cmd : commands)
                    guild.deleteCommandById(cmd.getId()).complete(true);
            }
            var commands = bot.getInstance().retrieveCommands().complete(true);
            for (Command cmd : commands)
                bot.getInstance().deleteCommandById(cmd.getId()).complete(true);
        } catch (RateLimitedException ex) {
            DiSky.debug("Failed to delete all commands: " + ex.getMessage());
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