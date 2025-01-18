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
    private final List<RegisteredCommand> registeredCommands = new ArrayList<>();
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
        CommandGroup group = commandGroups.computeIfAbsent(baseCommandName, name -> {
            CommandType type = parts.length == 1 ? CommandType.SINGLE : CommandType.GROUP;
            return new CommandGroup(name, type);
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
        // Register the main command if it's a single command
        if (group.getType() == CommandType.SINGLE && group.getSingleCommand() != null) {
            final RegisteredCommand registeredCommand = new RegisteredCommand(
                    group.getSingleCommand(),
                    cmd.getIdLong(),
                    bot.getName(),
                    guildId
            );
            registeredCommands.add(registeredCommand);
        }

        // Register all subcommands
        for (ParsedCommand subCmd : group.getSubCommands().values()) {
            final RegisteredCommand registeredCommand = new RegisteredCommand(
                    subCmd,
                    cmd.getIdLong(),
                    bot.getName(),
                    guildId
            );
            registeredCommands.add(registeredCommand);
        }

        // Register subcommands in groups
        for (CommandGroup subGroup : group.getSubGroups().values()) {
            for (ParsedCommand subCmd : subGroup.getSubCommands().values()) {
                final RegisteredCommand registeredCommand = new RegisteredCommand(
                        subCmd,
                        cmd.getIdLong(),
                        bot.getName(),
                        guildId
                );
                registeredCommands.add(registeredCommand);
            }
        }

        DiSky.debug("Successfully registered command group " + group.getName() +
                (guildId != null ? " in guild " + guildId : " globally"));
    }

    // Command Management Methods
    public RegisteredCommand findCommand(String name, String guildId) {
        return registeredCommands.stream()
                .filter(cmd -> cmd.getName().equals(name) && Objects.equals(cmd.getGuildId(), guildId))
                .findFirst()
                .orElse(null);
    }

    public RegisteredCommand findCommand(String name) {
        return registeredCommands.stream()
                .filter(cmd -> cmd.getName().equals(name) && cmd.getGuildId() == null)
                .findFirst()
                .orElse(null);
    }

    public void deleteLocalCommand(RegisteredCommand command) {
        registeredCommands.remove(command);
        final Guild guild = bot.getInstance().getGuildById(command.getGuildId());
        if (guild == null) {
            DiSky.debug("Guild " + command.getGuildId() + " is not available, skipping command deletion");
            return;
        }
        guild.deleteCommandById(command.getCommandId()).complete();
    }

    public void deleteGlobalCommand(RegisteredCommand command) {
        registeredCommands.remove(command);
        bot.getInstance().deleteCommandById(command.getCommandId()).complete();
    }

    // Event Handlers
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        // Build the full command name from the base command and subcommand
        StringBuilder fullCommandName = new StringBuilder(event.getName());

        if (event.getSubcommandGroup() != null) {
            fullCommandName.append(" ").append(event.getSubcommandGroup());
        }
        if (event.getSubcommandName() != null) {
            fullCommandName.append(" ").append(event.getSubcommandName());
        }

        String commandString = fullCommandName.toString();

        // Find the registered command
        RegisteredCommand command = registeredCommands.stream()
                .filter(cmd -> {
                    String originalName = cmd.getParsedCommand().getOriginalName();

                    // For single commands, compare directly
                    if (!commandString.contains(" ") && originalName.equals(commandString)) {
                        return event.isGlobalCommand() ?
                                cmd.getGuildId() == null :
                                cmd.getGuildId() != null && cmd.getGuildId().equals(event.getGuild().getId());
                    }

                    // For subcommands and groups, compare parts
                    String[] originalParts = originalName.split(" ");
                    String[] executedParts = commandString.split(" ");

                    if (originalParts.length != executedParts.length) {
                        return false;
                    }

                    for (int i = 0; i < originalParts.length; i++) {
                        if (!originalParts[i].equalsIgnoreCase(executedParts[i])) {
                            return false;
                        }
                    }

                    return event.isGlobalCommand() ?
                            cmd.getGuildId() == null :
                            cmd.getGuildId() != null && cmd.getGuildId().equals(event.getGuild().getId());
                })
                .findFirst()
                .orElse(null);

        if (command == null) {
            DiSky.debug("Received unregistered command '" + fullCommandName + "' for execution (global: " + event.isGlobalCommand() + ")");
            return;
        }

        tryExecute(command, event);
    }

    @Override
    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event) {
        handleAutoComplete(event);
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
    private void tryExecute(RegisteredCommand command, SlashCommandInteractionEvent event) {
        SkriptUtils.sync(() -> {
            if (handleCooldown(command, event)) {
                return;
            }
            executeCommand(command, event);
        });
    }

    private boolean handleCooldown(RegisteredCommand command, SlashCommandInteractionEvent event) {
        if (command.getParsedCommand().hasCooldown()) {
            if (command.isInCooldown(event.getUser())) {
                if (command.getParsedCommand().getOnCooldown() != null) {
                    final OnCooldownEvent.BukkitCooldownEvent bukkitEvent = new OnCooldownEvent.BukkitCooldownEvent(
                            new OnCooldownEvent(),
                            command.getCooldown(event.getUser()) - System.currentTimeMillis()
                    );
                    bukkitEvent.setJDAEvent(event);
                    command.getParsedCommand().prepareArguments(event.getOptions());
                    command.getParsedCommand().getOnCooldown().execute(bukkitEvent);

                    return !bukkitEvent.isCancelled();
                }
            }
            command.setCooldown(event.getUser());
        }
        return false;
    }

    private void executeCommand(RegisteredCommand command, SlashCommandInteractionEvent event) {
        command.getParsedCommand().prepareArguments(event.getOptions());
        final Trigger trigger = command.getParsedCommand().getTrigger();
        final SlashCommandReceiveEvent.BukkitSlashCommandReceiveEvent bukkitEvent =
                new SlashCommandReceiveEvent.BukkitSlashCommandReceiveEvent(new SlashCommandReceiveEvent());
        bukkitEvent.setJDAEvent(event);
        trigger.execute(bukkitEvent);
    }

    private void handleAutoComplete(CommandAutoCompleteInteractionEvent event) {
        RegisteredCommand registeredCommand = event.isGlobalCommand() ?
                findCommand(event.getName()) :
                findCommand(event.getName(), event.getGuild().getId());

        if (registeredCommand == null) {
            DiSky.debug("Received unregistered command " + event.getName() + " for auto-completion");
            return;
        }

        String focusedArgument = event.getFocusedOption().getName();
        Trigger trigger = registeredCommand.getParsedCommand().getArguments()
                .stream()
                .filter(arg -> arg.getName().equals(focusedArgument))
                .findFirst()
                .map(ParsedArgument::getOnCompletionRequest)
                .orElse(null);

        if (trigger == null) {
            DiSky.debug("No completion trigger for argument " + focusedArgument);
            return;
        }

        registeredCommand.getParsedCommand().prepareArguments(event.getOptions());
        SlashCompletionEvent.BukkitSlashCompletionEvent bukkitEvent =
                new SlashCompletionEvent.BukkitSlashCompletionEvent(new SlashCompletionEvent());
        bukkitEvent.setJDAEvent(event);
        trigger.execute(bukkitEvent);
    }

    // Cleanup Methods
    public void shutdown() {
        bot.getInstance().removeEventListener(this);
        cleanupRegisteredCommands();
        registeredCommands.clear();
        commandGroups.clear();
    }

    private void cleanupRegisteredCommands() {
        // gather all guilds, if any, to clear
        Set<String> guilds = new HashSet<>();
        registeredCommands.forEach(cmd -> {
            if (cmd.getGuildId() != null)
                guilds.add(cmd.getGuildId());
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

        debug.append("\nRegistered Commands (").append(registeredCommands.size()).append("):\n");
        registeredCommands.forEach(cmd ->
                debug.append("  - ").append(cmd.getName())
                        .append(" (ID: ").append(cmd.getCommandId()).append(")")
                        .append(cmd.getGuildId() != null ? " [Guild: " + cmd.getGuildId() + "]" : " [Global]")
                        .append("\n")
        );

        return debug.toString();
    }

    public Map<String, CommandGroup> getCommandGroups() {
        return Collections.unmodifiableMap(commandGroups);
    }

    public List<RegisteredCommand> getRegisteredCommands() {
        return Collections.unmodifiableList(registeredCommands);
    }
}