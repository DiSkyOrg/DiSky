package info.itsthesky.disky.elements.properties.tags;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.api.emojis.Emote;
import info.itsthesky.disky.api.skript.EasyElement;
import net.dv8tion.jda.api.entities.channel.forums.BaseForumTag;
import net.dv8tion.jda.api.entities.channel.forums.ForumTagData;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("New Forum Tag")
@Description({"Create a new forum tag with a specific name & optional emoji.",
"You can also specify if the tag is 'moderate' or not."})
@Examples({"new forum tag named \"solved\" with reaction \"white_check_mark\"",
"new moderated forum tag named \"internal\""})
@Since("4.4.4")
public class NewTag extends SimpleExpression<BaseForumTag> {

	static {
		Skript.registerExpression(
				NewTag.class,
				BaseForumTag.class,
				ExpressionType.SIMPLE,
				"new [forum] tag [named] %string% [with %-emote%]",
				"new moderated [forum] tag [named] %string% [with %-emote%]"
		);
	}

	private Expression<String> exprName;
	private Expression<Emote> exprEmoji;
	private boolean moderate;

	@Override
	public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.@NotNull ParseResult parseResult) {
		exprName = (Expression<String>) exprs[0];
		exprEmoji = (Expression<Emote>) exprs[1];
		moderate = matchedPattern == 1;
		return true;
	}

	@Override
	protected BaseForumTag @NotNull [] get(@NotNull Event e) {
		final String name = EasyElement.parseSingle(exprName, e);
		final Emote emoji = EasyElement.parseSingle(exprEmoji, e);
		if (name == null)
			return new BaseForumTag[0];
		return new BaseForumTag[] {
				new ForumTagData(name)
						.setModerated(moderate)
						.setEmoji(emoji.getEmoji())
		};
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public @NotNull Class<? extends BaseForumTag> getReturnType() {
		return BaseForumTag.class;
	}

	@Override
	public @NotNull String toString(@Nullable Event e, boolean debug) {
		return "new forum tag named " + exprName.toString(e, debug);
	}
}
