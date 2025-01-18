package net.itsthesky.disky.elements.structures.context;

import net.itsthesky.disky.DiSky;
import net.itsthesky.disky.core.Bot;
import net.itsthesky.disky.elements.sections.handler.DiSkyRuntimeHandler;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class ContextCommandManager extends ListenerAdapter {

    // Static Management
    private static final Map<Bot, ContextCommandManager> MANAGERS = new HashMap<>();

    public static ContextCommandManager getManager(Bot bot) {
        return MANAGERS.computeIfAbsent(bot, ContextCommandManager::new);
    }

    public static void shutdownAll() {
        MANAGERS.values().forEach(ContextCommandManager::shutdown);
        MANAGERS.clear();
    }

    // Instance Fields
    private final Map<String, List<Runnable>> waitingGuildCommands = new HashMap<>();
    private final Set<String> readyGuilds = new HashSet<>();
    private final List<Runnable> waitingGlobalCommands = new ArrayList<>();
    private final Map<Long, ParsedContextCommand> registeredCommands = new ConcurrentHashMap<>();
    private final Bot bot;
    private boolean readyGlobal = false;

    private ContextCommandManager(Bot bot) {
        this.bot = bot;
        DiSky.debug("Created ContextCommandManager for bot " + bot.getName());
        this.bot.getInstance().addEventListener(this);
    }

    // Registration Methods
    public void registerCommand(ParsedContextCommand command) {
        CommandData commandData;
        
        if (command.getType() == Command.Type.USER) {
            commandData = Commands.user(command.getName());
        } else {
            commandData = Commands.message(command.getName());
        }

        if (command.getNameLocalizations() != null && !command.getNameLocalizations().isEmpty()) {
            commandData.setNameLocalizations(command.getNameLocalizations());
        }

        commandData.setDefaultPermissions(command.getPermissions());

        if (command.getGuilds().isEmpty()) {
            registerGlobalCommand(command, commandData);
        } else {
            for (String guildId : command.getGuilds()) {
                registerGuildCommand(command, commandData, guildId);
            }
        }
    }

    private void registerGlobalCommand(ParsedContextCommand command, CommandData commandData) {
        final Runnable registrationTask = () -> {
            bot.getInstance().upsertCommand(commandData).queue(
                cmd -> {
                    registeredCommands.put(cmd.getIdLong(), command);
                    DiSky.debug("Successfully registered command " + command.getName() + " globally");
                },
                error -> DiSky.debug("Failed to register command " + command.getName() + ": " + error.getMessage())
            );
        };

        if (readyGlobal) {
            registrationTask.run();
        } else {
            waitingGlobalCommands.add(registrationTask);
        }
    }

    private void registerGuildCommand(ParsedContextCommand command, CommandData commandData, String guildId) {
        final Runnable registrationTask = () -> {
            final var guild = bot.getInstance().getGuildById(guildId);
            if (guild == null) {
                DiSky.debug("Guild " + guildId + " not found for command " + command.getName());
                return;
            }

            guild.upsertCommand(commandData).queue(
                cmd -> {
                    registeredCommands.put(cmd.getIdLong(), command);
                    DiSky.debug("Successfully registered command " + command.getName() + " in guild " + guildId);
                },
                error -> DiSky.debug("Failed to register command " + command.getName() + " in guild " + guildId + ": " + error.getMessage())
            );
        };

        if (readyGuilds.contains(guildId)) {
            registrationTask.run();
        } else {
            waitingGuildCommands.computeIfAbsent(guildId, k -> new ArrayList<>()).add(registrationTask);
        }
    }

    // Event Handlers
    @Override
    public void onUserContextInteraction(@NotNull UserContextInteractionEvent event) {
        handleContextCommand(event.getCommandIdLong(), event);
    }

    @Override
    public void onMessageContextInteraction(@NotNull MessageContextInteractionEvent event) {
        handleContextCommand(event.getCommandIdLong(), event);
    }

    private void handleContextCommand(long commandId, Object event) {
        ParsedContextCommand command = registeredCommands.get(commandId);
        if (command == null) {
            DiSky.debug("Received unregistered context command with ID: " + commandId);
            return;
        }

        try {
            command.getTrigger().execute(command.wrapEvent(event));
        } catch (Exception e) {
            DiSkyRuntimeHandler.error(new Exception("Failed to execute context command " + command.getName(), e));
        }
    }

    // JDA Ready Event Handlers
    @Override
    public void onReady(@NotNull net.dv8tion.jda.api.events.session.ReadyEvent event) {
        readyGlobal = true;
        DiSky.debug("Bot " + bot.getName() + " ready, registering global commands (" + waitingGlobalCommands.size() + ")");
        waitingGlobalCommands.forEach(Runnable::run);
        waitingGlobalCommands.clear();
    }

    @Override
    public void onGuildReady(@NotNull net.dv8tion.jda.api.events.guild.GuildReadyEvent event) {
        final String guildId = event.getGuild().getId();
        if (!waitingGuildCommands.containsKey(guildId)) {
            return;
        }

        readyGuilds.add(guildId);
        final List<Runnable> tasks = waitingGuildCommands.remove(guildId);
        DiSky.debug("Guild " + guildId + " ready, registering commands (" + tasks.size() + ")");
        tasks.forEach(Runnable::run);
    }

    // Cleanup Methods
    public void shutdown() {
        bot.getInstance().removeEventListener(this);
        cleanupCommands();
        registeredCommands.clear();
    }

    private void cleanupCommands() {
        List<RestAction<?>> actions = new ArrayList<>();
        
        // Delete global commands
        registeredCommands.forEach((id, command) -> {
            if (command.getGuilds().isEmpty()) {
                actions.add(bot.getInstance().deleteCommandById(id));
            }
        });

        // Delete guild commands
        registeredCommands.forEach((id, command) -> {
            for (String guildId : command.getGuilds()) {
                var guild = bot.getInstance().getGuildById(guildId);
                if (guild != null) {
                    actions.add(guild.deleteCommandById(id));
                }
            }
        });

        // Execute all delete actions
        try {
            RestAction.allOf(actions).complete();
        } catch (Exception e) {
            DiSkyRuntimeHandler.error(new Exception("Failed to delete commands", e));
        }
    }

    // Utility Methods
    public @Nullable ParsedContextCommand getCommand(long id) {
        return registeredCommands.get(id);
    }

    public Collection<ParsedContextCommand> getRegisteredCommands() {
        return Collections.unmodifiableCollection(registeredCommands.values());
    }
}