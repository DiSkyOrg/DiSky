package net.itsthesky.disky.elements.sections.message;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import net.itsthesky.disky.api.skript.EasyElement;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Message Builder Content")
@Description({"Text content of a message builder",
		"See also: 'Create (rich) Message'"})
public class MessageContent extends SimplePropertyExpression<MessageCreateBuilder, String> {

	static {
		register(
				MessageContent.class,
				String.class,
				"content",
				"messagecreatebuilder"
		);
	}

	@Override
	public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
		if (!getParser().isCurrentSection(CreateMessage.class)) {
			Skript.error("You can only use the 'builder content' expression inside a 'create message' section");
			return false;
		}
		return super.init(exprs, matchedPattern, isDelayed, parseResult);
	}

	@Override
	public Class<?> @NotNull [] acceptChange(@NotNull Changer.ChangeMode mode) {
		if (mode == Changer.ChangeMode.SET)
			return new Class[]{String.class};
		return new Class[0];
	}

	@Override
	public void change(@NotNull Event e, @NotNull Object[] delta, @NotNull Changer.ChangeMode mode) {
		if (!EasyElement.isValid(delta))
			return;

		final String content = (String) delta[0];
		final MessageCreateBuilder builder = EasyElement.parseSingle(getExpr(), e, null);

		if (content == null || builder == null)
			return;

		builder.setContent(content);
	}

	@Override
	protected @NotNull String getPropertyName() {
		return "content";
	}

	@Override
	public @Nullable String convert(MessageCreateBuilder messageCreateBuilder) {
		return messageCreateBuilder.getContent();
	}

	@Override
	public @NotNull Class<? extends String> getReturnType() {
		return String.class;
	}
}
