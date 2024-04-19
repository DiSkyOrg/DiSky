package info.itsthesky.disky.structures.bot;

import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.Trigger;
import ch.njol.skript.lang.VariableString;
import ch.njol.skript.lang.util.SimpleEvent;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.api.events.SimpleDiSkyEvent;
import info.itsthesky.disky.core.Bot;
import info.itsthesky.disky.core.BotOptions;
import info.itsthesky.disky.core.SkriptUtils;
import info.itsthesky.disky.elements.events.bots.BotStopEvent;
import info.itsthesky.disky.elements.events.bots.GuildReadyEvent;
import info.itsthesky.disky.elements.events.bots.ReadyEvent;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.Compression;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;
import org.skriptlang.skript.lang.entry.KeyValueEntryData;
import org.skriptlang.skript.lang.entry.util.LiteralEntryData;
import org.skriptlang.skript.lang.entry.util.VariableStringEntryData;
import org.skriptlang.skript.lang.structure.Structure;

import java.util.*;
import java.util.regex.Pattern;

public class StructBot extends Structure {

	public static final GatewayIntent[] DefaultIntents = new GatewayIntent[] {
			GatewayIntent.GUILD_MODERATION,
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

        static {
		Skript.registerStructure(
				StructBot.class,
				EntryValidator.builder()
						.addEntryData(new VariableStringEntryData("token", null, false))
						.addEntryData(new KeyValueEntryData<GatewayIntent[]>("intents", DefaultIntents, false) {
							private final Pattern pattern = Pattern.compile("\\s*,\\s*/?");

							@Override
							protected GatewayIntent[] getValue(@NotNull String value) {
								if (value.equalsIgnoreCase("default intents"))
									return DefaultIntents;

								return Arrays.stream(pattern.split(value))
										.map(str -> {
											try {
												return GatewayIntent.valueOf(str.toUpperCase().replace(" ", "_"));
											} catch (Exception ex) {
												Skript.error("The intent '" + str + "' is not valid! Use one of the following: " + Arrays.toString(GatewayIntent.values()));
												return null;
											}
										})
										.filter(Objects::nonNull)
										.toArray(GatewayIntent[]::new);
							}
						})

						//.addEntryData(new LiteralEntryData<>("compression", Compression.ZLIB.name(), true, String.class))
						.addEntryData(new KeyValueEntryData<Compression>("compression", Compression.ZLIB, true) {
							@Override
							protected Compression getValue(@NotNull String value) {
								try {
									return Compression.valueOf(value.toUpperCase().replace(" ", "_"));
								} catch (Exception ex) {
									Skript.error("The compression type '" + value + "' is not valid! Use one of the following: " + Arrays.toString(Compression.values()));
									return Compression.ZLIB;
								}
							}
						})

						.addEntryData(new KeyValueEntryData<CacheFlag[]>("cache flags", new CacheFlag[0], true) {
							private final Pattern pattern = Pattern.compile("\\s*,\\s*/?");

							@Override
							protected CacheFlag[] getValue(@NotNull String value) {
								if (value.equalsIgnoreCase("default flags"))
									return new CacheFlag[0];

								return Arrays.stream(pattern.split(value))
										.map(str -> {
											try {
												return CacheFlag.valueOf(str.toUpperCase().replace(" ", "_"));
											} catch (Exception ex) {
												Skript.error("The cache flag '" + str + "' is not valid! Use one of the following: " + Arrays.toString(CacheFlag.values()));
												return null;
											}
										})
										.filter(Objects::nonNull)
										.toArray(CacheFlag[]::new);
							}
						})

						//.addEntryData(new LiteralEntryData<>("policy", "DEFAULT", true, String.class))
						.addEntryData(new KeyValueEntryData<MemberCachePolicy>("policy", MemberCachePolicy.DEFAULT, true) {
							@Override
							protected MemberCachePolicy getValue(@NotNull String value) {
								try {
									return (MemberCachePolicy) MemberCachePolicy.class
											.getDeclaredField(value
													.toUpperCase(Locale.ROOT)
													.replaceAll(" ", "_"))
											.get(null);
								} catch (Exception ex) {
									Skript.error("The member cache policy '" + value + "' is not valid!");
									return MemberCachePolicy.DEFAULT;
								}
							}
						})

						.addEntryData(new LiteralEntryData<>("auto reconnect", true, true, Boolean.class))
						.addEntryData(new LiteralEntryData<>("force reload", false, true, Boolean.class))

						.addSection("on ready", true)
						.addSection("on guild ready", true)
						.addSection("on shutdown", true)

						.unexpectedEntryMessage(key ->
								"Unexpected entry '" + key + "'. Check that it's spelled correctly, and ensure that you have put all code into a trigger."
						)

						.build(),
				"define [the] [new] bot (with name|named) %string%"
		);
	}

	private String name;
	private EntryContainer container;

	private @Nullable BotOptions options;

	@Override
	public boolean init(Literal<?>[] args, int matchedPattern, @NotNull SkriptParser.ParseResult parseResult, @NotNull EntryContainer entryContainer) {
		this.name = ((Literal<String>) args[0]).getSingle();
		this.container = entryContainer;
		return true;
	}

	@Override
	@SuppressWarnings("ConstantConditions")
	public boolean load() {
		final VariableString token = container.getOptional("token", VariableString.class, true);
		final SectionNode readySection = container.getOptional("on ready", SectionNode.class, true);
		final SectionNode guildReadySection = container.getOptional("on guild ready", SectionNode.class, true);
		final SectionNode shutdownSection = container.getOptional("on shutdown", SectionNode.class, true);

		if (token == null) {
			Skript.error("The token of the bot '" + name + "' is not defined! You need to define it in order to use the bot!");
			return false;
		}

		@Nullable Trigger ready = null;
		@Nullable Trigger guildReady = null;
		@Nullable Trigger shutdown = null;

		if (readySection != null)
			ready = new Trigger(getParser().getCurrentScript(), "bot '" + name + "' ready section", new SimpleEvent(),
					SkriptUtils.loadCode(readySection, ReadyEvent.BukkitReadyEvent.class));
		if (guildReadySection != null)
			guildReady = new Trigger(getParser().getCurrentScript(), "bot '" + name + "' guild ready section", new SimpleEvent(),
					SkriptUtils.loadCode(guildReadySection, GuildReadyEvent.BukkitGuildReadyEvent.class));
		if (shutdownSection != null)
			shutdown = new Trigger(getParser().getCurrentScript(), "bot '" + name + "' shutdown section", new SimpleEvent(),
					SkriptUtils.loadCode(shutdownSection, BotStopEvent.BukkitShutdownEvent.class));

		options = new BotOptions();

		options.setToken(token.getSingle(new SimpleDiSkyEvent<>()));
		options.setName(name);

		options.setCompression(container.getOptional("compression", Compression.class, true));
		options.setFlags(container.getOptional("cache flags", CacheFlag[].class, true));
		options.setPolicy(container.getOptional("policy", MemberCachePolicy.class, true));

		options.setAutoReconnect(container.getOptional("auto reconnect", Boolean.class, true));
		options.setForceReload(container.getOptional("force reload", Boolean.class, true));

		options.setOnReady(ready == null ? new ArrayList<>() : Collections.singletonList(ready));
		options.setOnGuildReady(guildReady == null ? new ArrayList<>() : Collections.singletonList(guildReady));
		options.setOnShutdown(shutdown == null ? new ArrayList<>() : Collections.singletonList(shutdown));

		options.setIntents(container.getOptional("intents", GatewayIntent[].class, true));

		// -------------------------------------------------------------------------------------------------


		if (options == null)
			throw new IllegalStateException("The options of the bot '" + name + "' are null! This is a bug, please report it on the DiSky GitHub!");
		final BotOptions parsedEntity = options;

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
				if (event instanceof net.dv8tion.jda.api.events.session.ReadyEvent)
					parsedEntity.runReady((net.dv8tion.jda.api.events.session.ReadyEvent) event);
				else if (event instanceof net.dv8tion.jda.api.events.guild.GuildReadyEvent)
					parsedEntity.runGuildReady((net.dv8tion.jda.api.events.guild.GuildReadyEvent) event);
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

	protected MemberCachePolicy parse(String input) {
		try {
			return (MemberCachePolicy) MemberCachePolicy.class
					.getDeclaredField(input
							.toUpperCase(Locale.ROOT)
							.replaceAll(" ", "_"))
					.get(null);
		} catch (Exception ex) {
			return MemberCachePolicy.DEFAULT;
		}
	}

	@Override
	public @NotNull String toString(@Nullable Event e, boolean debug) {
		return "define bot named " + name;
	}
}
