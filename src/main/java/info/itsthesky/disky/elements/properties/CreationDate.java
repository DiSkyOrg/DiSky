package info.itsthesky.disky.elements.properties;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.util.Date;
import net.dv8tion.jda.api.entities.ISnowflake;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.TimeZone;

@Name("Creation Date")
@Description({"Get the creation date (as Skript date) of any ISnowFlake entity, including, but not limited to:",
        "- Member",
        "- User",
        "- Role",
        "- Guild",
        "- Channel",
        "- etc...",
})
@Examples({"creation date of event-user",
        "created date of event-member"})
public class CreationDate extends SimplePropertyExpression<ISnowflake, Date> {

    static {
        register(
                CreationDate.class,
                Date.class,
                "creat(ion|ed) date",
                "guild/member/user/role/channel/message/emote/webhook"
        );
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "creation date";
    }

    @Override
    public @Nullable Date convert(ISnowflake o) {
        return new Date(o.getTimeCreated().toInstant().toEpochMilli(), TimeZone.getTimeZone("GMT"));
    }

    @Override
    public @NotNull Class<? extends Date> getReturnType() {
        return Date.class;
    }

}
