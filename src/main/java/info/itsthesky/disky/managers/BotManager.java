package info.itsthesky.disky.managers;

import com.google.common.collect.Sets;
import info.itsthesky.disky.core.Bot;
import info.itsthesky.disky.api.skript.ErrorHandler;
import info.itsthesky.disky.core.Utils;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * Class that will store and manage every loaded bots.
 */
public class BotManager {

    private final JavaPlugin plugin;

    public BotManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    private final Set<Bot> bots = Sets.newConcurrentHashSet();

    public Set<Bot> getBots() {
        return bots;
    }

    public void addBot(Bot bot) {
        this.bots.removeIf(b -> b.getName().equals(bot.getName()));
        this.bots.add(bot);
    }

    public void shutdown() {
        // TODO: 29/12/2021 Make the shutdown better, it's blocking the thread currently but else it throw an error since Bukkit disable the class before the bot is offline.
        this.bots.forEach(bot -> bot.getInstance().shutdownNow());
    }

    public boolean exist(@Nullable String name) {
        return name != null && bots.stream().anyMatch(bot -> bot.getName().equals(name));
    }

    public ErrorHandler errorHandler() {
        return message -> plugin
                .getServer()
                .getConsoleSender()
                .sendMessage(Utils.colored("&4[&c!&4] &c" + message));
    }

    public @Nullable Bot fromName(String name) {
        return bots
                .stream()
                .filter(bot -> bot.getName().equals(name))
                .findAny()
                .orElse(null);
    }
}
