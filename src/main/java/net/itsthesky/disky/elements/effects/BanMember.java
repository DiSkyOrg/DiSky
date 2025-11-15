package net.itsthesky.disky.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.config.Node;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import net.itsthesky.disky.api.generator.SeeAlso;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.AsyncEffect;
import ch.njol.skript.util.Timespan;
import ch.njol.util.Kleenean;
import net.itsthesky.disky.elements.sections.handler.DiSkyRuntimeHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.UserSnowflake;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.TimeUnit;

@Name("Ban User")
@Description({"Ban a member or user ID from a guild, with optional reason and message deletion.",
	"Starting DiSky v4.20.0, you can specify a USER ID to ban a user without retrieving the member first."})
@Examples({"ban discord event-member because of \"being lame\" and delete 10 days worth of messages",
	"ban discord member \"00000000000\" from guild with id \"000\" due to \"being lame\""})
@Since("4.0.0")
@SeeAlso({Guild.class, Member.class})

public class BanMember extends AsyncEffect {

    static {
        Skript.registerEffect(
                BanMember.class,
                "[discord] ban [the] discord [member] %member% [(due to|because of|with [the] reason) %-string%] [and (delete|remove) %-timespan% [worth ]of messages]",
                "[discord] ban [the] discord [member] %string% (from|of) [the] [guild] %guild% [(due to|because of|with [the] reason) %-string%]"
        );
    }

    private Node node;
    private boolean usingUserId;
    private Expression<Object> exprTarget;
    private Expression<String> exprReason;

    // Member
    private Expression<Timespan> exprDays;
    // User ID
    private Expression<Guild> exprGuild;

    @Override
    public boolean init(Expression[] expr, int matchedPattern, Kleenean kleenean, ParseResult parseResult) {
        getParser().setHasDelayBefore(Kleenean.TRUE);
        usingUserId = matchedPattern == 1;
        node = getParser().getNode();

        exprTarget = (Expression<Object>) expr[0];

        if (usingUserId) {
            exprGuild = (Expression<Guild>) expr[1];
            exprReason = (Expression<String>) expr[2];
        } else {
            exprReason = (Expression<String>) expr[1];
            exprDays = (Expression<Timespan>) expr[2];
        }

        return true;
    }

    @Override
    public void execute(@NotNull Event e) {
        final Object target = exprTarget.getSingle(e);
        final @Nullable String reason = exprReason == null ? null : exprReason.getSingle(e);
        if (target == null)
            return;

        if (usingUserId) {
            final Guild guild = exprGuild.getSingle(e);
            if (guild == null) {
                DiSkyRuntimeHandler.error(new NullPointerException("DiSky cannot ban the user ID '"+target+"' because the given guild is null!"), node);
                return;
            }

            guild.ban(UserSnowflake.fromId((String) target), 0, TimeUnit.MILLISECONDS)
                    .reason(reason).complete();
        } else {
            final Member member = (Member) target;
            final Timespan timespan = exprDays.getSingle(e);
            member.ban(timespan == null ? 0 : (int) timespan.getAs(Timespan.TimePeriod.MILLISECOND), TimeUnit.MILLISECONDS)
                    .reason(reason).complete();
        }
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        if (usingUserId) {
            return "ban member " + exprTarget.toString(e, debug)
                    + " from guild " + exprGuild.toString(e, debug)
                    + (exprReason == null ? "" : " with reason " + exprReason.toString(e, debug));
        } else {
            return "ban member " + exprTarget.toString(e, debug)
                    + (exprReason == null ? "" : " with reason " + exprReason.toString(e, debug))
                    + " and delete " + exprDays.toString(e, debug)
                    + " worth of messages";
        }
    }
}
