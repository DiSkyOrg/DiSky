package info.itsthesky.disky.managers;

import com.google.common.collect.Sets;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.api.events.EventListener;
import info.itsthesky.disky.api.skript.ErrorHandler;
import info.itsthesky.disky.core.Bot;
import info.itsthesky.disky.core.MemberRemoveEventListener;
import info.itsthesky.disky.core.ReactionListener;
import info.itsthesky.disky.elements.commands.CommandListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.session.ShutdownEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Class that will store and manage every loaded bots.
 */
public class BotManager {

    private final LinkedList<EventListener<?>> queuedListeners = new LinkedList<>();
    private boolean anyBotEnabled = false;
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
        configureBot(bot);
        anyBotEnabled = true;
        this.bots.add(bot);
    }

    private void configureBot(Bot bot) {
        bot.getInstance().addEventListener(new CommandListener());
        bot.getInstance().addEventListener(new ReactionListener());
        bot.getInstance().addEventListener(new MessageManager(bot));
        bot.getInstance().addEventListener(new MemberRemoveEventListener());
        bot.getInstance().addEventListener(queuedListeners.toArray());
    }

    public void shutdown() {
        // TODO: 04/03/2023 Better way to shutdown bots (JDA's awaitShutdown implementation)
        this.bots.forEach(bot -> {
            DiSky.debug("1. Running shut down bot " + bot.getName());
            bot.getOptions().runShutdown(new ShutdownEvent(bot.getInstance(), OffsetDateTime.now(), 0));
            DiSky.debug("2. Finished shut down bot " + bot.getName());
        });
        this.bots.forEach(bot -> {
            DiSky.debug("3. Shutting down bot " + bot.getName());
            bot.getInstance().shutdown();
        });
    }

    public boolean exist(@Nullable String name) {
        return name != null && bots.stream().anyMatch(bot -> bot.getName().equals(name));
    }

    public void execute(Consumer<Bot> consumer) {
        bots.forEach(consumer);
    }

    public ErrorHandler errorHandler() {
        return new DiSkyErrorHandler();
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

	public Bot fromJDA(JDA core) {
        return bots.stream()
                .filter(bot -> bot.getInstance().equals(core))
                .findAny()
                .orElse(null);
	}

    public void removeBot(Bot bot) {
        final Set<Bot> set = new HashSet<>(bots).stream()
                .filter(bot1 -> !bot1.getName().equalsIgnoreCase(bot.getName()))
                .collect(Collectors.toSet());
        bots.clear();
        bots.addAll(set);
    }

    public void registerGlobalListener(EventListener<?> listener) {
        if (anyBotEnabled)
            execute(bot -> bot.getInstance().addEventListener(listener));
        else
            queuedListeners.add(listener);
    }

    public Bot getBotByName(String name) {
        return bots.stream().filter(bot -> bot.getName().equalsIgnoreCase(name)).findAny().orElse(null);
    }
}
