package info.itsthesky.disky.elements.conditions;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.api.skript.PropertyCondition;
import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.NotNull;

public class IsDeafen extends PropertyCondition<Member> {

    static {
        register(
                IsDeafen.class,
                PropertyType.BE,
                "[discord] [member] [:self|:guild] deafen[ed]",
                "member"
        );
    }

    public enum Type {
        DEAFENED,
        SELF_DEAFENED,
        GUILD_DEAFENED
    }

    private Type type;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.@NotNull ParseResult parseResult) {

        if (parseResult.hasTag("self")) {
            type = Type.SELF_DEAFENED;
        } else if (parseResult.hasTag("guild")) {
            type = Type.GUILD_DEAFENED;
        } else {
            type = Type.DEAFENED;
        }

        return super.init(exprs, matchedPattern, isDelayed, parseResult);
    }

    @Override
    public boolean check(Member member) {
        if (member.getVoiceState() == null)
            return false;

        switch (type) {
            case DEAFENED:
                return member.getVoiceState().isDeafened();
            case SELF_DEAFENED:
                return member.getVoiceState().isSelfDeafened();
            case GUILD_DEAFENED:
                return member.getVoiceState().isGuildDeafened();
            default:
                return false;
        }
    }

    @Override
    protected String getPropertyName() {
        return type.name().toLowerCase().replace("_", " ");
    }

}
