package info.itsthesky.disky.elements.properties.members;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import info.itsthesky.disky.api.skript.EasyElement;
import info.itsthesky.disky.core.Utils;
import net.dv8tion.jda.api.entities.Member;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Member Nickname")
@Description({"Represent the member nickname. Can be none if the member doesn't have any nickname currently.",
"USe 'effective name' expression to get member's name of its nickname is not set."})
@Examples({"reply with member nickname of event-member",
"set member nickname of event-member to \"ayo?!\""})
public class MemberNickname extends MemberProperty<String> {

    static {
        register(
                MemberNickname.class,
                String.class,
                "nick[( |-)]name[s]"
        );
    }

    @Override
    public @Nullable String convert(Member member) {
        return member.getNickname();
    }

    @Override
    public @NotNull Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public Class<?> @NotNull [] acceptChange(@NotNull Changer.ChangeMode mode) {
        if (EasyElement.equalAny(mode, Changer.ChangeMode.SET, Changer.ChangeMode.RESET))
            return new Class[] {String.class};
        return new Class[0];
    }

    @Override
    public void change(@NotNull Event e, @NotNull Object[] delta, @NotNull Changer.ChangeMode mode) {
        if (!EasyElement.isValid(delta))
            return;
        final Member member = EasyElement.parseSingle(getExpr(), e, null);
        final String name = (String) delta[0];
        if (EasyElement.anyNull(this, member, name))
            return;
        Utils.catchAction(member.modifyNickname(name), e);
    }

}
