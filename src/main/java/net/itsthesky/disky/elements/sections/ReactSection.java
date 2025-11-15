package net.itsthesky.disky.elements.sections;

import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import net.itsthesky.disky.api.generator.SeeAlso;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.*;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import net.itsthesky.disky.api.emojis.Emote;
import net.itsthesky.disky.api.skript.EasyElement;
import net.itsthesky.disky.core.ReactionListener;
import net.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.itsthesky.disky.elements.events.rework.ReactionEvents;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Name("ReactSection")
@Description("React to a message with an emote and execute code when a user reacts. Can be set to run once or wait for a specific user.")
@Examples({"react to event-message with reaction \"joy\" to run:",
        "\treply with \"You clicked the reaction!\""})
@Since("4.0.0")
@SeeAlso(Message.class)
public class ReactSection extends EffectSection {

	static {
		Skript.registerSection(
				ReactSection.class,
				"react to [the] [message] %message% with [the] %emote% [to run [(1¦one time)] [[and] wait for %-user%]]"
		);
	}

	private Expression<Message> exprMessage;
	private Expression<Emote> exprEmote;
	private Expression<User> exprUser;
	private boolean runOneTime;
	private Trigger trigger;

	@Override
	public boolean init(Expression<?> @NotNull [] exprs,
						int matchedPattern,
						@NotNull Kleenean isDelayed,
						@NotNull ParseResult parseResult,
						@Nullable SectionNode sectionNode,
						@Nullable List<TriggerItem> triggerItems) {
		exprMessage = (Expression<Message>) exprs[0];
		exprEmote = (Expression<Emote>) exprs[1];
		exprUser = (Expression<User>) exprs[2];
		runOneTime = (parseResult.mark & 1) != 0;
		if (sectionNode != null)
			trigger = loadCode(sectionNode, "react section",
					SkriptUtils.addEventClasses(ReactionEvents.REACTION_ADD.getBukkitEventClass()));
		return true;
	}

	@Override
	protected @Nullable TriggerItem walk(@NotNull Event e) {
		final Message message = EasyElement.parseSingle(exprMessage, e, null);
		final Emote emote = EasyElement.parseSingle(exprEmote, e, null);
		final @Nullable User user = EasyElement.parseSingle(exprUser, e, null);
		if (EasyElement.anyNull(this, message, emote))
			return getNext();
		emote.addReaction(message).queue(v -> ReactionListener.waiters.put(message.getIdLong(),
				new ReactionListener.ReactionInfo(runOneTime, emote, message.getIdLong(), message
				.getJDA().getSelfUser().getIdLong(), user, trigger)));
		return getNext();
	}

	@Override
	public @NotNull String toString(@Nullable Event e, boolean debug) {
		return "react to " + exprMessage.toString(e, debug) + " with " + exprEmote.toString(e, debug);
	}
}
