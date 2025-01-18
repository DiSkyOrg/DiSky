package net.itsthesky.disky.core;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.util.Timespan;
import net.itsthesky.disky.BotApplication;
import net.itsthesky.disky.DiSky;
import net.itsthesky.disky.api.events.SimpleDiSkyEvent;
import net.itsthesky.disky.elements.structures.context.ContextCommandManager;
import net.itsthesky.disky.elements.structures.slash.BotReadyWaiter;
import net.itsthesky.disky.elements.structures.slash.SlashManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.Channel;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Class that will handle every information about a bot.
 * @author ItsTheSky
 */
// TODO: 29/12/2021 Maybe use records here, but it's only for Java 14+
public class Bot {

    private final long startedTime;
    private final String name;
    private final JDA instance;
    private final @Nullable BotApplication application;
    private final boolean forceReload;
    private final BotOptions options;

    private final SlashManager slashManager;
    private final ContextCommandManager contextManager;

    public Bot(String name, JDA instance, BotOptions options, @Nullable BotApplication application, boolean forceReload) {
        this.name = name;
        this.application = application;
        this.instance = instance;
        this.forceReload = forceReload;
        this.startedTime = System.currentTimeMillis();
        this.options = options;

        this.slashManager = SlashManager.getManager(this);
        this.contextManager = ContextCommandManager.getManager(this);

        BotReadyWaiter.onBotLoaded(this);
    }

    public String getName() {
        return name;
    }

    public boolean isForceReload() {
        return forceReload;
    }

    public JDA getInstance() {
        return instance;
    }

    public @Nullable BotApplication getApplication() {
        return application;
    }

    @SuppressWarnings("unchecked")
    public <T> T findSimilarEntity(T original) {
        if (original instanceof Guild)
            return (T) getInstance().getGuildById(((Guild) original).getId());
        if (original instanceof User)
            return (T) getInstance().getUserById(((User) original).getId());
        if (original instanceof Role)
            return (T) getInstance().getRoleById(((Role) original).getId());
        return original;
    }

    public <C extends Channel> C findMessageChannel(C original) {
        return (C) instance.getChannelById(original.getClass(), original.getId());
    }

	public boolean coreIsEquals(JDA core) {
        return getInstance().getSelfUser().getId().equals(core.getSelfUser().getId());
	}

	public Timespan getUptime() {
        return new Timespan(System.currentTimeMillis() - startedTime);
	}

    public void shutdown(boolean force) {
        if (force)
            getInstance().shutdownNow();
        else
            getInstance().shutdown();
        DiSky.getManager().removeBot(this);
    }

    public String getDiscordName() {
        return getInstance().getSelfUser().getEffectiveName();
    }

    public BotOptions getOptions() {
        return options;
    }

    public static @Nullable Bot any() {
        return DiSky.getManager().findAny();
    }

    public static @NotNull Bot fromEvent(Event event) {
        if (event instanceof SimpleDiSkyEvent)
            return byJDA(((SimpleDiSkyEvent) event).getJDAEvent().getJDA());
        return any();
    }

    public static @NotNull Bot fromContext(@Nullable Expression<Bot> expr, Event event) {
        final Bot first = expr == null ? null : expr.getSingle(event);
        return first == null ? fromEvent(event) : first;
    }

    public static @Nullable Bot byJDA(JDA instance) {
        return DiSky.getManager().fromJDA(instance);
    }

    public SlashManager getSlashManager() {
        return slashManager;
    }

    public ContextCommandManager getContextManager() {
        return contextManager;
    }
}
