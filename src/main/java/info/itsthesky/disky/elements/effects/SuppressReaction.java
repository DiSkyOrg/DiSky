package info.itsthesky.disky.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.api.emojis.Emote;
import info.itsthesky.disky.api.skript.SpecificBotEffect;
import info.itsthesky.disky.core.Bot;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.RestAction;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

@Name("Suppress Reaction")
@Description({"Suppress one or more reactions of a message.",
		"You can also specific the user who added the emote to remove it one time.",
		"Without any specified user, it will be the bot's self user that removes the emote."})
@Examples({"suppress reaction \"x\" of event-user from event-message",
		"suppress reaction \"joy\" from event-message # Remove the reaction ADDED BY THE BOT"})
@Since("4.1.1")
public class SuppressReaction extends SpecificBotEffect {

	static {
		Skript.registerEffect(
				SuppressReaction.class,
				"suppress [the] %emotes% [(of|from) [the] %-user%] (of|from) [the] %message%"
		);
	}

	private Expression<Emote> exprEmote;
	private Expression<User> exprUser;
	private Expression<Message> exprMessage;

	@Override
	public boolean initEffect(Expression[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
		exprEmote = (Expression<Emote>) expressions[0];
		exprUser = (Expression<User>) expressions[1];
		exprMessage = (Expression<Message>) expressions[2];
		return true;
	}

	@Override
	public void runEffect(@NotNull Event e, Bot bot) {
		final Emote[] emotes = parseList(exprEmote, e, null);
		final User user = parseSingle(exprUser, e, null);
		final Message message = parseSingle(exprMessage, e, null);
		if (anyNull(this, emotes, message)) {
			restart();
			return;
		}

		final List<RestAction<Void>> actions = new LinkedList<>();

		for (Emote emote : emotes) {
			if (user == null)
				actions.add(message.removeReaction(emote.getEmoji()));
			else
				actions.add(message.removeReaction(emote.getEmoji(), user));
		}

		RestAction.allOf(actions).queue(this::restart, ex -> {
			restart();
			DiSky.getErrorHandler().exception(event, ex);
		});
	}

	@Override
	public @NotNull String toString(@Nullable Event e, boolean debug) {
		return "suppress reaction " + exprEmote.toString(e, debug) + (exprUser != null ? " of " + exprUser.toString(e, debug) : "") + " from " + exprMessage.toString(e, debug);
	}
}
