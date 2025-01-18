package net.itsthesky.disky.api.skript.reflects.state;

import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.config.Node;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import net.itsthesky.disky.api.skript.INodeHolder;
import org.jetbrains.annotations.NotNull;

public class DiSkyStateCondition extends PropertyCondition<Object> implements INodeHolder {

    public Node node;
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.@NotNull ParseResult parseResult) {
        node = getParser().getNode();
        return super.init(exprs, matchedPattern, isDelayed, parseResult);
    }

    @Override
    @NotNull
    public Node getNode() {
        return node;
    }

    @Override
    public boolean check(Object entity) {
        throw new UnsupportedOperationException("This should never be called!");
    }

    @Override
    protected String getPropertyName() {
        throw new UnsupportedOperationException("This should never be called!");
    }

}
