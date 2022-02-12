package info.itsthesky.disky.managers;

import com.google.common.collect.Sets;
import info.itsthesky.disky.core.Bot;
import info.itsthesky.disky.api.skript.ErrorHandler;
import info.itsthesky.disky.core.Utils;
import net.dv8tion.jda.api.JDA;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

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

    public void execute(Consumer<Bot> consumer) {
        bots.forEach(consumer);
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

    public <T> @Nullable T searchIfAnyPresent(Function<Bot, T> function) {
        if (findAny() == null)
            return null;
        return function.apply(findAny());
    }

    public @Nullable Bot findAny() {
        return getBots()
                .stream()
                .findAny()
                .orElse(null);
    }

	public final String getJDAName(final JDA core) {
        return bots
                .stream()
                .filter(bot -> bot.coreIsEquals(core))
                .map(Bot::getName)
                .findAny()
                .orElse(null);
	}
}
