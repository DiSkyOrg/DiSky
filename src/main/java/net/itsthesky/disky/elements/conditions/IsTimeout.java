package net.itsthesky.disky.elements.conditions;

import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.config.Node;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.SeeAlso;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import net.itsthesky.disky.api.ReflectionUtils;
import net.itsthesky.disky.elements.sections.handler.DiSkyRuntimeHandler;
import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.NotNull;

@Name("Member is Timed Out")
@Description("Check if a member is timed out in a discord server or not.")
@Examples({"if event-member is timeout:",
        "\treply with \"This member is currently timed out!\""})
@Since("4.20.2")
@SeeAlso(Member.class)
public class IsTimeout extends PropertyCondition<Member> {

    static {
        register(
                IsTimeout.class,
                PropertyType.BE,
                "[discord] [member] timeout[ed]",
                "member"
        );
    }

    private Node node;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.@NotNull ParseResult parseResult) {
        node = getParser().getNode();
        return super.init(exprs, matchedPattern, isDelayed, parseResult);
    }

    @Override
    public boolean check(Member member) {
        if (member == null)
        {
            DiSkyRuntimeHandler.exprNotSet(node, ReflectionUtils.getFieldValueViaInstance(this, "expr"));
            return false;
        }

        return member.isTimedOut();
    }

    @Override
    protected String getPropertyName() {
        return "timeout";
    }

}
