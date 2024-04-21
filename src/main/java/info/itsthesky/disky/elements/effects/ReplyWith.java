package info.itsthesky.disky.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.config.Node;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.Variable;
import ch.njol.skript.util.AsyncEffect;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.api.events.specific.InteractionEvent;
import info.itsthesky.disky.api.events.specific.MessageEvent;
import info.itsthesky.disky.api.skript.SpecificBotEffect;
import info.itsthesky.disky.core.Bot;
import info.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.entities.sticker.Sticker;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import net.dv8tion.jda.api.utils.messages.MessagePollBuilder;
import net.dv8tion.jda.api.utils.messages.MessagePollData;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static info.itsthesky.disky.api.skript.EasyElement.containsInterfaces;
import static info.itsthesky.disky.api.skript.EasyElement.parseSingle;

@Name("Reply With")
@Description({"Reply with a specific message to the channel where a message-event was triggered.",
		"It can also be used to acknowledge & reply to an interaction, such as button click or slash command.",
		"In interaction only, you can use the keyword 'hidden' to reply with an ephemeral message (only the executor can see it).",
		"Therefore, the value stored in the variable, if specified, will be an interaction hook, and not a compete message.",
		"You can also provide a message as reference. The replied message be linked with the provided one."})
@Examples({"reply with \"Hello world!\"",
		"reply with last embed with reference event-message",
		"reply with hidden \"Hello ...\" and store it in {_msg}\n" +
				"wait a second",
		"edit {_msg} to show \"... world!\""})
@Since("4.4.0")
public class ReplyWith extends AsyncEffect {

	static {
		Skript.registerEffect(
				ReplyWith.class,
				"reply with [hidden] %string/messagecreatebuilder/sticker/embedbuilder% [with [the] reference[d] [message] %-message%] [and store (it|the message) in %-objects%]"
		);
	}

	private Node node;
	private Expression<Object> exprMessage;
	private Expression<Message> exprReference;
	private Expression<Object> exprResult;
	private boolean hidden;

	@Override
	public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
		if (!containsInterfaces(MessageEvent.class)) {
			Skript.error("The effect reply effect can only be used in a message event.");
			return false;
		}

		node = getParser().getNode();
		hidden = parseResult.expr.startsWith("reply with hidden");
		exprMessage = (Expression<Object>) expressions[0];
		exprReference = (Expression<Message>) expressions[1];
		exprResult = (Expression<Object>) expressions[2];

		return exprResult == null || Changer.ChangerUtils.acceptsChange(exprResult, Changer.ChangeMode.SET, Message.class);
	}

	@Override
	public void execute(@NotNull Event e) {
		final Object message = parseSingle(exprMessage, e);
		final Message reference = parseSingle(exprReference, e);
		if (message == null)
			return;

		@Nullable RestAction<Message> messageRestAction = null;
		@Nullable RestAction<?> otherRestAction = null;

		if (message instanceof Sticker) {
			final MessageEvent event = (MessageEvent) e;
			if (!(event.getMessageChannel() instanceof GuildMessageChannel)) {
				SkriptUtils.error(node, "You can't reply with a sticker in a guild channel!");
				return;
			}
			messageRestAction =((GuildMessageChannel) event.getMessageChannel())
					.sendStickers((Sticker) message)
					.setMessageReference(reference);
		} else {
			final MessageCreateBuilder builder;
			if (message instanceof MessageCreateBuilder)
				builder = (MessageCreateBuilder) message;
			else if (message instanceof EmbedBuilder)
				builder = new MessageCreateBuilder().addEmbeds(((EmbedBuilder) message).build());
			else
				builder = new MessageCreateBuilder().setContent((String) message);
			final @Nullable MessagePollData poll = builder.getPoll();

			if (!builder.isValid()) {
				SkriptUtils.error(node, "The provided message is not valid!");
				return;
			}

			if (e instanceof InteractionEvent) {
				final InteractionEvent event = (InteractionEvent) e;
				if (!(event.getInteractionEvent().getInteraction() instanceof IReplyCallback)) {
					SkriptUtils.error(node,"You are trying to reply or defer an interaction that is not a reply callback!");
					return;
				}

				final IReplyCallback callback = (IReplyCallback) event.getInteractionEvent().getInteraction();

				if (DeferInteraction.WAITING_INTERACTIONS.contains(callback.getHook().getInteraction().getIdLong())) {
					DeferInteraction.WAITING_INTERACTIONS.remove(callback.getHook().getInteraction().getIdLong());
					messageRestAction = callback.getHook().editOriginal(MessageEditData.fromCreateData(builder.build()));
				} else {
					if (event.getInteractionEvent().getInteraction().isAcknowledged()) {
						SkriptUtils.error(node,"You are trying to reply or defer an interaction that has already been acknowledged!");
						return;
					}

					otherRestAction = callback.reply(builder.build())
							.setPoll(poll)
							.setEphemeral(hidden);
				}
			} else {
				final MessageEvent event = (MessageEvent) e;
				messageRestAction = event.getMessageChannel().sendMessage(builder.build())
						.setPoll(poll)
						.setMessageReference(reference);
			}
		}

		if (messageRestAction != null) {

			final Message result;
			try {
				result = messageRestAction.complete();
			} catch (Exception ex) {
				DiSky.getErrorHandler().exception(e, ex);
				return;
			}

			if (exprResult != null)
				exprResult.change(e, new Message[] {result}, Changer.ChangeMode.SET);
		}

		if (otherRestAction != null) {
			try {
				otherRestAction.complete();
			} catch (Exception ex) {
				DiSky.getErrorHandler().exception(e, ex);
			}
		}
	}

	@Override
	public @NotNull String toString(@Nullable Event e, boolean debug) {
		return "reply with " + exprMessage.toString(e, debug) + (hidden ? " hidden" : "")
				+ (exprReference != null ? " with reference " + exprReference.toString(e, debug) : "")
				+ (exprResult != null ? " and store it in " + exprResult.toString(e, debug) : "");
	}

}
