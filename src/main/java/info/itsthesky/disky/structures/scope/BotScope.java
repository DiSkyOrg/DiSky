package info.itsthesky.disky.structures.scope;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.config.validate.SectionValidator;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.TriggerItem;
import info.itsthesky.disky.BotApplication;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.api.skript.BaseBukkitEvent;
import info.itsthesky.disky.api.skript.BaseScope;
import info.itsthesky.disky.api.skript.EasyElement;
import info.itsthesky.disky.core.Bot;
import info.itsthesky.disky.core.BotOptions;
import info.itsthesky.disky.core.SkriptUtils;
import info.itsthesky.disky.elements.events.bots.BotStopEvent.BukkitShutdownEvent;
import info.itsthesky.disky.elements.events.bots.GuildReadyEvent.BukkitGuildReadyEvent;
import info.itsthesky.disky.elements.events.bots.ReadyEvent.BukkitReadyEvent;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.Compression;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@SuppressWarnings("unused")
public class BotScope extends BaseScope<BotOptions> {

    public static final GatewayIntent[] defaults = new GatewayIntent[] {
            GatewayIntent.GUILD_BANS,
            GatewayIntent.GUILD_EMOJIS_AND_STICKERS,
            GatewayIntent.GUILD_WEBHOOKS,
            GatewayIntent.GUILD_INVITES,
            GatewayIntent.GUILD_VOICE_STATES,
            GatewayIntent.GUILD_MESSAGE_REACTIONS,
            GatewayIntent.GUILD_MESSAGE_TYPING,
            GatewayIntent.DIRECT_MESSAGES,
            GatewayIntent.GUILD_MESSAGES,
            GatewayIntent.GUILD_MEMBERS,
            GatewayIntent.GUILD_PRESENCES,
            GatewayIntent.MESSAGE_CONTENT,
            GatewayIntent.SCHEDULED_EVENTS
    };

    public static final String[] defaultsStr = Arrays.stream(defaults).map(GatewayIntent::name).toArray(String[]::new);

    public static final SectionValidator validator = new SectionValidator()
            .addEntry("token", false)
            .addEntry("intents", true)
            .addEntry("cache flags", true)
            .addEntry("compression", true)
            .addEntry("policy", true)
            .addEntry("auto reconnect", true)
            .addEntry("force reload", true)
            .addSection("on ready", true)
            .addSection("application", true)
            .addSection("on guild ready", true)
            .addSection("on shutdown", true);

    private static final SectionValidator appValidator = new SectionValidator()
            .addEntry("application id", false)
            .addEntry("application secret", false);

    public static void register() {
        Skript.registerEvent("Bot Creation Scope",
                BotScope.class, BotScopeEvent.class,
                "define [the] [new] bot (with name|named) %string%");
    }

    private @Nullable String name;

    @Override
    public void init(Literal<?> @NotNull [] args, int matchedPattern, @NotNull SkriptParser.ParseResult parseResult, SectionNode node) {
        name = EasyElement.parseSingle((Literal<String>) args[0], null);
    }

    @Override
    public @Nullable BotOptions parse(@NotNull SectionNode node) {
        if (name == null)
            return null;
        node.convertToEntries(0);
        if (!validator.validate(node))
            return null;

        /* Bot's name & initialize options */
        final BotOptions options = new BotOptions();
        options.setName(name);

        /* Bot's Token */
        final String token = parseEntry(node, "token");
        if (token.isEmpty())
            return error("The token cannot be empty.");
        options.setToken(token);

        final @Nullable boolean forceReload;
        @Nullable boolean forceReload1;
        try {
            forceReload1 = Boolean.parseBoolean(parseEntry(node, "force reload", "false"));
        } catch (Throwable ex) {
            forceReload1 = false;
            Skript.error("Unknown boolean value for 'force reload' entry: " + parseEntry(node, "force reload", "false"));
        }
        forceReload = forceReload1;
        options.setForceReload(forceReload);

        final String rawPolicy = parseEntry(node, "policy");
        final @Nullable MemberCachePolicy policy;
        if (!rawPolicy.isEmpty()) {
            try {
                policy = (MemberCachePolicy) MemberCachePolicy.class
                        .getDeclaredField(rawPolicy
                                .toUpperCase(Locale.ROOT)
                                .replaceAll(" ", "_"))
                        .get(null);
            } catch (Exception ex) {
                return error("Unable to parse member cache policy for input: " + rawPolicy);
            }
        } else {
            policy = MemberCachePolicy.DEFAULT;
        }
        options.setPolicy(policy);

        final String rawCompression = parseEntry(node, "compression");
        final @Nullable Compression compression;
        if (!rawCompression.isEmpty()) {
            try {
                compression = Compression.valueOf(rawCompression
                        .toUpperCase(Locale.ROOT)
                        .replaceAll(" ", "_"));
            } catch (Exception ex) {
                return error("Unable to parse gateway compression for input: " + rawCompression);
            }
        } else {
            compression = Compression.ZLIB;
        }
        options.setCompression(compression);

        /* Auto Reconnect */
        final String rawAutoReco = parseEntry(node, "auto reconnect", "true");
        final boolean autoReconnect;
        try {
            autoReconnect = Boolean.parseBoolean(rawAutoReco);
        } catch (Exception ex) {
            return error("Unable to parse auto reconnect entry, must be a valid boolean but got: " + rawAutoReco);
        }
        options.setAutoReconnect(autoReconnect);

        /* Intents */

        final String inputIntent = parseEntry(node, "intents", "");
        final boolean defaultIntents = inputIntent.equalsIgnoreCase("default intents");

        final String[] unparsedIntents = inputIntent.split(listPattern);

        final List<GatewayIntent> intents;
        if (defaultIntents) {
            intents = Arrays.asList(defaults);
        } else {
            intents = new ArrayList<>();
            for (String intent : unparsedIntents) {
                try {
                    intents.add(GatewayIntent.valueOf(intent.toUpperCase(Locale.ROOT).replaceAll(" ", "_")));
                } catch (Exception ex) {
                    return error("Unknown gateway intent: " + intent);
                }
            }
        }
        options.setIntents(intents.toArray(new GatewayIntent[0]));
        final List<CacheFlag> flags = new ArrayList<>();

        final String inputFlag = parseEntry(node, "cache flags", "");
        if (!inputFlag.isEmpty()) {
            if (inputFlag.equalsIgnoreCase("all")) {
                flags.addAll(Arrays.asList(CacheFlag.values()));
            } else {
                final String[] unparsedFlags = inputFlag.split(listPattern);

                for (String flag : unparsedFlags) {
                    try {
                        flags.add(CacheFlag.valueOf(flag.toUpperCase(Locale.ROOT).replaceAll(" ", "_")));
                    } catch (Exception ex) {
                        return error("Unknown cache flag: " + flag);
                    }
                }
            }
        }
        options.setFlags(flags.toArray(new CacheFlag[0]));

        final List<TriggerItem> onReady = node.get("on ready") == null ? new ArrayList<>() : SkriptUtils.loadCode((SectionNode) node.get("on ready"), BukkitReadyEvent.class);
        final List<TriggerItem> onGuildReady = node.get("on guild ready") == null ? new ArrayList<>() : SkriptUtils.loadCode((SectionNode) node.get("on guild ready"), BukkitGuildReadyEvent.class);
        final List<TriggerItem> onBotShutdown = node.get("on shutdown") == null ? new ArrayList<>() : SkriptUtils.loadCode((SectionNode) node.get("on shutdown"), BukkitShutdownEvent.class);
        options.setOnReady(onReady);
        options.setOnGuildReady(onGuildReady);
        options.setOnShutdown(onBotShutdown);

        final SectionNode appNode = (SectionNode) node.get("application");
        final @Nullable BotApplication app = buildApp(appNode, name);
        options.setApplication(app);

        return options;
    }

    public static @Nullable BotApplication buildApp(SectionNode node, String name) {
        if (node == null)
            return null;

        node.convertToEntries(0);
        if (!appValidator.validate(node))
            return null;

        final String appID = ScriptLoader.replaceOptions(node.get("application id", ""));
        final String appSecret = ScriptLoader.replaceOptions(node.get("application secret", ""));

        return new BotApplication(name, appID, appSecret);
    }

    @Override
    public boolean validate(@Nullable BotOptions parsedEntity) {
        if (parsedEntity == null)
            return false;
        final String name = parsedEntity.getName();
        if (DiSky.getManager().exist(name)) {
            final Bot bot = DiSky.getManager().fromName(name);
            if (bot.isForceReload())
                bot.getInstance().shutdownNow();
            else
                return true;
        }
        final JDA jda;
        try {

            final EventListener listener = event -> {
                if (event instanceof ReadyEvent)
                    parsedEntity.runReady((ReadyEvent) event);
                else if (event instanceof GuildReadyEvent)
                    parsedEntity.runGuildReady((GuildReadyEvent) event);
            };

            jda = parsedEntity
                    .toBuilder()
                    .addEventListeners(listener)
                    .build();
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        DiSky.getManager().addBot(parsedEntity.asBot(jda, parsedEntity));
        return true;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "bot creation scope";
    }

    public static class BotScopeEvent extends BaseBukkitEvent { }
}
