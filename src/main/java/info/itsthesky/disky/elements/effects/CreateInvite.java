package info.itsthesky.disky.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.Variable;
import ch.njol.skript.util.AsyncEffect;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.api.skript.SpecificBotEffect;
import info.itsthesky.disky.core.Bot;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.channel.attribute.IInviteContainer;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static info.itsthesky.disky.api.skript.EasyElement.anyNull;
import static info.itsthesky.disky.api.skript.EasyElement.parseSingle;

public class CreateInvite extends AsyncEffect {

    static {
        Skript.registerEffect(
                CreateInvite.class,
                "(make|create) [the] [new] invite in [the] [(guild|channel)] %guild/channel% [with max us(e|age)[s] %-number%] [with max (time|age) %-number%] and store (it|the invite) in %~objects%"
        );
    }

    private Expression<Object> exprEntity;
    private Expression<Number> exprMaxUses;
    private Expression<Number> exprMaxAge;
    private Expression<Object> exprResult;

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "create invite in "+ exprEntity.toString(e, debug)
                + " with max uses " + exprMaxUses.toString(e, debug)
                + " with max age " + exprMaxAge.toString(e, debug)
                + " and store it in " + exprResult.toString(e, debug);
    }

    @Override
    public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        getParser().setHasDelayBefore(Kleenean.TRUE);

        exprEntity = (Expression<Object>) exprs[0];

        exprMaxUses = (Expression<Number>) exprs[1];
        exprMaxAge = (Expression<Number>) exprs[2];

        exprResult = (Expression<Object>) exprs[3];

        return Changer.ChangerUtils.acceptsChange(exprResult, Changer.ChangeMode.SET, Invite.class);
    }

    @Override
    public void execute(@NotNull Event e) {
        Object entity = exprEntity.getSingle(e);

        Number maxUses = parseSingle(exprMaxUses, e, null);
        Number maxAge = parseSingle(exprMaxAge, e, null);

        if (anyNull(this, entity))
            return;
        if (!(entity instanceof IInviteContainer))
            return;

        final Invite invite;
        try {

            invite = ((IInviteContainer) entity).createInvite()
                    .setMaxUses(maxUses == null ? null : maxUses.intValue())
                    .setMaxAge(maxAge == null ? null : maxAge.intValue())
                    .complete();

        } catch (Exception ex) {
            DiSky.getErrorHandler().exception(e, ex);
            return;
        }

        exprResult.change(e, new Invite[] {invite}, Changer.ChangeMode.SET);
    }
}
