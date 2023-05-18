package info.itsthesky.disky.elements.getters;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.api.skript.EasyElement;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Get Member")
@Description({"Get a cached member from its unique ID",
        "This expression could return null, according to if the actual member was cached or not.",
        "To be sure it will return the corresponding member, use the retrieve member effect.",
        "This expression cannot be changed"})
@Examples({"member with id \"000\" in event-guild"})
public class GetMember extends SimpleExpression<Member> {

    static {
        Skript.registerExpression(
                GetMember.class,
                Member.class,
                ExpressionType.COMBINED,
                "[get] [the] member with id %string% (from|in|of) [the] [guild] %guild%"
        );
    }

    private Expression<String> exprId;
    private Expression<Guild> exprGuild;

    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull SkriptParser.ParseResult parseResult) {
        exprId = (Expression<String>) exprs[0];
        exprGuild = (Expression<Guild>) exprs[1];
        return true;
    }

    @Override
    protected Member @NotNull [] get(@NotNull Event e) {
        final String id = exprId.getSingle(e);
        final Guild guild = exprGuild.getSingle(e);
        if (EasyElement.anyNull(this, id, guild))
            return new Member[0];
        return new Member[] {guild.getMemberById(id)};
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
        return "member with id " + exprId.toString(e, debug) +" in " + exprGuild.toString(e, debug);
    }
}
