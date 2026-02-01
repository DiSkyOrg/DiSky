package net.itsthesky.disky.elements.properties.tags;

import ch.njol.skript.Skript;
import net.itsthesky.disky.api.DiSkyRegistry;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import net.itsthesky.disky.api.skript.EasyElement;
import net.dv8tion.jda.api.entities.channel.concrete.ForumChannel;
import net.dv8tion.jda.api.entities.channel.forums.ForumTag;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Get Tag")
@Description("Get a tag from a forum channel using its name.")
@Examples("tag named \"v4\" from forum channel with id \"000\"")
@Since("4.4.4")
public class GetTag extends SimpleExpression<ForumTag> {

	static {
		DiSkyRegistry.registerExpression(
				GetTag.class,
				ForumTag.class,
				ExpressionType.SIMPLE,
				"[forum] tag ((from|with) name|named) %string% (of|from|in) %forumchannel%"
		);
	}

	private Expression<String> exprName;
	private Expression<ForumChannel> exprChannel;

	@Override
	public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull SkriptParser.ParseResult parseResult) {
		exprName = (Expression<String>) exprs[0];
		exprChannel = (Expression<ForumChannel>) exprs[1];
		return true;
	}

	@Override
	protected ForumTag @NotNull [] get(@NotNull Event e) {
		final String name = EasyElement.parseSingle(exprName, e);
		final ForumChannel channel = EasyElement.parseSingle(exprChannel, e);
		if (name == null || channel == null)
			return new ForumTag[0];

		return new ForumTag[] {channel.getAvailableTags().stream().filter(tag -> tag.getName().equals(name)).findFirst().orElse(null)};
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public @NotNull Class<? extends ForumTag> getReturnType() {
		return ForumTag.class;
	}

	@Override
	public @NotNull String toString(@Nullable Event e, boolean debug) {
		return "tag named " + exprName.toString(e, debug) + " from " + exprChannel.toString(e, debug);
	}
}
