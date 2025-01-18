package net.itsthesky.disky.api.skript.reflects.state;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.config.Node;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import net.itsthesky.disky.api.skript.INodeHolder;
import net.itsthesky.disky.elements.changers.IAsyncChangeableExpression;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DiSkyStateProperty extends SimplePropertyExpression<Object, Boolean>
        implements IAsyncChangeableExpression, INodeHolder {

    public Node node;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        node = getParser().getNode();
        return super.init(expressions, matchedPattern, isDelayed, parseResult);
    }

    @Override
    @NotNull
    public Node getNode() {
        return node;
    }

    @Override
    @Nullable
    public Boolean convert(Object from) {
        throw new UnsupportedOperationException("This should never be called!");
    }

    @Override
    protected @NotNull String getPropertyName() {
        throw new UnsupportedOperationException("This should never be called!");
    }

    public void change0(Event event, Object[] delta, Changer.ChangeMode mode, boolean async) {
        throw new UnsupportedOperationException("This should never be called!");
    }

    @Override
    public Class<?> @NotNull [] acceptChange(Changer.@NotNull ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET || mode == Changer.ChangeMode.DELETE || mode == Changer.ChangeMode.RESET)
            return new Class[] {Boolean.class};

        return new Class[0];
    }

    @Override
    public @NotNull Class<? extends Boolean> getReturnType() {
        return Boolean.class;
    }

    @Override
    public void change(@NotNull Event event, Object @NotNull [] delta, Changer.@NotNull ChangeMode mode) {
        change0(event, delta, mode, false);
    }

    @Override
    public void changeAsync(Event e, Object[] delta, Changer.ChangeMode mode) {
        change0(e, delta, mode, true);
    }
}
