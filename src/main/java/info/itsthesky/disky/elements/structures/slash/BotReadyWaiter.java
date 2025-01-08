package info.itsthesky.disky.elements.structures.slash;

import info.itsthesky.disky.core.Bot;
import info.itsthesky.disky.elements.structures.context.ParsedContextCommand;
import info.itsthesky.disky.elements.structures.slash.models.ParsedCommand;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class BotReadyWaiter {

    public final static Map<String, List<ParsedCommand>> WaitingCommands = new HashMap<>();
    public static final Map<String, List<ParsedContextCommand>> WaitingContextCommands = new HashMap<>();

    public static void onBotLoaded(Bot bot) {
        checkCommands(bot);
        checkContextCommands(bot);
    }

    private static void checkCommands(Bot bot) {
        final var commands = WaitingCommands.get(bot.getName());
        if (commands == null || commands.isEmpty())
            return;

        final var slashManager = bot.getSlashManager();
        commands.forEach(slashManager::registerCommand);
    }

    private static void checkContextCommands(Bot bot) {
        final var commands = WaitingContextCommands.get(bot.getName());
        if (commands == null || commands.isEmpty())
            return;

        final var contextManager = bot.getContextManager();
        commands.forEach(contextManager::registerCommand);
    }

}
