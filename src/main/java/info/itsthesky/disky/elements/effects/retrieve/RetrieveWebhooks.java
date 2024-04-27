package info.itsthesky.disky.elements.effects.retrieve;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.util.AsyncEffect;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.DiSky;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RetrieveWebhooks extends AsyncEffect {

    static {
        Skript.registerEffect(
                RetrieveWebhooks.class,
                "retrieve [all] [discord] webhooks (of|from) [the] [(guild|channel)] %guild/textchannel% and store (them|the webhooks) in %~objects%"
        );
    }

    private Expression<Object> exprEntity;
    private Expression<Object> exprResult;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.@NotNull ParseResult parseResult) {
        exprEntity = (Expression<Object>) expressions[0];
        exprResult = (Expression<Object>) expressions[1];
        return Changer.ChangerUtils.acceptsChange(exprResult, Changer.ChangeMode.SET, Webhook[].class);
    }

    @Override
    protected void execute(@NotNull Event event) {
        final Object entity = exprEntity.getSingle(event);
        if (entity == null)
            return;

        final Webhook[] webhooks;
        try {
            if (entity instanceof Guild)
                webhooks = ((Guild) entity).retrieveWebhooks().complete().toArray(new Webhook[0]);
            else if (entity instanceof TextChannel)
                webhooks = ((TextChannel) entity).retrieveWebhooks().complete().toArray(new Webhook[0]);
            else
                return;
        } catch (Exception ex) {
            DiSky.getErrorHandler().exception(event, ex);
            return;
        }

        exprResult.change(event, webhooks, Changer.ChangeMode.SET);
    }

    @Override
    public @NotNull String toString(@Nullable Event event, boolean debug) {
        return "retrieve all discord webhooks of " + exprEntity.toString(event, debug) + " and store them in " + exprResult.toString(event, debug);
    }
}
