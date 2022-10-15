package info.itsthesky.disky.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.api.skript.SpecificBotEffect;
import info.itsthesky.disky.core.Bot;
import net.dv8tion.jda.api.entities.Member;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Kick Member")
@Description("Kick a specific member out of its guild. You can also specify a reason if needed.")
@Examples("kick discord event-member due to \"ur bad guys!\"")
public class KickMember extends SpecificBotEffect {

    static {
        Skript.registerEffect(
                KickMember.class,
                "kick [the] discord [member] %member% [(due to|because of|with [the] reason) %-string%]"
        );
    }

    private Expression<Member> exprMember;
    private Expression<String> exprReason;

    @Override
    public boolean initEffect(Expression @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull SkriptParser.ParseResult parseResult) {
        exprMember = (Expression<Member>) exprs[0];
        exprReason = (Expression<String>) exprs[1];
        return true;
    }

    @Override
    public void runEffect(@NotNull Event e, final Bot bot) {
        Member member = exprMember.getSingle(e);
        final @Nullable String reason = parseSingle(exprReason, e, null);

        if (member == null || bot == null) {
            restart();
            return;
        }

        member.getGuild().retrieveMemberById(member.getId()).queue(m -> {
            m.kick(reason).queue(
                    (v) -> restart(),
                    ex -> {
                        DiSky.getErrorHandler().exception(e, ex);
                        restart();
                    }
            );
        });
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "kick " + exprMember.toString(e, debug) + (exprReason != null ? " because of " + exprReason.toString(e, debug) : "");
    }
}
