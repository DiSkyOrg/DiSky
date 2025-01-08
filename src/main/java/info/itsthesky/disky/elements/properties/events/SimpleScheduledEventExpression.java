package info.itsthesky.disky.elements.properties.events;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import info.itsthesky.disky.elements.changers.IAsyncChangeableExpression;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import net.dv8tion.jda.api.requests.RestAction;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.ParameterizedType;

@SuppressWarnings("unchecked")
public abstract class SimpleScheduledEventExpression<T> extends SimplePropertyExpression<ScheduledEvent, T>
        implements IAsyncChangeableExpression {

    @Override
    public void change(@NotNull Event event, Object @NotNull [] delta, Changer.@NotNull ChangeMode mode) {
        ScheduledEvent entity = getExpr().getSingle(event);

        if (entity == null)
            return;
        change(entity, delta).queue();
    }

    @Override
    public void changeAsync(Event e, Object[] delta, Changer.ChangeMode mode) {
        ScheduledEvent entity = getExpr().getSingle(e);

        if (entity == null)
            return;
        change(entity, delta).complete();
    }

    @Override
    public Class<?> @NotNull [] acceptChange(Changer.@NotNull ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET)
            return new Class[]{getReturnType()};
        return null;
    }

    public abstract RestAction<?> change(ScheduledEvent entity, Object[] delta);

    @Override
    public @NotNull Class<? extends T> getReturnType() {
        return (Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
    }
}
