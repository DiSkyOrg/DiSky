package net.itsthesky.disky.elements.properties.forums;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.itsthesky.disky.api.ReflectionUtils;
import net.itsthesky.disky.api.skript.EasyElement;
import net.itsthesky.disky.elements.changers.IAsyncChangeableExpression;
import net.dv8tion.jda.api.entities.channel.attribute.IPostContainer;
import net.dv8tion.jda.api.entities.channel.concrete.ForumChannel;
import net.dv8tion.jda.api.requests.RestAction;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ForumSortOrder extends SimplePropertyExpression<ForumChannel, String>
		implements IAsyncChangeableExpression {

	static {
		register(ForumSortOrder.class, String.class,
				"[forum] [default] sort order",
				"forumchannel"
		);
	}

	@Override
	public @Nullable String convert(ForumChannel forumChannel) {
		return forumChannel.getDefaultSortOrder().name().toLowerCase();
	}

	@Override
	public @NotNull Class<? extends String> getReturnType() {
		return String.class;
	}

	@Override
	protected @NotNull String getPropertyName() {
		return "forum default sort order";
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
			return new Class[] {String.class};
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

				String rawLayout = (String) delta[0];
				IPostContainer.SortOrder sortOrder;

				sortOrder = ReflectionUtils.parseEnum(IPostContainer.SortOrder.class, rawLayout);
				if (sortOrder == null) {
					Skript.error("The sort order named '" + rawLayout + "' doesn't exist!");
					return;
				}

				action = channel.getManager().setDefaultSortOrder(sortOrder);
				break;
			case RESET:
			case DELETE:
				action = channel.getManager().setDefaultSortOrder(IPostContainer.SortOrder.RECENT_ACTIVITY);
				break;
			default:
				return;
		}

		if (complete) action.complete();
		else action.queue();
	}
}
