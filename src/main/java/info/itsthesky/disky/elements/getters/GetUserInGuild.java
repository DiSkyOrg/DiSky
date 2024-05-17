package info.itsthesky.disky.elements.getters;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.api.skript.EasyElement;
import info.itsthesky.disky.elements.changers.IAsyncGettableExpression;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("User in Guild")
@Description({"Get the member related to the specified user in a specific guild.",
"Users are common to whole Discord, two user cannot have the same instance.",
"Members are common to guilds, but also holding an user as reference.",
"User can have multiple instance according to which guild they are in, therefore they are considered as member."})
public class GetUserInGuild extends SimpleExpression<Member> implements IAsyncGettableExpression<Member> {

	static {
		Skript.registerExpression(GetUserInGuild.class,
				Member.class,
				ExpressionType.COMBINED,
				"%user% in [the] [guild] %guild%");
	}

	@Override
	public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull SkriptParser.ParseResult parseResult) {
		exprUser = (Expression<User>) exprs[0];
		exprGuild = (Expression<Guild>) exprs[1];
		return true;
	}

	private Expression<User> exprUser;
	private Expression<Guild> exprGuild;

	@Override
	protected Member @NotNull [] get(@NotNull Event e) {
		final User user = EasyElement.parseSingle(exprUser, e, null);
		final Guild guild = EasyElement.parseSingle(exprGuild, e, null);
		if (EasyElement.anyNull(this, user, guild))
			return new Member[0];
		return new Member[] {guild.getMember(user)};
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
		return exprUser.toString(e, debug) + " in " + exprGuild.toString(e, debug);
	}

	@Override
	public Member[] getAsync(Event e) {
		final User user = EasyElement.parseSingle(exprUser, e, null);
		final Guild guild = EasyElement.parseSingle(exprGuild, e, null);
		if (EasyElement.anyNull(this, user, guild))
			return new Member[0];
		return new Member[] {guild.retrieveMember(user).complete()};
	}
}
