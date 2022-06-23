package info.itsthesky.disky.elements.getters;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import info.itsthesky.disky.core.Bot;
import net.dv8tion.jda.api.entities.AudioChannel;
import org.jetbrains.annotations.NotNull;

@Name("Get Audio Channel")
@Description({"This is an utility expression.",
		"It will returns an Audio Channel out of the provided ID.",
		"It will returns either the voice or stage channel corresponding to the provided ID.",
		"This expression cannot be changed."})
@Examples("audio channel with id \"000\"")
@Since("4.0.0")
public class GetAudioChannel extends BaseGetterExpression<AudioChannel> {

	static {
		register(
				GetAudioChannel.class,
				AudioChannel.class,
				"audio channel"
		);
	}

	@Override
	protected AudioChannel get(String id, Bot bot) {
		final AudioChannel voice = bot.getInstance().getVoiceChannelById(id);
		return voice == null ? bot.getInstance().getStageChannelById(id) : voice;
	}

	@Override
	public String getCodeName() {
		return "audio channel";
	}

	@Override
	public @NotNull Class<? extends AudioChannel> getReturnType() {
		return AudioChannel.class;
	}
}
