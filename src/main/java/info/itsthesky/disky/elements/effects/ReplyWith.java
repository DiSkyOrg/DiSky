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
import info.itsthesky.disky.api.events.specific.InteractionEvent;
import info.itsthesky.disky.api.events.specific.MessageEvent;
import info.itsthesky.disky.api.skript.SpecificBotEffect;
import info.itsthesky.disky.core.Bot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.entities.sticker.Sticker;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
public class ReplyWith extends SpecificBotEffect<Object> {

	static {
		Skript.registerEffect(
				ReplyWith.class,
				"reply with [hidden] %string/messagecreatebuilder/sticker/embedbuilder% [with [the] reference[d] [message] %-message%] [and store (it|the message) in %-objects%]"
		);
	}

	private Expression<Object> exprMessage;
	private Expression<Message> exprReference;
	private boolean hidden;

	@Override
	public boolean initEffect(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
		if (!containsInterfaces(MessageEvent.class)) {
			Skript.error("The effect reply effect can only be used in a message event.");
			return false;
		}

		hidden = parseResult.expr.startsWith("reply with hidden");
		exprMessage = (Expression<Object>) expressions[0];
		exprReference = (Expression<Message>) expressions[1];
		setChangedVariable((Variable<Object>) expressions[2]);

		return true;
	}

	@Override
	public void runEffect(@NotNull Event e, @NotNull Bot bot) {
		final Object message = parseSingle(exprMessage, e);
		final Message reference = parseSingle(exprReference, e);
		if (message == null) {
			restart();
			return;
		}

		if (message instanceof Sticker) {
			final MessageEvent event = (MessageEvent) e;
			if (!(event.getMessageChannel() instanceof GuildMessageChannel)) {
				Skript.error("You can't reply with a sticker in a guild channel!");
				return;
			}
			((GuildMessageChannel) event.getMessageChannel()).sendStickers((Sticker) message).setMessageReference(reference).queue(this::restart, ex -> {
				DiSky.getErrorHandler().exception(e, ex);
				restart();
			});
		} else {
			final MessageCreateBuilder builder;
			if (message instanceof MessageCreateBuilder)
				builder = (MessageCreateBuilder) message;
			else if (message instanceof EmbedBuilder)
				builder = new MessageCreateBuilder().addEmbeds(((EmbedBuilder) message).build());
			else
				builder = new MessageCreateBuilder().setContent((String) message);

			if (e instanceof InteractionEvent) {
				final InteractionEvent event = (InteractionEvent) e;
				if (!(event.getInteractionEvent().getInteraction() instanceof IReplyCallback)) {
					Skript.error("You are trying to reply to an interaction that is not a reply callback.");
					restart();
					return;
				}

				if (event.getInteractionEvent().getInteraction().isAcknowledged()) {
					Skript.error("You are trying to reply or defer an interaction that has already been acknowledged!");
					restart();
					return;
				}

				final IReplyCallback callback = (IReplyCallback) event.getInteractionEvent().getInteraction();
				callback.reply(builder.build()).setEphemeral(hidden).queue(hook -> {
					if (hidden) restart(hook);
					else restart(hook);
				}, ex -> {
					DiSky.getErrorHandler().exception(e, ex);
					restart();
				});

			} else {
				final MessageEvent event = (MessageEvent) e;
				event.getMessageChannel().sendMessage(builder.build()).setMessageReference(reference).queue(this::restart, ex -> {
					DiSky.getErrorHandler().exception(e, ex);
					restart();
				});
			}
		}
	}

	@Override
	public @NotNull String toString(@Nullable Event e, boolean debug) {
		return null;
	}

}
