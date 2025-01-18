package net.itsthesky.disky.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.config.Node;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.AsyncEffect;
import ch.njol.util.Kleenean;
import net.itsthesky.disky.core.Bot;
import net.itsthesky.disky.elements.sections.handler.DiSkyRuntimeHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.sticker.Sticker;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessagePollBuilder;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.itsthesky.disky.api.skript.EasyElement.parseSingle;

@Name("Post Message")
@Description({"Posts a message to a message-channel.",
		"You can send messages in a text, private, news, post or thread channel.",
})
@Examples({"post \"Hello world!\" to text channel with id \"000\"",
		"post last embed to thread channel with id \"000\" and store it in {_message}"})
@Since("4.4.0")
public class PostMessage extends AsyncEffect {

	static {
		Skript.registerEffect(
				PostMessage.class,
				"(post|dispatch) %string/messagecreatebuilder/sticker/embedbuilder/messagepollbuilder% (in|to) [the] %channel% [(using|with) [the] [bot] %-bot%] [with [the] reference[d] [message] %-message%] [and store (it|the message) in %-~objects%]"
		);
	}

	private Expression<Object> exprMessage;
	private Expression<Channel> exprChannel;
	private Expression<Bot> exprBot;
	private Expression<Message> exprReference;
	private Expression<Object> exprResult;
	private Node node;

	@Override
	public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, ParseResult parseResult) {
		getParser().setHasDelayBefore(Kleenean.TRUE);
		node = getParser().getNode();

		this.exprMessage = (Expression<Object>) expressions[0];
		this.exprChannel = (Expression<Channel>) expressions[1];
		this.exprBot = (Expression<Bot>) expressions[2];
		this.exprReference = (Expression<Message>) expressions[3];
		this.exprResult = (Expression<Object>) expressions[4];
		return exprResult == null || Changer.ChangerUtils.acceptsChange(exprResult, Changer.ChangeMode.SET, Message.class);
	}

	@Override
	public void execute(@NotNull Event e) {
		final Object message = parseSingle(exprMessage, e);
		Channel channel = parseSingle(exprChannel, e);
		final @Nullable Message reference = parseSingle(exprReference, e);
		final Bot bot = Bot.fromContext(exprBot, e);
		if (message == null || channel == null)
			return;

		if (!MessageChannel.class.isAssignableFrom(channel.getClass())) {
			Skript.error("The specified channel must be a message channel.");
			return;
		}

		final MessageCreateAction action;
		if (message instanceof Sticker) {
			final MessageChannel messageChannel = (MessageChannel) channel;
			if (!(messageChannel instanceof GuildMessageChannel)) {
				Skript.error("You can't reply with a sticker in a guild channel!");
				return;
			}

			action = ((GuildMessageChannel) messageChannel).sendStickers((Sticker) message);
		} else {
			final MessageCreateBuilder builder;
			if (message instanceof MessageCreateBuilder)
				builder = (MessageCreateBuilder) message;
			else if (message instanceof EmbedBuilder)
				builder = new MessageCreateBuilder().addEmbeds(((EmbedBuilder) message).build());
			else if (message instanceof MessagePollBuilder)
				builder = new MessageCreateBuilder().setPoll(((MessagePollBuilder) message).build());
			else
				builder = new MessageCreateBuilder().setContent((String) message);

			action = ((MessageChannel) channel).sendMessage(builder.build());
			if (reference != null) // see https://github.com/discord-jda/JDA/pull/2749
				action.setMessageReference(reference);
			if (builder.getPoll() != null)
				action.setPoll(builder.getPoll());
		}

		final Message finalMessage;
		try {
			finalMessage = action.complete(true);
		} catch (Exception ex) {
			DiSkyRuntimeHandler.error(ex, node);
			return;
		}

		if (exprResult == null)
			return;
		exprResult.change(e, new Object[] {finalMessage}, Changer.ChangeMode.SET);
	}

	@Override
	public @NotNull String toString(@Nullable Event e, boolean debug) {
		return "post " + exprMessage.toString(e, debug) + " to " + exprChannel.toString(e, debug)
				+ (exprResult == null ? "" : " and store it in " + exprResult.toString(e, debug));
	}
}
