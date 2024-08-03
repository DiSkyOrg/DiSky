package info.itsthesky.disky.elements.structures.slash;

import ch.njol.skript.lang.Trigger;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.core.Bot;
import info.itsthesky.disky.core.SkriptUtils;
import info.itsthesky.disky.elements.events.interactions.SlashCommandReceiveEvent;
import info.itsthesky.disky.elements.events.interactions.SlashCompletionEvent;
import info.itsthesky.disky.elements.structures.slash.elements.OnCooldownEvent;
import info.itsthesky.disky.elements.structures.slash.models.ParsedArgument;
import info.itsthesky.disky.elements.structures.slash.models.ParsedCommand;
import info.itsthesky.disky.elements.structures.slash.models.RegisteredCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.requests.RestAction;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @author ItsTheSky
 */
public final class SlashManager extends ListenerAdapter {

    private static final Map<Bot, SlashManager> MANAGERS = new HashMap<>();

    public static SlashManager getManager(Bot bot) {
        return MANAGERS.computeIfAbsent(bot, SlashManager::new);
    }

    public static void shutdownAll() {
        MANAGERS.values().forEach(SlashManager::shutdown);
        MANAGERS.clear();
    }

    private final Map<String, List<Runnable>> waitingGuildCommands = new HashMap<>();
    private final Set<String> readyGuilds = new HashSet<>();

    private final List<Runnable> waitingGlobalCommands = new ArrayList<>();
    private boolean readyGlobal = false;

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
            DiSky.debug("Registering command " + command.getName() + " for all guilds on bot " + bot.getName());

            // first check for existing ones in guilds (in case we updated it)
            for (RegisteredCommand cmd : new ArrayList<>(registeredCommands)) {
                if (cmd.getGuildId() != null && cmd.getName().equalsIgnoreCase(command.getName())) {
                    deleteLocalCommand(cmd);
                    DiSky.getInstance().getLogger().warning("We deleted the command '" + cmd.getName() + "' of bot '" + bot.getName() + "' as you updated its registration from guild '" + cmd.getGuildId() + "' to global!");
                }
            }

            final Runnable runnable = () -> {
                final RegisteredCommand global = findCommand(command.getName());
                if (global == null) {
                    registerGlobalCommand(command, bot);
                } else {
                    updateGlobalCommand(command, global, bot);
                }
            };

            if (readyGlobal) {
                runnable.run();
            } else {
                if (!bot.getInstance().getStatus().equals(JDA.Status.CONNECTED)) {
                    waitingGlobalCommands.add(runnable);
                    DiSky.debug("Bot " + bot.getName() + " is not ready yet, waiting for ready event to register command (now " + waitingGlobalCommands.size() + " waiting)");
                    return;
                }

                runnable.run();
            }

        } else {
            // first check for existing ones in global
            final RegisteredCommand global = findCommand(command.getName());
            if (global != null) {
                deleteGlobalCommand(global);
                DiSky.getInstance().getLogger().warning("We deleted the command '" + global.getName() + "' of bot '" + bot.getName() + "' as you updated its registration from global to guilds!");
            }

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
                    final @Nullable Guild guild = bot.getInstance().getGuildById(guildId);
                    if (guild == null) {
                        waitingGuildCommands.computeIfAbsent(guildId, k -> new ArrayList<>()).add(runnable);
                        DiSky.debug("Guild " + guildId + " is not ready yet, waiting for ready event to register command (now " + waitingGuildCommands.get(guildId).size() + " waiting)");
                        return;
                    }

                    runnable.run();
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

    public RegisteredCommand findCommand(String name) {
        return registeredCommands.stream()
                .filter(registeredCommand -> registeredCommand.getName().equals(name))
                .filter(registeredCommand -> registeredCommand.getGuildId() == null)
                .findFirst()
                .orElse(null);
    }

    public void deleteLocalCommand(RegisteredCommand command) {
        registeredCommands.remove(command);
        final Guild guild =
        bot.getInstance().getGuildById(command.getGuildId());
        if (guild == null) {
            DiSky.debug("Guild " + command.getGuildId() + " is not available, skipping command deletion");
            return;
        }

        guild.deleteCommandById(command.getCommandId()).queue();
    }

    public void deleteGlobalCommand(RegisteredCommand command) {
        registeredCommands.remove(command);
        bot.getInstance().deleteCommandById(command.getCommandId()).queue();
    }

    public void registerCommand(ParsedCommand command,
                                Bot bot, String guildId) {
        final SlashCommandData slashCommandData = buildCommand(command);
        final Guild guild = bot.getInstance().getGuildById(guildId);
        if (guild == null) {
            DiSky.debug("Guild " + guildId + " is not available, skipping command registration");
            return;
        }
        final RestAction<Command> createAction = guild.upsertCommand(slashCommandData);

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
        if (!registeredCommand.getParsedCommand().shouldUpdate(command))
        {
            DiSky.debug("{UPDATE} No changes detected for command " + command.getName() + " on guild " + guildId + " for bot " + bot.getName());
            return; // no changes, no need to update for Discord
        }

        registeredCommand.setParsedCommand(command);
        final SlashCommandData slashCommandData = buildCommand(command);
        final Guild guild = bot.getInstance().getGuildById(guildId);
        if (guild == null) {
            DiSky.debug("Guild " + guildId + " is not available, skipping command update");
            return;
        }
        guild.editCommandById(registeredCommand.getCommandId()).apply(slashCommandData).queue(cmd -> {
            if (cmd.getIdLong() != registeredCommand.getCommandId())
                throw new IllegalStateException("Command ID changed after update! (this should never happens)");

            DiSky.debug("{UPDATE} Updated command " + command.getName() + " on guild " + guildId + " for bot " + bot.getName());
        }, ex -> {
            DiSky.debug("{UPDATE} Failed to update command " + command.getName() + " on guild " + guildId + " for bot " + bot.getName());
            DiSky.debug("We'll register it again instead (Error: " + ex.getMessage() + ")");

            registeredCommands.remove(registeredCommand);
            registerCommand(command, bot, guildId);
        });
    }

    public void registerGlobalCommand(ParsedCommand command, Bot bot) {
        final SlashCommandData slashCommandData = buildCommand(command);
        final RestAction<Command> createAction
                = bot.getInstance().upsertCommand(slashCommandData);

        createAction.queue(cmd -> {
            final RegisteredCommand registeredCommand = new RegisteredCommand(
                    command,
                    cmd.getIdLong(),
                    bot.getName(),
                    null
            );

            registeredCommands.add(registeredCommand);
            DiSky.debug("{CREATE} Registered command " + command.getName() + " on all guilds for bot " + bot.getName());
        }, ex -> {
            DiSky.debug("{CREATE} Failed to register command " + command.getName() + " on all guilds for bot " + bot.getName());
            DiSky.getErrorHandler().exception(null, ex);
        });
    }

    public void updateGlobalCommand(ParsedCommand command,
                                    RegisteredCommand registeredCommand,
                                    Bot bot) {
        // We first must check if they were any changes in the command itself
        if (!registeredCommand.getParsedCommand().shouldUpdate(command))
        {
            DiSky.debug("{UPDATE} No changes detected for command " + command.getName() + " on all guilds for bot " + bot.getName());
            return; // no changes, no need to update for Discord
        }

        registeredCommand.setParsedCommand(command);
        final SlashCommandData slashCommandData = buildCommand(command);
        bot.getInstance().editCommandById(registeredCommand.getCommandId()).apply(slashCommandData).queue(cmd -> {
            if (cmd.getIdLong() != registeredCommand.getCommandId())
                throw new IllegalStateException("Command ID changed after update! (this should never happens)");

            DiSky.debug("{UPDATE} Updated command " + command.getName() + " on all guilds for bot " + bot.getName());
        }, ex -> {
            DiSky.debug("{UPDATE} Failed to update command " + command.getName() + " on all guilds for bot " + bot.getName());
            DiSky.debug("We'll register it again instead (Error: " + ex.getMessage() + ")");

            registerGlobalCommand(command, bot);
        });
    }

    public void shutdown() {
        bot.getInstance().removeEventListener(this);
        registeredCommands.forEach(registeredCommand -> {
            try {
                if (registeredCommand.getGuildId() == null) {
                    bot.getInstance().deleteCommandById(registeredCommand.getCommandId()).complete();
                } else {
                    final Guild guild = bot.getInstance().getGuildById(registeredCommand.getGuildId());
                    if (guild == null) {
                        DiSky.debug("Guild " + registeredCommand.getGuildId() + " is not available, skipping command deletion");
                        return;
                    }
                    guild.deleteCommandById(registeredCommand.getCommandId()).complete();
                }
                DiSky.debug("{DELETE} Deleted command " + registeredCommand.getName() + " on guild " + registeredCommand.getGuildId() + " for bot " + bot.getName());
            } catch (Exception ex) {
                DiSky.debug("{DELETE} Failed to delete command " + registeredCommand.getName() + " on guild " + registeredCommand.getGuildId() + " for bot " + bot.getName() + " (already deleted?): " + ex.getMessage());
            }
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
                if (parsedArgument.isAutoCompletion())
                    optionData.setAutoComplete(true);

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

    // ---------------------------------------------------------------------------------------------

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.isGlobalCommand()) {
            final RegisteredCommand registeredCommand = findCommand(event.getName(), event.getGuild().getId());
            if (registeredCommand == null) {
                DiSky.debug("Received command " + event.getName() + " but it's not registered on guild " + event.getGuild().getId() + " for bot " + event.getJDA().getSelfUser().getGlobalName());
                return;
            }

            tryExecute(registeredCommand, event);
        } else {
            final RegisteredCommand registeredCommand = findCommand(event.getName());
            if (registeredCommand == null) {
                DiSky.debug("Received command " + event.getName() + " but it's not registered on all guilds for bot " + event.getJDA().getSelfUser().getGlobalName());
                return;
            }

            tryExecute(registeredCommand, event);
        }
    }

    private void tryExecute(RegisteredCommand command, SlashCommandInteractionEvent event) {

        SkriptUtils.sync(() -> {
            // cooldown
            if (command.getParsedCommand().hasCooldown()) {
                if (command.isInCooldown(event.getUser())) {
                    if (command.getParsedCommand().getOnCooldown() != null) {
                        final OnCooldownEvent.BukkitCooldownEvent bukkitEvent =
                                new OnCooldownEvent.BukkitCooldownEvent(new OnCooldownEvent(),
                                        command.getCooldown(event.getUser()) - System.currentTimeMillis());
                        bukkitEvent.setJDAEvent(event);
                        command.getParsedCommand().prepareArguments(event.getOptions());
                        command.getParsedCommand().getOnCooldown().execute(bukkitEvent);

                        if (!bukkitEvent.isCancelled())
                            return;
                    }
                }

                command.setCooldown(event.getUser());
            }

            // "real" execution
            command.getParsedCommand().prepareArguments(event.getOptions());
            final Trigger trigger = command.getParsedCommand().getTrigger();
            final SlashCommandReceiveEvent.BukkitSlashCommandReceiveEvent bukkitEvent = new SlashCommandReceiveEvent.BukkitSlashCommandReceiveEvent(new SlashCommandReceiveEvent());
            bukkitEvent.setJDAEvent(event);

            trigger.execute(bukkitEvent);
        });
    }

    @Override
    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event) {
        if (!event.isGlobalCommand()) {
            final RegisteredCommand registeredCommand = findCommand(event.getName(), event.getGuild().getId());
            if (registeredCommand == null) {
                DiSky.debug("Received command " + event.getName() + " but it's not registered on guild " + event.getGuild().getId() + " for bot " + event.getJDA().getSelfUser().getGlobalName());
                return;
            }
            final String focusedArgument = event.getFocusedOption().getName();
            final Trigger trigger = registeredCommand.getParsedCommand().getArguments()
                            .stream()
                            .filter(parsedArgument -> parsedArgument.getName().equals(focusedArgument))
                            .findFirst()
                            .map(ParsedArgument::getOnCompletionRequest)
                            .orElse(null);
            if (trigger == null) {
                DiSky.debug("Received command " + event.getName() + " but no completion trigger for argument " + focusedArgument);
                return;
            }

            registeredCommand.getParsedCommand().prepareArguments(event.getOptions());
            final SlashCompletionEvent.BukkitSlashCompletionEvent bukkitEvent =
                    new SlashCompletionEvent.BukkitSlashCompletionEvent(new SlashCompletionEvent());
            bukkitEvent.setJDAEvent(event);

            trigger.execute(bukkitEvent);
        } else {
            final RegisteredCommand registeredCommand = findCommand(event.getName());
            if (registeredCommand == null) {
                DiSky.debug("Received command " + event.getName() + " but it's not registered on all guilds for bot " + event.getJDA().getSelfUser().getGlobalName());
                return;
            }

            final String focusedArgument = event.getFocusedOption().getName();
            final Trigger trigger = registeredCommand.getParsedCommand().getArguments()
                    .stream()
                    .filter(parsedArgument -> parsedArgument.getName().equals(focusedArgument))
                    .findFirst()
                    .map(ParsedArgument::getOnCompletionRequest)
                    .orElse(null);

            if (trigger == null) {
                DiSky.debug("Received command " + event.getName() + " but no completion trigger for argument " + focusedArgument);
                return;
            }

            registeredCommand.getParsedCommand().prepareArguments(event.getOptions());
            final SlashCompletionEvent.BukkitSlashCompletionEvent bukkitEvent =
                    new SlashCompletionEvent.BukkitSlashCompletionEvent(new SlashCompletionEvent());
            bukkitEvent.setJDAEvent(event);

            trigger.execute(bukkitEvent);
        }
    }

    @Override
    public void onReady(ReadyEvent event) {
        readyGlobal = true;
        DiSky.debug("Bot " + bot.getName() + " is ready, registering commands (" + waitingGlobalCommands.size() + ")");
        waitingGlobalCommands.forEach(Runnable::run);
        waitingGlobalCommands.clear();
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
