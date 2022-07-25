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

@Name("Ban Member")
@Description({"Bans a member from a guild."})
@Examples({"ban event-member because of \"being lame\" and delete 10 days' worth of messages"})

public class BanMember extends SpecificBotEffect {

    static {
        Skript.registerEffect(
                BanMember.class,
                "[discord] ban [the] discord [member] %member% [(due to|because of|with [the] reason) %-string%] [and (delete|remove) %number% day[s] [worth ]of messages]"
        );
    }

    private Expression<Member> exprMember;
    private Expression<String> exprReason;
    private Expression<Integer> exprDays;

    @Override
    public boolean initEffect(Expression[] expr, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        exprMember = (Expression<Member>) expr[0];
        exprReason = (Expression<String>) expr[1];
        exprDays = (Expression<Integer>) expr[2];
        return true;
    }

    @Override
    public void runEffect(@NotNull Event e, Bot bot) {
        final Member member = parseSingle(exprMember, e, null);
        final @Nullable String reason = parseSingle(exprReason, e, null);
        final Integer days = parseSingle(exprDays, e, 0);

        if (member == null || bot == null) {
            restart();
            return;
        }

        member.ban(days, reason).queue(
                v -> restart(),
                ex -> {
                    DiSky.getErrorHandler().exception(e, ex);
                    restart();
                }
           );
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "ban " + exprMember.toString(e, debug) + " with reason " + exprReason.toString(e, debug) + " with" + (exprDays == null ? 0 : exprDays) + " days of messages deleted";
    }
}
