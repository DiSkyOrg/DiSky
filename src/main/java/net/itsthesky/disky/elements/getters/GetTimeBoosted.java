package net.itsthesky.disky.elements.getters;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.util.Date;
import net.dv8tion.jda.api.entities.Member;
import net.itsthesky.disky.core.SkriptUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.time.OffsetDateTime;

@Name("Member Boost Date")
@Description("Get the date when a member started boosting the server. Returns null if the member is not boosting.")
@Examples({
        "set {_time} to boost date of event-member",
        "send \"Member has been boosting since: %boost date of {_member}%\"",
        "This expression cannot be changed."
})
@Since("4.0.0")
public class GetTimeBoosted extends SimplePropertyExpression<Member, Date> {

    static {
        register(GetTimeBoosted.class,
                Date.class,
                "[discord] boost[ing] date", "member");
    }

    @Override
    public @Nullable Date convert(Member member) {
        return SkriptUtils.convertDateTime(member.getTimeBoosted());
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "boost date";
    }

    @Override
    public @NotNull Class<? extends Date> getReturnType() {
        return Date.class;
    }
}
