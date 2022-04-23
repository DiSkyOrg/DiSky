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

@Name("Mute Member")
@Description({"Mute or unmute a member in their guild."})
@Examples({"voice mute event-member",
        "unmute member event-member"})

public class MuteMember extends SpecificBotEffect {

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
    public boolean initEffect(Expression[] expr, int matchedPattern, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        exprMember = (Expression<Member>) expr[0];
        this.matchedPattern = matchedPattern;
        return true;
    }

    @Override
    public void runEffect(Event e, Bot bot) {
        final Member member = parseSingle(exprMember, e, null);

        if (member == null || bot == null) {
            restart();
            return;
        }

        member.mute(pattern2bool(matchedPattern)).queue(
                (v) -> restart(),
                ex -> {
                    DiSky.getErrorHandler().exception(e, ex);
                    restart();
                }
        );
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "mute " + exprMember.toString(e, debug);
    }

    private boolean pattern2bool(int v) {
        if (v <= 0) return true;
        return false;
    }
}