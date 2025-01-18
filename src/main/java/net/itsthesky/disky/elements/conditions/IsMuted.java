package net.itsthesky.disky.elements.conditions;

import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.NotNull;

public class IsMuted extends PropertyCondition<Member> {

    static {
        register(
                IsMuted.class,
                PropertyType.BE,
                "[discord] [member] [(:self|:guild)] mute[d]",
                "member"
        );
    }

    public enum Type {
        MUTED,
        SELF_MUTED,
        GUILD_MUTED
    }

    private Type type;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.@NotNull ParseResult parseResult) {

        if (parseResult.hasTag("self")) {
            type = Type.SELF_MUTED;
        } else if (parseResult.hasTag("guild")) {
            type = Type.GUILD_MUTED;
        } else {
            type = Type.MUTED;
        }

        return super.init(exprs, matchedPattern, isDelayed, parseResult);
    }

    @Override
    public boolean check(Member member) {
        if (member.getVoiceState() == null)
            return false;

        switch (type) {
            case MUTED:
                return member.getVoiceState().isMuted();
            case SELF_MUTED:
                return member.getVoiceState().isSelfMuted();
            case GUILD_MUTED:
                return member.getVoiceState().isGuildMuted();
            default:
                return false;
        }
    }

    @Override
    protected String getPropertyName() {
        return type.name().toLowerCase().replace("_", " ");
    }

}
