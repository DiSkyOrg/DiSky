package info.itsthesky.disky.core;

import ch.njol.skript.lang.Trigger;
import ch.njol.skript.lang.TriggerItem;
import info.itsthesky.disky.BotApplication;
import info.itsthesky.disky.elements.events.bots.GuildReadyEvent.BukkitGuildReadyEvent;
import info.itsthesky.disky.elements.events.bots.BotStopEvent;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.ShutdownEvent;
import net.dv8tion.jda.api.events.StatusChangeEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.Compression;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

/**
 * Class that store every option defined by the user before its actual {@link Bot} creation.
 */
@SuppressWarnings("unused")
public class BotOptions {

    /**
     * Base information
     */
    private String name;
    private String token;
    private @Nullable BotApplication application;
    private boolean forceReload;
    private boolean autoReconnect;
    /**
     * Policy, access & cache information
     */
    private GatewayIntent[] intents;
    private CacheFlag[] flags;
    private Compression compression;
    private MemberCachePolicy policy;
    /**
     * Events & triggers
     */
    private List<TriggerItem> onReady;
    private List<TriggerItem> onGuildReady;
    private List<TriggerItem> onShutdown;

    public BotOptions() {}

    public JDABuilder toBuilder() {
        return JDABuilder
                .createDefault(getToken())
                .setCompression(compression)
                .setAutoReconnect(autoReconnect)
                .disableCache(Arrays.asList(getFlags()))
                .setEnabledIntents(Arrays.asList(getIntents()))
                .setMemberCachePolicy(policy);
    }

    public void runReady(ReadyEvent event) {
        if (getOnReady().isEmpty())
            return;
        final info.itsthesky.disky.elements.events.bots.ReadyEvent.BukkitReadyEvent e = new info.itsthesky.disky.elements.events.bots.ReadyEvent.BukkitReadyEvent(new info.itsthesky.disky.elements.events.bots.ReadyEvent());
        e.setJDAEvent(event);
        TriggerItem.walk(getOnReady().get(0), e);
    }

    public void runGuildReady(GuildReadyEvent event) {
        if (getOnGuildReady().isEmpty())
            return;
        final BukkitGuildReadyEvent e = new BukkitGuildReadyEvent(new info.itsthesky.disky.elements.events.bots.GuildReadyEvent());
        e.setJDAEvent(event);
        TriggerItem.walk(getOnGuildReady().get(0), e);
    }

    public void runShutdown(ShutdownEvent event) {
        if (getOnShutdown().isEmpty())
            return;
        final BotStopEvent.BukkitShutdownEvent e = new BotStopEvent.BukkitShutdownEvent(new BotStopEvent());
        e.setJDAEvent(event);
        TriggerItem.walk(getOnGuildReady().get(0), e);
    }

    public String getName() {
        return name;
    }

    public List<TriggerItem> getOnShutdown() {
        return onShutdown;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public GatewayIntent[] getIntents() {
        return intents;
    }

    public void setIntents(GatewayIntent[] intents) {
        this.intents = intents;
    }

    public CacheFlag[] getFlags() {
        return flags;
    }

    public void setFlags(CacheFlag[] flags) {
        this.flags = flags;
    }

    public Compression getCompression() {
        return compression;
    }

    public void setCompression(Compression compression) {
        this.compression = compression;
    }

    public boolean isAutoReconnect() {
        return autoReconnect;
    }

    public void setAutoReconnect(boolean autoReconnect) {
        this.autoReconnect = autoReconnect;
    }

    public MemberCachePolicy getPolicy() {
        return policy;
    }

    public void setPolicy(MemberCachePolicy policy) {
        this.policy = policy;
    }

    public boolean forceReload() {
        return forceReload;
    }

    public void setForceReload(boolean forceReload) {
        this.forceReload = forceReload;
    }

    public @Nullable BotApplication getApplication() {
        return application;
    }

    public void setApplication(@Nullable BotApplication application) {
        this.application = application;
    }

    public List<TriggerItem> getOnReady() {
        return onReady;
    }

    public void setOnReady(List<TriggerItem> onReady) {
        this.onReady = onReady;
    }

    public List<TriggerItem> getOnGuildReady() {
        return onGuildReady;
    }

    public void setOnGuildReady(List<TriggerItem> onGuildReady) {
        this.onGuildReady = onGuildReady;
    }

    public void setOnShutdown(List<TriggerItem> onShutdown) {
        this.onShutdown = onShutdown;
    }

    public Bot asBot(JDA core, BotOptions options) {
        return new Bot(getName(), core, options, getApplication(), forceReload);
    }
}
