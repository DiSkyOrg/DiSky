package info.itsthesky.disky;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.Compression;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import java.util.Arrays;

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
    private boolean autoReconnect;
    /**
     * Policy, access & cache information
     */
    private GatewayIntent[] intents;
    private CacheFlag[] flags;
    private Compression compression;
    private MemberCachePolicy policy;

    public BotOptions() {}

    public JDABuilder toBuilder() {
        return JDABuilder
                .createDefault(getToken())
                .setCompression(compression)
                .setAutoReconnect(autoReconnect)
                .setEnabledIntents(Arrays.asList(getIntents()))
                .setMemberCachePolicy(policy);
    }

    public String getName() {
        return name;
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
}
