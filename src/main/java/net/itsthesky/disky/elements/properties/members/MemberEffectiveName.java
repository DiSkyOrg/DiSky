package net.itsthesky.disky.elements.properties.members;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Member Effective Name")
@Description({"Simple way to get the effective name of a member in a guild:",
"If the nickname is not set, it will return the discord name of the member, else its nickname."})
@Examples({"reply with effective name of event-member"})
public class MemberEffectiveName extends MemberProperty<String> {

    static {
        register(
                MemberEffectiveName.class,
                String.class,
                "effective name[s]"
        );
    }

    @Override
    public @Nullable String convert(Member member) {
        return member.getEffectiveName();
    }

    @Override
    public @NotNull Class<? extends String> getReturnType() {
        return String.class;
    }

}
