package net.itsthesky.disky.core;

import ch.njol.skript.lang.TriggerItem;
import net.itsthesky.disky.BotApplication;
import net.itsthesky.disky.DiSky;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.events.session.ShutdownEvent;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.Compression;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.itsthesky.disky.elements.events.rework.BotEvents;
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
                .enableIntents(Arrays.asList(getIntents()))
                .enableCache(Arrays.asList(getFlags()))
                .setMemberCachePolicy(policy);
    }

    public void runReady(ReadyEvent event) {
        if (getOnReady().isEmpty())
            return;
        final var e = BotEvents.READY_EVENT.createBukkitInstance(event);
        TriggerItem.walk(getOnReady().get(0), e);
    }

    public void runGuildReady(GuildReadyEvent event) {
        if (getOnGuildReady().isEmpty())
            return;
        final var e = BotEvents.GUILD_READY_EVENT.createBukkitInstance(event);
        TriggerItem.walk(getOnGuildReady().get(0), e);
    }

    public void runShutdown(ShutdownEvent event) {
        if (getOnShutdown().isEmpty())
        {
            DiSky.debug("No shutdown event defined for bot " + getName());
            return;
        }
        final var e = BotEvents.SHUTDOWN_EVENT.createBukkitInstance(event);
        DiSky.debug("Running shutdown event for bot " + getName());
        TriggerItem.walk(getOnShutdown().get(0), e);
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

    public void setToken(String text) {
        while (text.contains("\"\""))
            text = text.replace("\"\"", "\"");

        if (text.startsWith("\"") && text.endsWith("\""))
            text = text.substring(1, text.length() - 1);

        this.token = text;
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
