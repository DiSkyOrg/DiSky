package info.itsthesky.disky.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.Variable;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.api.skript.SpecificBotEffect;
import info.itsthesky.disky.core.Bot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Post Message")
@Description({"Posts a message to a message-channel.",
		"You can send messages in a text, private, news, post or thread channel.",
})
@Examples({"post \"Hello world!\" to text channel with id \"000\"",
		"post last embed to thread channel with id \"000\" and store it in {_message"})
@Since("4.4.0")
public class PostMessage extends SpecificBotEffect<Message> {

	static {
		Skript.registerEffect(
				PostMessage.class,
				"(post|dispatch) %string/messagecreatebuilder/embedbuilder% (in|to) [the] %channel% [and store (it|the message) in %-objects%]"
		);
	}

	private Expression<Object> exprMessage;
	private Expression<Channel> exprChannel;

	@Override
	public boolean initEffect(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
		this.exprMessage = (Expression<Object>) expressions[0];
		this.exprChannel = (Expression<Channel>) expressions[1];
		setChangedVariable((Variable<Message>) expressions[2]);
		return true;
	}

	@Override
	public void runEffect(@NotNull Event e, @NotNull Bot bot) {
		final Object message = parseSingle(exprMessage, e);
		final Channel channel = parseSingle(exprChannel, e);
		if (message == null || channel == null) {
			restart();
			return;
		}

		final MessageCreateBuilder builder;
		if (message instanceof MessageCreateBuilder)
			builder = (MessageCreateBuilder) message;
		else if (message instanceof EmbedBuilder)
			builder = new MessageCreateBuilder().addEmbeds(((EmbedBuilder) message).build());
		else
			builder = new MessageCreateBuilder().setContent((String) message);

		if (!MessageChannel.class.isAssignableFrom(channel.getClass())) {
			Skript.error("The specified channel must be a message channel.");
			restart();
			return;
		}

		((MessageChannel) channel).sendMessage(builder.build()).queue(this::restart, ex -> {
			DiSky.getErrorHandler().exception(e, ex);
			restart();
		});
	}

	@Override
	public @NotNull String toString(@Nullable Event e, boolean debug) {
		return "post " + exprMessage.toString(e, debug) + " to " + exprChannel.toString(e, debug);
	}
}
