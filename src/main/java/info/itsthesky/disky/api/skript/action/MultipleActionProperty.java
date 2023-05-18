package info.itsthesky.disky.api.skript.action;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.api.changers.ChangeablePropertyExpression;
import info.itsthesky.disky.api.skript.EasyElement;
import info.itsthesky.disky.core.Bot;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public abstract class MultipleActionProperty<E, T extends AuditableRestAction<E>, O> extends ChangeablePropertyExpression<Object, O> {

    public void updateEntity(T newAction, Event event) {
        getExpr().change(event, newAction == null ? new Object[0] : new Object[] {newAction}, Changer.ChangeMode.SET);
    }

    @Override
    public void change(Event e, Object[] delta, Bot bot, Changer.ChangeMode mode) {
        if (EasyElement.isValid(delta))
            return;
        final O[] value = (O[]) delta;
        final Object entity = EasyElement.parseSingle(getExpr(), e, null);
        try {
            change((E) entity, value);
        } catch (ClassCastException ex) {
            updateEntity(change((T) entity, value), e);
        }
    }

    public abstract void change(E role, O[] value);

    public abstract T change(T action, O[] value);

    public abstract O[] get(E role);

    @Override
    protected O @NotNull [] get(@NotNull Event e, Object @NotNull [] source) {
        return (O[]) Arrays.stream(source).map(object -> object instanceof Role ? get((E) object) : null).toArray(Object[]::new);
    }

    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull SkriptParser.ParseResult parseResult) {
        setExpr(exprs[0]);
        return true;
    }
    
}
