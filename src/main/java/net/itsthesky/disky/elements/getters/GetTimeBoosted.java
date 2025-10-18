package net.itsthesky.disky.elements.getters;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.util.Date;
import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.time.OffsetDateTime;

@Name("Member Boost Time")
@Description("Get the time when a member started boosting the server. Returns null if the member is not boosting.")
@Examples({
        "set {_time} to boost time of event-member",
        "send \"Member has been boosting since: %boost time of {_member}%\"",
        "This expression cannot be changed."
})
public class GetTimeBoosted extends SimplePropertyExpression<Member, Date> {

    static {
        register(GetTimeBoosted.class,
                Date.class,
                "[discord] boost[ing] (time|date)", "member");
    }

    @Override
    public @Nullable Date convert(Member m) {
        OffsetDateTime boostTime = m.getTimeBoosted();
        if (boostTime == null) {
            return null;
        }
        return new Date(boostTime.toInstant().toEpochMilli());
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "boost time";
    }

    @Override
    public @NotNull Class<? extends Date> getReturnType() {
        return Date.class;
    }
}