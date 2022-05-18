package info.itsthesky.disky.elements.properties.members;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.util.Date;
import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.TimeZone;

@Name("Member Join Date")
@Description({"Represent the skript's date of the member's join date.",
        "It cannot be changed.",
        "This is a specific element of the bot, so it can be used in the bots event."})
@Examples({"reply with member join date of event-member"})
public class MemberJoinDate extends MemberProperty<Date> {

    static {
        register(
                MemberJoinDate.class,
                Date.class,
                "[member] join date"
        );
    }

    @Override
    public @Nullable Date convert(Member member) {
        return new Date(member.getTimeJoined().toInstant().toEpochMilli(), TimeZone.getTimeZone("GMT"));
    }

    @Override
    public @NotNull Class<? extends Date> getReturnType() {
        return Date.class;
    }

}
