package net.itsthesky.disky.api.skript.action;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.config.Node;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import net.itsthesky.disky.api.changers.ChangeablePropertyExpression;
import net.itsthesky.disky.api.skript.EasyElement;
import net.itsthesky.disky.api.skript.INodeHolder;
import net.itsthesky.disky.core.Bot;
import net.itsthesky.disky.elements.changers.IAsyncChangeableExpression;
import net.itsthesky.disky.elements.changers.IAsyncGettableExpression;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;

@SuppressWarnings("unchecked")
public abstract class ActionProperty<E, T extends AuditableRestAction, O>
        extends ChangeablePropertyExpression<Object, O>
        implements IAsyncChangeableExpression, IAsyncGettableExpression<O>, INodeHolder {

    protected Node node;
    public void updateEntity(T newAction, Event event) {
        getExpr().change(event, newAction == null ? new Object[0] : new Object[] {newAction}, Changer.ChangeMode.SET);
    }

    @Override
    public void change(Event e, Object[] delta, Bot bot, Changer.ChangeMode mode) {
        if (!EasyElement.isValid(delta))
            return;
        final O value = (O) delta[0];
        final Object entity = EasyElement.parseSingle(getExpr(), e, null);
        if (entity instanceof AuditableRestAction) {
            updateEntity(change((T) entity, value), e);
        } else {
            change((E) entity, value, false);
        }
    }

    @Override
    public void changeAsync(Event e, Object[] delta, Changer.ChangeMode mode) {
        if (!EasyElement.isValid(delta))
            return;
        final O value = (O) delta[0];
        final Object entity = EasyElement.parseSingle(getExpr(), e, null);
        if (entity instanceof AuditableRestAction) {
            ((AuditableRestAction<?>) entity).queue();
        } else {
            change((E) entity, value, true);
        }
    }

    public abstract void change(E role, O value, boolean async);

    public abstract T change(T action, O value);

    public abstract O get(E role, boolean async);

    @Override
    protected O @NotNull [] get(@NotNull Event e, Object @NotNull [] source) {
        return (O[]) Arrays.stream(source).map(object -> get((E) object, false)).toArray(Object[]::new);
    }

    @Override
    public O[] getAsync(Event e) {
        final Expression<?> expr = getExpr();
        return (O[]) Arrays.stream(expr.getArray(e)).map(object -> get((E) object, true)).toArray(Object[]::new);
    }

    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
        setExpr(exprs[0]);
        node = getParser().getNode();
        return true;
    }

    public Class<E> getEntityClass() {
        return (Class<E>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
    }

    public Class<T> getActionClass() {
        return (Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[1];
    }

    @Override
    @NotNull
    public Node getNode() {
        return node;
    }
}
