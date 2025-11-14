package net.itsthesky.disky.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.SeeAlso;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.AsyncEffect;
import ch.njol.util.Kleenean;
import net.itsthesky.disky.DiSky;
import net.itsthesky.disky.elements.sections.handler.DiSkyRuntimeHandler;
import net.dv8tion.jda.api.entities.Member;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.itsthesky.disky.api.skript.EasyElement.parseSingle;

@Name("Kick Member")
@Description("Kick a specific member out of its guild. You can also specify a reason if needed.")
@Examples("kick discord event-member due to \"ur bad guys!\"")
@Since("4.0.0")
@SeeAlso(Member.class)
public class KickMember extends AsyncEffect {

    static {
        Skript.registerEffect(
                KickMember.class,
                "kick [the] discord [member] %member% [(due to|because of|with [the] reason) %-string%]"
        );
    }

    private Expression<Member> exprMember;
    private Expression<String> exprReason;

    @Override
    public boolean init(Expression @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
        getParser().setHasDelayBefore(Kleenean.TRUE);

        exprMember = (Expression<Member>) exprs[0];
        exprReason = (Expression<String>) exprs[1];
        return true;
    }

    @Override
    public void execute(@NotNull Event e) {
        Member member = exprMember.getSingle(e);
        final @Nullable String reason = parseSingle(exprReason, e, null);

        if (member == null)
            return;

        try {
            member.kick().reason(reason).complete();
        } catch (Exception ex) {
            DiSkyRuntimeHandler.error((Exception) ex);
        }
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "kick " + exprMember.toString(e, debug) + (exprReason != null ? " because of " + exprReason.toString(e, debug) : "");
    }
}
