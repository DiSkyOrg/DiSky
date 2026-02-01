package net.itsthesky.disky.elements.conditions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.itsthesky.disky.api.DiSkyRegistry;
import net.itsthesky.disky.core.SkriptUtils;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Member Can Interact With Member(s)/Role(s)")
@Description({"Check if a member can interact with a specific member or role."})
@Examples({
        "if event-member can interact with (member with id \"000\" in event-guild):",
        "\treply with \"You can interact with this member!\"",
})
@Since("4.27.0")
public class MemberCanInteract extends Condition {

    static {
        DiSkyRegistry.registerCondition(
                MemberCanInteract.class,
                ConditionType.PROPERTY,
                "%member% can interact with %members/roles%",
                "%member% (can't|cannot|can not) interact with %members/roles%"
        );
    }

    private Expression<Member> exprMember;
    private Expression<Object> exprTargets;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        exprMember = (Expression<Member>) expressions[0];
        exprTargets = (Expression<Object>) expressions[1];
        setNegated(matchedPattern == 1);
        return true;
    }

    @Override
    public boolean check(Event event) {
        final var member = exprMember.getSingle(event);
        final var targets = SkriptUtils.getAllValues(event, exprTargets);
        if (member == null || targets.isEmpty()) return false;
        boolean canInteract = false;
        for (Object target : targets) {
            if (target instanceof Member) {
                if (member.canInteract((Member) target)) {
                    canInteract = true;
                    break;
                }
            } else if (target instanceof Role) {
                if (member.canInteract((Role) target)) {
                    canInteract = true;
                    break;
                }
            }
        }

        return isNegated() != canInteract;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "";
    }
}
