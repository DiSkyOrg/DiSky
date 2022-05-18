package info.itsthesky.disky.elements.properties.members;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.util.Date;
import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.TimeZone;

@Name("Member Status")
@Description({"Represent the member's online status.",
        "The online status is represented by the following values:",
        "• `online`",
        "• `offline`",
        "• `idle`",
        "• `do not disturb`",
        "• `invisible`"})
@Examples({"reply with member online status of event-member"})
public class MemberStatus extends MemberProperty<String> {

    static {
        register(
                MemberStatus.class,
                String.class,
                "[member] online[( |-)]status"
        );
    }

    @Override
    public @Nullable String convert(Member member) {
        return member.getOnlineStatus().name().toLowerCase(Locale.ROOT).replace("_", " ");
    }

    @Override
    public @NotNull Class<? extends String> getReturnType() {
        return String.class;
    }

}
