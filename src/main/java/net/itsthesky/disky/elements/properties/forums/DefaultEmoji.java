package net.itsthesky.disky.elements.properties.forums;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.itsthesky.disky.api.emojis.Emote;
import net.itsthesky.disky.api.skript.EasyElement;
import net.itsthesky.disky.elements.changers.IAsyncChangeableExpression;
import net.dv8tion.jda.api.entities.channel.concrete.ForumChannel;
import net.dv8tion.jda.api.requests.RestAction;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Default Forum Emoji")
@Description({"Represent the default emoji of a forum channel.",
"It's the mote that is added automatically once a new post is created.",
"Can return none and can be changed."})
@Examples("set default emoji of event-forumchannel to reaction \"smile\"")
public class DefaultEmoji extends SimplePropertyExpression<ForumChannel, Emote>
		implements IAsyncChangeableExpression {

	static {
		register(DefaultEmoji.class, Emote.class,
				"default [forum] emoji",
				"forumchannel"
		);
	}

	@Override
	public @Nullable Emote convert(ForumChannel forumChannel) {
		if (forumChannel.getDefaultReaction() == null)
			return null;

		return new Emote(forumChannel.getDefaultReaction());
	}

	@Override
	public @NotNull Class<? extends Emote> getReturnType() {
		return Emote.class;
	}

	@Override
	protected @NotNull String getPropertyName() {
		return "default forum emoji";
	}

	@Override
	public void changeAsync(Event e, Object[] delta, Changer.ChangeMode mode) {
		change(e, delta, mode, true);
	}

	@Override
	public void change(@NotNull Event event, Object @NotNull [] delta, Changer.@NotNull ChangeMode mode) {
		change(event, delta, mode, false);
	}

	@Override
	public Class<?> @NotNull [] acceptChange(Changer.@NotNull ChangeMode mode) {
		if (mode == Changer.ChangeMode.SET || mode == Changer.ChangeMode.RESET || mode == Changer.ChangeMode.DELETE)
			return new Class[] {Emote.class};
		return new Class[0];
	}

	public void change(Event e, Object[] delta, Changer.ChangeMode mode, boolean complete) {
		final ForumChannel channel = getExpr().getSingle(e);
		if (channel == null)
			return;

		final RestAction<?> action;
		switch (mode) {
			case SET:
				if (!EasyElement.isValid(delta) || delta[0] == null)
					return;

				Emote emote = (Emote) delta[0];
				action = channel.getManager().setDefaultReaction(emote.getEmoji());
				break;
			case RESET:
			case DELETE:
				action = channel.getManager().setDefaultReaction(null);
				break;
			default:
				return;
		}

		if (complete) action.complete();
		else action.queue();
	}
}
