package net.itsthesky.disky.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.config.Node;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.AsyncEffect;
import ch.njol.skript.util.Timespan;
import ch.njol.util.Kleenean;
import net.itsthesky.disky.DiSky;
import net.itsthesky.disky.core.SkriptUtils;
import net.itsthesky.disky.elements.sections.handler.DiSkyRuntimeHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.channel.attribute.IInviteContainer;
import net.dv8tion.jda.api.requests.restaction.InviteAction;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.itsthesky.disky.api.skript.EasyElement.anyNull;
import static net.itsthesky.disky.api.skript.EasyElement.parseSingle;

public class CreateInvite extends AsyncEffect {

    static {
        Skript.registerEffect(
                CreateInvite.class,
                "(make|create) [the] [new] invite in [the] [(guild|channel)] %guild/channel% [with max us(e|age)[s] %-number%] [with max (time|age) %-timespan%] and store (it|the invite) in %~objects%"
        );
    }

    private Node node;
    private Expression<Object> exprEntity;
    private Expression<Number> exprMaxUses;
    private Expression<Timespan> exprMaxAge;
    private Expression<Object> exprResult;

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "create invite in "+ exprEntity.toString(e, debug)
                + " with max uses " + exprMaxUses.toString(e, debug)
                + " with max age " + exprMaxAge.toString(e, debug)
                + " and store it in " + exprResult.toString(e, debug);
    }

    @Override
    public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, ParseResult parseResult) {
        getParser().setHasDelayBefore(Kleenean.TRUE);

        exprEntity = (Expression<Object>) exprs[0];

        exprMaxUses = (Expression<Number>) exprs[1];
        exprMaxAge = (Expression<Timespan>) exprs[2];

        exprResult = (Expression<Object>) exprs[3];

        node = getParser().getNode();

        return Changer.ChangerUtils.acceptsChange(exprResult, Changer.ChangeMode.SET, Invite.class);
    }

    @Override
    public void execute(@NotNull Event e) {
        Object entity = exprEntity.getSingle(e);

        Number maxUses = parseSingle(exprMaxUses, e, null);
        Timespan maxAge = parseSingle(exprMaxAge, e, null);

        if (anyNull(this, entity))
            return;
        if (!(entity instanceof IInviteContainer) && !(entity instanceof Guild))
        {
            SkriptUtils.error(node, "The entity must be a guild or a channel to create an invite!");
            return;
        }

        if (entity instanceof Guild) {
            SkriptUtils.warning(node, "Creating an invite via a guild is now Deprecated. This will use the default channel of the guild. Please use a channel instead!");

            if (((Guild) entity).getDefaultChannel() == null) {
                Skript.error("The default channel of the guild is null, so the invite cannot be created. Please specify a channel!");
                return;
            }
        }

        final Invite invite;
        try {

            final InviteAction inviteAction = entity instanceof Guild
                    ? ((Guild) entity).getDefaultChannel().createInvite()
                    : ((IInviteContainer) entity).createInvite();

            invite = inviteAction
                    .setMaxUses(maxUses == null ? null : maxUses.intValue())
                    .setMaxAge(maxAge == null ? null : (int) (maxAge.getAs(Timespan.TimePeriod.MILLISECOND) / 1000))
                    .complete();

        } catch (Exception ex) {
            DiSkyRuntimeHandler.error((Exception) ex);
            return;
        }

        exprResult.change(e, new Invite[] {invite}, Changer.ChangeMode.SET);
    }
}
