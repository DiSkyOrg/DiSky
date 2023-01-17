package info.itsthesky.disky.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.api.skript.WaiterEffect;
import info.itsthesky.disky.core.Bot;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("rawtypes")
@Name("Connect / Disconnect Bot")
@Description({"Connect or disconnect a bot to a specific audio channel (or disconnect it from the current one).",
		"The bot must have the required permissions to connect to the channel.",
		"If using the disconnect pattern, only the guild will be required."
})
@Examples({"connect bot \"bot_name\" to voice channel with id \"000\"",
		"disconnect from event-guild"})
@Since("4.9.0")
public class ConnectBot extends WaiterEffect {

	static {
		Skript.registerEffect(
				ConnectBot.class,
				"connect %bot% to [the] [(audio|voice)] [channel] %audiochannel%",
				"disconnect [[the] [bot] %bot%] from [the] [guild] %guild%"
		);
	}

	private Expression<Bot> exprBot;
	private boolean connect;

	private Expression<AudioChannel> exprAudioChannel;
	private Expression<Guild> exprGuild;

	@Override
	public boolean initEffect(Expression[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
		exprBot = (Expression<Bot>) expressions[0];
		connect = i == 0;

		if (connect)
			exprAudioChannel = (Expression<AudioChannel>) expressions[1];
		else
			exprGuild = (Expression<Guild>) expressions[1];

		return true;
	}

	@Override
	public void runEffect(Event e) {
		final Bot bot = parseSingle(exprBot, e);
		final Guild guild = parseSingle(exprGuild, e);
		final AudioChannel channel = parseSingle(exprAudioChannel, e);
		if (bot == null || (connect && channel == null) || (!connect && guild == null))
			return;

		final AudioChannel foundChannel = bot.getInstance().getChannelById(AudioChannel.class, channel.getId());
		if (foundChannel == null) {
			Skript.error("The audio channel with id " + channel.getId() + " is not found for bot " + bot.getName() + "!");
			restart();
			return;
		}

		if (connect) {
			foundChannel.getGuild().getAudioManager().openAudioConnection(foundChannel);
		} else {
			foundChannel.getGuild().getAudioManager().closeAudioConnection();
		}
	}

	@Override
	public @NotNull String toString(@Nullable Event e, boolean debug) {
		return (connect
				? "connect " + exprBot.toString(e, debug) + " to " + exprAudioChannel.toString(e, debug)
				: "disconnect " + exprBot.toString(e, debug) + " from " + exprGuild.toString(e, debug)
		);
	}
}
