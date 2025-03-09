package net.itsthesky.disky.elements.structures.slash;

import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.itsthesky.disky.DiSky;
import net.itsthesky.disky.core.Bot;
import net.itsthesky.disky.elements.structures.context.ParsedContextCommand;
import net.itsthesky.disky.elements.structures.slash.models.ParsedCommand;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class BotReadyWaiter extends ListenerAdapter {

    public final static Map<String, List<ParsedCommand>> WaitingCommands = new HashMap<>();
    public static final Map<String, List<ParsedContextCommand>> WaitingContextCommands = new HashMap<>();

    public static void onBotLoaded(Bot bot) {
        // Add the listener to the bot
        bot.getInstance().addEventListener(new BotReadyWaiter(bot.getName()));

        // Check immediately in case the bot is already ready
        if (bot.getInstance().getStatus() == net.dv8tion.jda.api.JDA.Status.CONNECTED) {
            DiSky.debug("Bot " + bot.getName() + " is already connected, registering commands immediately");
            checkCommands(bot);
            checkContextCommands(bot);
        }
    }

    private final String botName;

    private BotReadyWaiter(String botName) {
        this.botName = botName;
    }

    @Override
    public void onReady(ReadyEvent event) {
        DiSky.debug("Bot " + botName + " is now READY, processing waiting commands");
        Bot bot = DiSky.getManager().getBotByName(botName);
        if (bot != null) {
            checkCommands(bot);
            checkContextCommands(bot);
        }
    }

    @Override
    public void onGuildReady(GuildReadyEvent event) {
        String guildId = event.getGuild().getId();
        DiSky.debug("Guild " + guildId + " is now READY for bot " + botName);
        Bot bot = DiSky.getManager().getBotByName(botName);

        if (bot != null) {
            bot.getSlashManager().markGuildAsReady(guildId);
            bot.getContextManager().markGuildAsReady(guildId);
        }
    }

    private static void checkCommands(Bot bot) {
        final var commands = WaitingCommands.get(bot.getName());
        if (commands == null || commands.isEmpty())
            return;

        DiSky.debug("Registering " + commands.size() + " slash commands for bot " + bot.getName());
        final var slashManager = bot.getSlashManager();
        commands.forEach(slashManager::registerCommand);
        commands.clear(); // Clear after registering to avoid duplicates
    }

    private static void checkContextCommands(Bot bot) {
        final var commands = WaitingContextCommands.get(bot.getName());
        if (commands == null || commands.isEmpty())
            return;

        DiSky.debug("Registering " + commands.size() + " context commands for bot " + bot.getName());
        final var contextManager = bot.getContextManager();
        commands.forEach(contextManager::registerCommand);
        commands.clear(); // Clear after registering to avoid duplicates
    }
}