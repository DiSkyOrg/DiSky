package info.itsthesky.disky.structures.structure;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.Trigger;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.core.Bot;
import info.itsthesky.disky.core.BotOptions;
import info.itsthesky.disky.elements.events.bots.GuildReadyEvent;
import info.itsthesky.disky.elements.events.bots.ReadyEvent;
import info.itsthesky.disky.structures.scope.BotScope;
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
import org.skriptlang.skript.lang.entry.util.LiteralEntryData;
import org.skriptlang.skript.lang.structure.Structure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.stream.Stream;

public class StructBot extends Structure {

	public static void register() {
		Skript.registerStructure(
				StructBot.class,
				EntryValidator.builder()
						.addEntryData(new LiteralEntryData<>("token", null, false, String.class))
						.addEntryData(new LiteralEntryData<>("intents", BotScope.defaultsStr, false, String[].class))

						.addEntryData(new LiteralEntryData<>("compression", Compression.ZLIB.name(), true, String.class))
						.addEntryData(new LiteralEntryData<>("cache flags", new String[0], true, String[].class))
						.addEntryData(new LiteralEntryData<>("policy", "DEFAULT", true, String.class))
						.addEntryData(new LiteralEntryData<>("auto reconnect", true, true, Boolean.class))
						.addEntryData(new LiteralEntryData<>("force reload", false, true, Boolean.class))

						.addEntryData(new LiteralEntryData<>("on ready", null, true, ReadyEvent.BukkitReadyEvent.class))
						.addEntryData(new LiteralEntryData<>("on guild ready", null, true, GuildReadyEvent.BukkitGuildReadyEvent.class))
						.addSection("on ready", true)
						.addSection("on guild ready", true)
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
	public boolean preLoad() {
		final String token = container.getOptional("token", String.class, true);
		final Trigger ready = container.getOptional("on ready", Trigger.class, true);
		final Trigger guildReady = container.getOptional("on guild ready", Trigger.class, true);

		if (token == null) {
			Skript.error("The token of the bot '" + name + "' is not defined! You need to define it in order to use the bot!");
			return false;
		}

		options = new BotOptions();

		options.setToken(token);
		options.setName(name);

		options.setCompression(Compression.valueOf(container.getOptional("compression", String.class, true)
				.toUpperCase().replace(" ", "_")));
		options.setFlags(
				Stream.of(container.getOptional("cache flags", String[].class, true))
						.map(str -> CacheFlag.valueOf(str.toUpperCase().replace(" ", "_")))
						.toArray(CacheFlag[]::new)
		);
		options.setPolicy(parse(container.getOptional("policy", String.class, true)));

		options.setAutoReconnect(container.getOptional("auto reconnect", Boolean.class, true));

		options.setOnReady(ready == null ? new ArrayList<>() : Collections.singletonList(ready));
		options.setOnGuildReady(guildReady == null ? new ArrayList<>() : Collections.singletonList(guildReady));

		options.setIntents(Stream.of(container.getOptional("intents", String[].class, true))
				.map(str -> GatewayIntent.valueOf(str.toUpperCase().replace(" ", "_")))
				.toArray(GatewayIntent[]::new));

		return true;
	}

	@Override
	@SuppressWarnings("ConstantConditions")
	public boolean load() {
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
