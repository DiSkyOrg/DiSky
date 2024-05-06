package info.itsthesky.disky.elements.structures.slash;

import ch.njol.skript.lang.Trigger;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.core.Bot;
import info.itsthesky.disky.elements.effects.SendTyping;
import info.itsthesky.disky.elements.events.interactions.SlashCommandReceiveEvent;
import info.itsthesky.disky.elements.structures.slash.models.ParsedCommand;
import info.itsthesky.disky.elements.structures.slash.models.RegisteredCommand;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.requests.RestAction;

import java.util.*;

public final class SlashManager extends ListenerAdapter {

    private static final Map<Bot, SlashManager> MANAGERS = new HashMap<>();

    public static SlashManager getManager(Bot bot) {
        return MANAGERS.computeIfAbsent(bot, b -> new SlashManager(b));
    }

    public static void shutdownAll() {
        MANAGERS.values().forEach(SlashManager::shutdown);
        MANAGERS.clear();
    }

    private final Map<String, List<Runnable>> waitingGuildCommands = new HashMap<>();
    private final Set<String> readyGuilds = new HashSet<>();

    private final List<RegisteredCommand> registeredCommands;
    private final Bot bot;

    private SlashManager(Bot bot) {
        registeredCommands = new ArrayList<>();
        this.bot = bot;

        DiSky.debug("Created SlashManager for bot " + bot.getName());
        this.bot.getInstance().addEventListener(this);
    }

    public void registerCommand(ParsedCommand command) {
        if (command.getGuilds().isEmpty()) {
            // todo: register command globally
            DiSky.debug("Registering command " + command.getName() + " for all guilds on bot " + bot.getName());
            DiSky.debug("(Not implemented yet)");
        } else {
            for (String guildId : command.getGuilds()) {
                DiSky.debug("Managing command " + command.getName() + " for guild " + guildId + " on bot " + bot.getName());
                final Runnable runnable = () -> {
                    final RegisteredCommand registeredCommand = findCommand(command.getName(), guildId);
                    if (registeredCommand == null) {
                        DiSky.debug("Command " + command.getName() + " is not registered on guild " + guildId + " for bot " + bot.getName() + ", registering ...");
                        registerCommand(command, bot, guildId);
                    } else {
                        DiSky.debug("Command " + command.getName() + " is already registered on guild " + guildId + " for bot " + bot.getName() + ", updating ...");
                        updateCommand(command, registeredCommand, bot, guildId);
                    }
                };

                if (readyGuilds.contains(guildId)) {
                    runnable.run();
                } else {
                    waitingGuildCommands.computeIfAbsent(guildId, k -> new ArrayList<>()).add(runnable);
                    DiSky.debug("Guild " + guildId + " is not ready yet, waiting for ready event to register command (now " + waitingGuildCommands.get(guildId).size() + " waiting)");
                }
            }
        }
    }

    public RegisteredCommand findCommand(String name, String guildId) {
        return registeredCommands.stream()
                .filter(registeredCommand -> registeredCommand.getName().equals(name))
                .filter(registeredCommand -> registeredCommand.getGuildId().equals(guildId))
                .findFirst()
                .orElse(null);
    }

    public void registerCommand(ParsedCommand command,
                                Bot bot, String guildId) {
        final SlashCommandData slashCommandData = buildCommand(command);
        final RestAction<Command> createAction
                = bot.getInstance().getGuildById(guildId).upsertCommand(slashCommandData);

        createAction.queue(cmd -> {
            final RegisteredCommand registeredCommand = new RegisteredCommand(
                    command,
                    cmd.getIdLong(),
                    bot.getName(),
                    guildId
            );

            registeredCommands.add(registeredCommand);
            DiSky.debug("{CREATE} Registered command " + command.getName() + " on guild " + guildId + " for bot " + bot.getName());
        }, ex -> {
            DiSky.debug("{CREATE} Failed to register command " + command.getName() + " on guild " + guildId + " for bot " + bot.getName());
            DiSky.getErrorHandler().exception(null, ex);
        });
    }

    public void updateCommand(ParsedCommand command,
                              RegisteredCommand registeredCommand,
                              Bot bot, String guildId) {
        // We first must check if they were any changes in the command itself

        registeredCommand.setTrigger(command.getTrigger()); // we update the trigger anyway & the args
        registeredCommand.setArguments(command.getArguments());

        if (!registeredCommand.shouldUpdate(command))
        {
            DiSky.debug("{UPDATE} No changes detected for command " + command.getName() + " on guild " + guildId + " for bot " + bot.getName());
            return; // no changes, no need to update for Discord
        }

        final SlashCommandData slashCommandData = buildCommand(command);
        bot.getInstance().getGuildById(guildId).editCommandById(registeredCommand.getCommandId()).apply(slashCommandData).queue(cmd -> {
            if (cmd.getIdLong() != registeredCommand.getCommandId())
                throw new IllegalStateException("Command ID changed after update! (this should never happens)");

            DiSky.debug("{UPDATE} Updated command " + command.getName() + " on guild " + guildId + " for bot " + bot.getName());
        }, ex -> {
            DiSky.debug("{UPDATE} Failed to update command " + command.getName() + " on guild " + guildId + " for bot " + bot.getName());
            DiSky.debug("We'll register it again instead");
            DiSky.getErrorHandler().exception(null, ex);

            registerCommand(command, bot, guildId);
        });
    }

    public void shutdown() {
        bot.getInstance().removeEventListener(this);
        registeredCommands.forEach(registeredCommand -> {
            bot.getInstance().getGuildById(registeredCommand.getGuildId())
                    .deleteCommandById(registeredCommand.getCommandId()).complete();
            DiSky.debug("{DELETE} Deleted command " + registeredCommand.getName() + " on guild " + registeredCommand.getGuildId() + " for bot " + bot.getName());
        });
        registeredCommands.clear();
    }





    private SlashCommandData buildCommand(ParsedCommand parsedCommand) {
        final SlashCommandData slashCommandData = Commands.slash(parsedCommand.getName(), parsedCommand.getDescription());

        if (!parsedCommand.getArguments().isEmpty()) {
            parsedCommand.getArguments().forEach(parsedArgument -> {
                final OptionData optionData = new OptionData(parsedArgument.getType(),
                        parsedArgument.getName(),
                        parsedArgument.getDescription(),
                        parsedArgument.isRequired());
                if (parsedArgument.hasChoices()) {
                    parsedArgument.getChoices().forEach((key, value) -> {
                        if (value instanceof String)
                            optionData.addChoice(key, (String) value);
                        else if (value instanceof Integer)
                            optionData.addChoice(key, (int) value);
                        else if (value instanceof Long)
                            optionData.addChoice(key, (long) value);
                        else if (value instanceof Double)
                            optionData.addChoice(key, (double) value);
                        else if (value instanceof Number)
                            optionData.addChoice(key, ((Number) value).doubleValue());
                    });
                }

                slashCommandData.addOptions(optionData);
            });
        }

        slashCommandData.setDescriptionLocalizations(parsedCommand.getDescriptionLocalizations());
        slashCommandData.setNameLocalizations(parsedCommand.getNameLocalizations());
        slashCommandData.setDefaultPermissions(DefaultMemberPermissions.enabledFor(parsedCommand.getEnabledFor()));
        if (parsedCommand.isDisabledByDefault())
            slashCommandData.setDefaultPermissions(DefaultMemberPermissions.DISABLED);

        return slashCommandData;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.isFromGuild()) {
            final RegisteredCommand registeredCommand = findCommand(event.getName(), event.getGuild().getId());
            if (registeredCommand == null) {
                DiSky.debug("Received command " + event.getName() + " but it's not registered on guild " + event.getGuild().getId() + " for bot " + event.getJDA().getSelfUser().getGlobalName());
                return;
            }

            registeredCommand.prepareArguments(event);
            final Trigger trigger = registeredCommand.getTrigger();
            final SlashCommandReceiveEvent.BukkitSlashCommandReceiveEvent bukkitEvent = new SlashCommandReceiveEvent.BukkitSlashCommandReceiveEvent(new SlashCommandReceiveEvent());
            bukkitEvent.setJDAEvent(event);

            trigger.execute(bukkitEvent);
        }
    }

    @Override
    public void onReady(ReadyEvent event) {
        //todo: global commands
    }

    @Override
    public void onGuildReady(GuildReadyEvent event) {
        final String guildId = event.getGuild().getId();
        if (readyGuilds.contains(guildId)) {
            DiSky.debug("Guild " + guildId + " is already ready, skipping command registration");
            return;
        } else if (!waitingGuildCommands.containsKey(guildId)) {
            DiSky.debug("Guild " + guildId + " is ready, but no command to register");
            return;
        }

        readyGuilds.add(guildId);
        final Collection<Runnable> runnables = waitingGuildCommands.get(guildId);
        DiSky.debug("Guild " + guildId + " is ready, registering commands (" + runnables.size() + ")");
        runnables.forEach(Runnable::run);
        waitingGuildCommands.remove(guildId);
    }
}
