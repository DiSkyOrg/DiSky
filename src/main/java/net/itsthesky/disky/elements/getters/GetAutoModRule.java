package net.itsthesky.disky.elements.getters;

import ch.njol.skript.Skript;
import ch.njol.skript.config.Node;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import net.itsthesky.disky.DiSky;
import net.itsthesky.disky.elements.changers.IAsyncGettableExpression;
import net.itsthesky.disky.elements.sections.handler.DiSkyRuntimeHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.automod.AutoModRule;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

public class GetAutoModRule extends SimpleExpression<AutoModRule>
        implements IAsyncGettableExpression<AutoModRule> {

    static {
        Skript.registerExpression(
                GetAutoModRule.class,
                AutoModRule.class,
                ExpressionType.SIMPLE,
                "[the] [discord] automod rule with id %string% (from|in) [the] [guild] %guild%"
        );
    }

    private Expression<String> exprId;
    private Expression<Guild> exprGuild;
    private Node node;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        exprId = (Expression<String>) expressions[0];
        exprGuild = (Expression<Guild>) expressions[1];
        node = getParser().getNode();
        return true;
    }

    @Override
    protected AutoModRule @Nullable [] get(Event event) {
        DiSkyRuntimeHandler.validateAsync(false, node);
        return new AutoModRule[0];
    }

    @Override
    public AutoModRule[] getAsync(Event e) {
        final var id = exprId.getSingle(e);
        final var guild = exprGuild.getSingle(e);
        if (!DiSkyRuntimeHandler.checkSet(node, id, exprId, guild, exprGuild))
            return new AutoModRule[0];

        DiSky.debug("Getting automod rule with ID " + id + " in guild " + guild);
        return new AutoModRule[] {guild.retrieveAutoModRuleById(id).complete()};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends AutoModRule> getReturnType() {
        return AutoModRule.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "discord automod rule with id " + exprId.toString(event, debug) + " in guild " + exprGuild.toString(event, debug);
    }
}
