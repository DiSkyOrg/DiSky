package net.itsthesky.disky.elements.properties.members;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import net.itsthesky.disky.DiSky;
import net.itsthesky.disky.api.skript.EasyElement;
import net.itsthesky.disky.elements.changers.IAsyncChangeableExpression;
import net.dv8tion.jda.api.entities.Member;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Member Nickname")
@Description({"Represent the member nickname. Can be none if the member doesn't have any nickname currently.",
"USe 'effective name' expression to get member's name of its nickname is not set."})
@Examples({"reply with member nickname of event-member",
"set member nickname of event-member to \"ayo?!\""})
public class MemberNickname extends MemberProperty<String>
        implements IAsyncChangeableExpression {

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

    public void change(Event e, Object[] delta, Changer.ChangeMode mode, boolean async) {
        if (!EasyElement.isValid(delta))
            return;
        final Member member = EasyElement.parseSingle(getExpr(), e, null);
        final String name = (String) delta[0];
        if ((mode != Changer.ChangeMode.RESET && mode != Changer.ChangeMode.DELETE) && EasyElement.anyNull(this, name)) return;

        if (!member.getGuild().getSelfMember().canInteract(member)) {
            DiSky.getInstance().getLogger()
                    .warning("The bot '"+member.getGuild().getSelfMember().getUser().getEffectiveName()+"' cannot interact with the member '"+member.getUser().getEffectiveName()+"' to change his nickname! For more information about that, please check DiSky's FAQ: https://disky.me/wiki/getting-started/faq/");
            return;
        }

        final var action = member.modifyNickname(name);
        if (async) action.complete();
        else action.queue();
    }

    @Override
    public void change(@NotNull Event e, @NotNull Object[] delta, @NotNull Changer.ChangeMode mode) {
        change(e, delta, mode, false);
    }

    @Override
    public void changeAsync(Event e, Object[] delta, Changer.ChangeMode mode) {
        change(e, delta, mode, true);
    }
}
