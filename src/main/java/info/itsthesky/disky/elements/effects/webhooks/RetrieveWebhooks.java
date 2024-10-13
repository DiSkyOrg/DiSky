package info.itsthesky.disky.elements.effects.webhooks;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.config.Node;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.util.AsyncEffect;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.elements.sections.handler.DiSkyRuntimeHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.attribute.IWebhookContainer;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RetrieveWebhooks extends AsyncEffect {

    static {
        Skript.registerEffect(
                RetrieveWebhooks.class,
                "retrieve [the] webhook[s] (from|of) [the] [channel] %channel/textchannel/guild% and store (them|the webhook[s]) in %objects%"
        );
    }

    private Expression<Channel> exprChannel;
    private Expression<Object> exprVar;
    private Node node;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.@NotNull ParseResult parseResult) {
        exprChannel = (Expression<Channel>) expressions[0];
        exprVar = (Expression<Object>) expressions[1];
        node = getParser().getNode();

        return Changer.ChangerUtils.acceptsChange(exprVar, Changer.ChangeMode.SET, Webhook.class);
    }

    @Override
    protected void execute(@NotNull Event event) {
        final var target = exprChannel.getSingle(event);
        final var var = exprVar.getSingle(event);
        if (target == null || var == null)
            return;

        if (!(target instanceof IWebhookContainer) && !(target instanceof Guild)) {
            DiSkyRuntimeHandler.error(new IllegalArgumentException("The entity " + target + " is not a webhook container (= text channel) or guild!"), node);
            return;
        }

        try {
            final List<Webhook> webhooks;
            if (target instanceof IWebhookContainer)
                webhooks = ((IWebhookContainer) target).retrieveWebhooks().complete();
            else
                webhooks = ((Guild) target).retrieveWebhooks().complete();

            exprVar.change(event, webhooks.toArray(new Webhook[0]), Changer.ChangeMode.SET);
        } catch (Exception ex) {
            DiSkyRuntimeHandler.error(ex, node);

            exprVar.change(event, new Webhook[0], Changer.ChangeMode.SET);
        }
    }

    @Override
    public @NotNull String toString(@Nullable Event event, boolean debug) {
        return "retrieve webhooks from entity " + exprChannel.toString(event, debug)
                + " and store them in " + exprVar.toString(event, debug);
    }
}
