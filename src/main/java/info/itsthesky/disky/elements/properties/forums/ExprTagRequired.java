package info.itsthesky.disky.elements.properties.forums;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import info.itsthesky.disky.api.skript.EasyElement;
import net.dv8tion.jda.api.entities.channel.concrete.ForumChannel;
import net.dv8tion.jda.api.requests.RestAction;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Name("Tag Required")
@Description({"Get a true/false value of the tag required state of a forum channel.",
"This property can be changed, and we recommend the tag required condition for checks."})
@Examples("set tag required of event-forumchannel to true")
public class ExprTagRequired extends SimplePropertyExpression<ForumChannel, Boolean> {

	static {
		register(ExprTagRequired.class, Boolean.class,
				"[the] tag required",
				"forumchannel"
		);
	}

	@Override
	public Class<?> @NotNull [] acceptChange(Changer.@NotNull ChangeMode mode) {
		if (mode == Changer.ChangeMode.SET)
			return new Class[]{Boolean.class};
		return new Class[0];
	}

	@Override
	public void change(@NotNull Event e, Object @NotNull [] delta, Changer.@NotNull ChangeMode mode) {
		if (!EasyElement.isValid(delta))
			return;

		final boolean value = (boolean) delta[0];
		final List<RestAction<?>> actions = new ArrayList<>();
		for (ForumChannel forumChannel : getExpr().getArray(e))
			actions.add(forumChannel.getManager().setTagRequired(value));

		RestAction.allOf(actions).queue();
	}

	@Override
	protected @NotNull String getPropertyName() {
		return "tag required";
	}

	@Override
	public @Nullable Boolean convert(ForumChannel forumChannel) {
		return forumChannel.isTagRequired();
	}

	@Override
	public @NotNull Class<? extends Boolean> getReturnType() {
		return Boolean.class;
	}
}
