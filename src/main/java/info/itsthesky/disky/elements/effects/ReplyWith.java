package info.itsthesky.disky.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.api.events.specific.MessageEvent;
import info.itsthesky.disky.api.skript.SpecificBotEffect;
import info.itsthesky.disky.core.Bot;
import info.itsthesky.disky.core.JDAUtils;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Reply With")
@Description({"Reply with a specific text, embed or message builder.",
		"This effect will only work with events that implement 'message event' (aka every channel-related events)"})
@Examples({"reply with \"Hello world\"",
		"reply with \"yo guys :p\" and store the message in {_msg}"})
public class ReplyWith extends SpecificBotEffect<Message> {

	static {
		Skript.registerEffect(
				ReplyWith.class,
				"reply with [the] [content] %string/embedbuilder/messagebuilder% [and store (it|the message) in %-objects%]"
		);
	}

	private Expression<Object> exprMessage;

	@Override
	public boolean initEffect(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
		if (!containsInterfaces(MessageEvent.class)) {
			Skript.error("The reply effect can only be used in messages events.");
			return false;
		}
		exprMessage = (Expression<Object>) expressions[0];
		return validateVariable(expressions[1], false, true);
	}

	@Override
	public void runEffect(Event e, Bot bot) {
		final MessageEvent event = (MessageEvent) e;
		final Object rawMessage = parseSingle(exprMessage, e, null);
		final MessageBuilder message = JDAUtils.constructMessage(rawMessage);
		final MessageChannel channel = bot.findMessageChannel(event.getMessageChannel());
		if (anyNull(channel, event, rawMessage, message)) {
			restart();
			return;
		}

		channel.sendMessage(message.build())
				.queue(this::restart, ex -> {
					DiSky.getErrorHandler().exception(ex);
					restart();
				});
	}

	@Override
	public @NotNull String toString(@Nullable Event e, boolean debug) {
		return "reply with " + exprMessage.toString(e, debug) + (getChangedVariable() == null ? "" :
				" and store the message in " + getChangedVariable().toString(e, debug));
	}
}
