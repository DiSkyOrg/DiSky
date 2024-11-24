package info.itsthesky.disky.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.AsyncEffect;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.DiSky;
import net.dv8tion.jda.api.entities.Member;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static info.itsthesky.disky.api.skript.EasyElement.parseSingle;

@Name("Mute Member")
@Description({"Mute or unmute a member in their guild."})
@Examples({"voice mute event-member",
        "unmute member event-member"})

public class MuteMember extends AsyncEffect {

    static {
        Skript.registerEffect(
                MuteMember.class,
                "[voice] mute [the] [discord] [member] %member%",
                "[voice] un[ |-]mute [the] [discord] [member] %member%"
        );
    }

    private Expression<Member> exprMember;
    private int matchedPattern;

    @Override
    public boolean init(Expression[] expr, int matchedPattern, Kleenean kleenean, ParseResult parseResult) {
        getParser().setHasDelayBefore(Kleenean.TRUE);

        exprMember = (Expression<Member>) expr[0];
        this.matchedPattern = matchedPattern;
        return true;
    }

    @Override
    public void execute(@NotNull Event e) {
        final Member member = parseSingle(exprMember, e, null);

        if (member == null)
            return;

        try {
            member.mute(pattern2bool(matchedPattern)).complete();
        } catch (Exception ex) {
            DiSky.getErrorHandler().exception(e, ex);
        }
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "mute " + exprMember.toString(e, debug);
    }

    private boolean pattern2bool(int v) {
        return v <= 0;
    }
}