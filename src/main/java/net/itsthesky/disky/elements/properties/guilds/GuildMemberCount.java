package net.itsthesky.disky.elements.properties.guilds;

import ch.njol.skript.config.Node;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import net.itsthesky.disky.elements.sections.handler.DiSkyRuntimeHandler;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GuildMemberCount extends SimplePropertyExpression<Guild, Number> {

    static {
        register(
                GuildMemberCount.class,
                Number.class,
                "member count",
                "guild"
        );
    }

    private Node node;

    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.@NotNull ParseResult parseResult) {
        node = getParser().getNode();
        return super.init(expressions, matchedPattern, isDelayed, parseResult);
    }

    @Override
    @Nullable
    public Number convert(Guild from) {
        if (from == null)
        {
            DiSkyRuntimeHandler.exprNotSet(node, getExpr());
            return 0;
        }

        return from.getMemberCount();
    }

    @Override
    public @NotNull Class<? extends Number> getReturnType() {
        return Number.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "member count";
    }
}
