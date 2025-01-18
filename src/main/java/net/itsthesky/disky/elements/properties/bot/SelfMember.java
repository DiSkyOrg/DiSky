package net.itsthesky.disky.elements.properties.bot;

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
import net.itsthesky.disky.api.skript.EasyElement;
import net.itsthesky.disky.core.Bot;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Bot Self Member")
@Description("Get the self member instance of a bot, in a specific guild.")
@Examples({"self member of event-bot in event-guild",
"self member of bot \"name\""})
@Since("4.9.0")
public class SelfMember extends SimpleExpression<Member> {

	static {
		Skript.registerExpression(
				SelfMember.class,
				Member.class,
				ExpressionType.PROPERTY,
				"[the] self [member] of [the] [bot] %bot% [in [the] [guild] %guild%]",
				"[the] [bot] %bot%'s self [member] [in [the] [guild] %guild%]"
		);
	}

	private Expression<Bot> exprBot;
	private Expression<Guild> exprGuild;

	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.@NotNull ParseResult parseResult) {
		exprBot = (Expression<Bot>) exprs[0];
		exprGuild = (Expression<Guild>) exprs[1];
		return true;
	}

	@Override
	protected Member @NotNull [] get(@NotNull Event e) {
		final Bot bot = EasyElement.parseSingle(exprBot, e);
		final Guild guild = EasyElement.parseSingle(exprGuild, e);
		if (bot == null || guild == null)
			return new Member[0];

		if (!guild.getJDA().equals(bot.getInstance()))
			return new Member[] {guild.getMember(bot.getInstance().getSelfUser())};

		return new Member[] {guild.getSelfMember()};
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public @NotNull Class<? extends Member> getReturnType() {
		return Member.class;
	}

	@Override
	public @NotNull String toString(@Nullable Event e, boolean debug) {
		return "self member of " + exprBot.toString(e, debug) + " in " + exprGuild.toString(e, debug);
	}
}
