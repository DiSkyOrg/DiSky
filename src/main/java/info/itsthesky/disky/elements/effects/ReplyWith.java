package info.itsthesky.disky.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.api.events.specific.InteractionEvent;
import info.itsthesky.disky.api.events.specific.MessageEvent;
import info.itsthesky.disky.api.skript.EasyElement;
import info.itsthesky.disky.api.skript.SpecificBotEffect;
import info.itsthesky.disky.core.Bot;
import info.itsthesky.disky.core.JDAUtils;
import info.itsthesky.disky.elements.components.core.ComponentRow;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Name("Reply With / To Message")
@Description({"Reply with a specific text, embed or message builder.",
		"A reference message can be provided, and therefore the mention optional part can be implemented.",
		"This effect will only work with events that implement 'message event' (aka every channel-related events)"})
@Examples({"reply with \"Hello world\"",
		"reply with \"yo guys :p\" and store the message in {_msg}"})
public class ReplyWith extends SpecificBotEffect<Message> {

	static {
		Skript.registerEffect(
				ReplyWith.class,
				"reply with [hidden] [the] [content] %string/embedbuilder/messagebuilder% [with [the] (component|action)[s] [row] %-rows%] [with reference[d] [message] %-message% [(1¦mentioning)]] [and store (it|the message) in %-objects%]"
		);
	}

	private Expression<Object> exprMessage;
	private Expression<ComponentRow> exprComponents;
	private boolean isInInteraction;
	private boolean hidden;

	private boolean mentioning;
	private Expression<Message> exprReference;

	@Override
	public boolean initEffect(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
		if (!containsInterfaces(MessageEvent.class) && !containsInterfaces(InteractionEvent.class)) {
			Skript.error("The reply effect can only be used in message / interaction events.");
			return false;
		}
		isInInteraction = EasyElement.containsInterfaces(InteractionEvent.class);
		hidden = parseResult.expr.contains("with hidden");
		exprMessage = (Expression<Object>) expressions[0];
		exprComponents = (Expression<ComponentRow>) expressions[1];

		exprReference = (Expression<Message>) expressions[2];
		mentioning = (parseResult.mark & 1) != 1;

		return validateVariable(expressions[2], false, true);
	}

	@Override
	public void runEffect(Event e, Bot bot) {
		if (isInInteraction) {

			final IReplyCallback event = (IReplyCallback) ((InteractionEvent) e).getInteractionEvent();
			final InteractionHook hook = event.getHook();
			final Object rawMessage = parseSingle(exprMessage, e, null);
			final MessageBuilder message = JDAUtils.constructMessage(rawMessage);
			final List<ComponentRow> rows = Arrays.asList(parseList(exprComponents, e, new ComponentRow[0]));
			if (anyNull(event, rawMessage, message)) {
				restart();
				return;
			}

			final List<ActionRow> formatted = rows
					.stream()
					.map(ComponentRow::asActionRow)
					.collect(Collectors.toList());
			if (!hook.isExpired()) {
				hook.editOriginal(message.build())
				.queue(v -> restart(), ex -> {
					DiSky.getErrorHandler().exception(e, ex);
					restart();
				});
			}
			event.reply(message.build())
					.addActionRows(formatted)
					.setEphemeral(hidden)
					.queue(v -> restart(), ex -> {
						DiSky.getErrorHandler().exception(e, ex);
						restart();
					});

		} else {

			final MessageEvent event = (MessageEvent) e;
			final Object rawMessage = parseSingle(exprMessage, e, null);
			final MessageBuilder message = JDAUtils.constructMessage(rawMessage);
			final List<ComponentRow> rows = Arrays.asList(parseList(exprComponents, e, new ComponentRow[0]));
			final @Nullable Message referenced = parseSingle(exprReference, e, null);
			final MessageChannel channel = bot.findMessageChannel(event.getMessageChannel());
			if (anyNull(channel, event, rawMessage, message)) {
				restart();
				return;
			}

			final List<ActionRow> formatted = rows
					.stream()
					.map(ComponentRow::asActionRow)
					.collect(Collectors.toList());

			MessageAction action = channel.sendMessage(message.build());

			action = action.setActionRows(formatted);
			if (referenced != null)
				action = action.reference(referenced);

			action.queue(this::restart, ex -> {
				DiSky.getErrorHandler().exception(e, ex);
				restart();
			});

		}
	}

	@Override
	public @NotNull String toString(@Nullable Event e, boolean debug) {
		return "reply with " + exprMessage.toString(e, debug) + (getChangedVariable() == null ? "" :
				" and store the message in " + variableAsString(e, debug));
	}
}
