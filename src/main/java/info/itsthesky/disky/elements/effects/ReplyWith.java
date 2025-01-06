package info.itsthesky.disky.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.config.Node;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.api.events.specific.InteractionEvent;
import info.itsthesky.disky.api.events.specific.MessageEvent;
import info.itsthesky.disky.api.skript.AsyncEffectSection;
import info.itsthesky.disky.api.skript.BetterExpressionEntryData;
import info.itsthesky.disky.core.SkriptUtils;
import info.itsthesky.disky.elements.components.core.ComponentRow;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.entities.sticker.Sticker;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.callbacks.IPremiumRequiredReplyCallback;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.utils.messages.*;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;
import org.skriptlang.skript.lang.entry.util.ExpressionEntryData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static info.itsthesky.disky.api.skript.EasyElement.*;

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
public class ReplyWith extends AsyncEffectSection {

	static {
		Skript.registerSection(
				ReplyWith.class,
				"reply with [hidden] %string/messagecreatebuilder/sticker/embedbuilder/messagepollbuilder% [with [the] reference[d] [message] %-message%] [and store (it|the message) in %-objects%]",
				"reply with premium [required] message"
		);
	}

	private static final EntryValidator ReplySectionValidator = EntryValidator.builder()
			.addEntryData(new ExpressionEntryData<>("content", null, true, Object.class))
			.addEntryData(new ExpressionEntryData<>("embed", null, true, Object.class))
			.addEntryData(new ExpressionEntryData<>("poll", null, true, Object.class))
			.addEntryData(new BetterExpressionEntryData<>("components", null, true, Object.class))
			.build();

	private Node node;
	private Expression<Object> exprMessage;
	private Expression<Message> exprReference;
	private Expression<Object> exprResult;
	private boolean hidden;
	private boolean premium;
	private @Nullable SectionNode sectionNode;

	//region Section Values
	private @Nullable Expression<?> secExprContent;
	private @Nullable Expression<?> secExprEmbed;
	private @Nullable Expression<?> secExprPoll;
	private @Nullable List<Expression<?>> secExprComponents;
	//endregion

	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult, SectionNode sectionNode, List<TriggerItem> triggerItems) {
		if (!containsInterfaces(MessageEvent.class)) {
			Skript.error("The effect reply effect can only be used in a message event.");
			return false;
		}

		this.node = getParser().getNode();
		this.premium = matchedPattern == 1;

		if (!premium) {
			this.hidden = parseResult.expr.startsWith("reply with hidden");
			this.exprMessage = (Expression<Object>) expressions[0];
			this.exprReference = (Expression<Message>) expressions[1];
			this.exprResult = (Expression<Object>) expressions[2];
		}

		if (sectionNode != null) {
			this.sectionNode = sectionNode;

			EntryContainer validatedEntries = ReplySectionValidator.validate(sectionNode);
			if (validatedEntries == null)
				return false;

			secExprContent = validatedEntries.getOptional("content", Expression.class, true);
			secExprEmbed = validatedEntries.getOptional("embed", Expression.class, true);
			secExprPoll = validatedEntries.getOptional("poll", Expression.class, true);
			secExprComponents = validatedEntries.getOptional("components", List.class, true);
		}

		return exprResult == null || Changer.ChangerUtils.acceptsChange(exprResult, Changer.ChangeMode.SET, Message.class);
	}

	private MessageCreateBuilder parseAdditionalData(Event event) {
		if (sectionNode == null)
			return new MessageCreateBuilder();

		final MessageCreateBuilder builder = new MessageCreateBuilder();

		if (secExprContent != null) {
			final Object content = parseSingle(secExprContent, event);
			if (content instanceof final String string)
				builder.setContent(string);
		}

		if (secExprEmbed != null) {
			final Object[] embeds = parseList((Expression<Object>) secExprEmbed, event, new Object[0]);
			for (Object embed : embeds) {
				if (embed instanceof final EmbedBuilder embedBuilder)
					builder.addEmbeds(embedBuilder.build());
			}
		}

		if (secExprPoll != null) {
			final Object poll = parseSingle(secExprPoll, event);
			if (poll instanceof final MessagePollBuilder pollBuilder)
				builder.setPoll(pollBuilder.build());
		}

		if (secExprComponents != null) {
			final List<ComponentRow> rows = new ArrayList<>();

			for (Expression<?> secExprComponent : secExprComponents) {
				final Object[] rawComponents = parseList((Expression<Object>) secExprComponent, event, new Object[0]);
				final ComponentRow row = new ComponentRow(Arrays.asList(rawComponents));
				if (row.isEmpty())
					continue;

				rows.add(row);
			}

			builder.setComponents(rows.stream().map(ComponentRow::asActionRow).toList());
		}

		return builder;
	}

	@Override
	public void execute(@NotNull Event e) {
		if (premium) {
			if (e instanceof InteractionEvent) {
				final InteractionEvent event = (InteractionEvent) e;
				final Interaction interaction = event.getInteractionEvent().getInteraction();
				if (!(interaction instanceof IPremiumRequiredReplyCallback)) {
					SkriptUtils.error(node, "You can only use the premium required message in an interaction event!");
					return;
				}

				try {
					((IPremiumRequiredReplyCallback) interaction).replyWithPremiumRequired().complete();
				} catch (Exception ex) {
					DiSky.getErrorHandler().exception(e, ex);
				}

            } else {
				SkriptUtils.error(node, "You can only use the premium required message in an interaction event!");
            }
            return;
        }

		final Object message = parseSingle(exprMessage, e);
		final Message reference = parseSingle(exprReference, e);
		if (message == null)
			return;

		@Nullable RestAction<Message> messageRestAction = null;
		@Nullable RestAction<InteractionHook> otherRestAction = null;

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
				builder = parseAdditionalData(e).addEmbeds(((EmbedBuilder) message).build());
			else if (message instanceof MessagePollBuilder)
				builder = parseAdditionalData(e).setPoll(((MessagePollBuilder) message).build());
			else
				builder = parseAdditionalData(e).setContent((String) message);

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
							.setEphemeral(hidden);
				}
			} else {
				final MessageEvent event = (MessageEvent) e;
				messageRestAction = event.getMessageChannel().sendMessage(builder.build());
				if (reference != null)
					((MessageCreateAction) messageRestAction).setMessageReference(reference);
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
			final InteractionHook interactionHook;
			try {
				interactionHook = otherRestAction.complete();
			} catch (Exception ex) {
				DiSky.getErrorHandler().exception(e, ex);
				return;
			}
			if (exprResult != null) {
				final Message hookMessage = interactionHook.retrieveOriginal().complete();
				exprResult.change(e, new Message[] {hookMessage}, Changer.ChangeMode.SET);
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
