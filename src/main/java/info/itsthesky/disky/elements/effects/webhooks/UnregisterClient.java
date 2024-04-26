package info.itsthesky.disky.elements.effects.webhooks;

import ch.njol.skript.Skript;
import ch.njol.skript.config.Node;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.core.SkriptUtils;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UnregisterClient extends Effect {

    static {
        Skript.registerEffect(
                UnregisterClient.class,
                "unregister [the] [webhook] client (with [the] name|named) %string%"
        );
    }

    private Node node;
    private Expression<String> exprName;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.@NotNull ParseResult parseResult) {
        exprName = (Expression<String>) expressions[0];
        node = getParser().getNode();
        return true;
    }

    @Override
    protected void execute(@NotNull Event event) {
        final String name = exprName.getSingle(event);
        if (name == null)
            return;

        if (!DiSky.getWebhooksManager().isWebhookRegistered(name)) {
            SkriptUtils.error(node, "The webhook client named " + name + " isn't registered!");
            return;
        }

        DiSky.getWebhooksManager().unregisterWebhook(name);
    }

    @Override
    public @NotNull String toString(@Nullable Event event, boolean debug) {
        return "unregister the webhook client named " + exprName.toString(event, debug);
    }
}
