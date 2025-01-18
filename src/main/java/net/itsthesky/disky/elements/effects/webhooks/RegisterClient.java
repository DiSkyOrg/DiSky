package net.itsthesky.disky.elements.effects.webhooks;

import ch.njol.skript.Skript;
import ch.njol.skript.config.Node;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import net.itsthesky.disky.DiSky;
import net.itsthesky.disky.core.Bot;
import net.itsthesky.disky.core.SkriptUtils;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RegisterClient extends Effect {

    static {
        Skript.registerEffect(
                RegisterClient.class,
                "register [a] [new] webhook[s] [client] (in|using) [the] [bot] %bot% (with [the] name|named) %string% (and|with) [the] [webhook] url %string%"
        );
    }

    private Node node;
    private Expression<Bot> exprBot;
    private Expression<String> exprName;
    private Expression<String> exprURL;

    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.@NotNull ParseResult parseResult) {
        exprBot = (Expression<Bot>) expressions[0];
        exprName = (Expression<String>) expressions[1];
        exprURL = (Expression<String>) expressions[2];
        node = getParser().getNode();
        return true;
    }

    @Override
    protected void execute(@NotNull Event event) {
        final Bot bot = exprBot.getSingle(event);
        final String name = exprName.getSingle(event);
        final String url = exprURL.getSingle(event);
        if (bot == null || name == null || url == null)
            return;

        if (DiSky.getWebhooksManager().isWebhookRegistered(name)) {
            SkriptUtils.error(node, "The webhook client named " + name + " is already registered!");
            return;
        }

        DiSky.getWebhooksManager().registerWebhook(bot, name, url);
    }

    @Override
    public @NotNull String toString(@Nullable Event event, boolean debug) {
        return "register a new webhook client in bot " + exprBot.toString(event, debug) + " with name " + exprName.toString(event, debug) + " and url " + exprURL.toString(event, debug);
    }
}
