package info.itsthesky.disky.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.util.AsyncEffect;
import ch.njol.skript.util.Timespan;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.api.skript.EasyElement;
import info.itsthesky.disky.api.skript.SpecificBotEffect;
import info.itsthesky.disky.core.Bot;
import net.dv8tion.jda.api.entities.Member;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.TimeUnit;

@Name("Ban Member")
@Description({"Bans a member from a guild."})
@Examples({"ban discord event-member because of \"being lame\" and delete 10 days worth of messages"})

public class BanMember extends AsyncEffect {

    static {
        Skript.registerEffect(
                BanMember.class,
                "[discord] ban [the] discord [member] %member% [(due to|because of|with [the] reason) %-string%] [and (delete|remove) %-timespan% [worth ]of messages]"
        );
    }

    private Expression<Member> exprMember;
    private Expression<String> exprReason;
    private Expression<Timespan> exprDays;

    @Override
    public boolean init(Expression[] expr, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        getParser().setHasDelayBefore(Kleenean.TRUE);

        exprMember = (Expression<Member>) expr[0];
        exprReason = (Expression<String>) expr[1];
        exprDays = (Expression<Timespan>) expr[2];

        return true;
    }

    @Override
    public void execute(@NotNull Event e) {
        final Member member = exprMember.getSingle(e);
        final @Nullable String reason = exprReason == null ? null : exprReason.getSingle(e);
        final Timespan timespan = exprDays == null ? null : exprDays.getSingle(e);

        if (member == null)
            return;

        try {
            member.ban(timespan == null ? 0 : (int) timespan.getMilliSeconds(), TimeUnit.MILLISECONDS).reason(reason).complete();
        } catch (Exception ex) {
            DiSky.getErrorHandler().exception(e, ex);
        }
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "ban " + exprMember.toString(e, debug) + " with reason " + exprReason.toString(e, debug) + " with" + (exprDays == null ? 0 : exprDays) + " days of messages deleted";
    }
}
