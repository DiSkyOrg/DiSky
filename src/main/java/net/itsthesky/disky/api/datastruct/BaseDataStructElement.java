package net.itsthesky.disky.api.datastruct;

import ch.njol.skript.config.SectionNode;
import ch.njol.skript.expressions.base.SectionExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.util.Kleenean;
import net.itsthesky.disky.api.datastruct.base.DataStruct;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class BaseDataStructElement<T, D extends DataStruct<T>> extends SectionExpression<T> {

    protected DataStructureFactory.DataStructureParseResult parseResult;

    @Override
    public boolean init(Expression<?>[] givenExprs, int pattern, Kleenean delayed,
                        SkriptParser.ParseResult result, @Nullable SectionNode node,
                        @Nullable List<TriggerItem> triggerItems) {
        this.parseResult = DataStructureFactory.initDataStructure(getDataStructClass(), node);
        return this.parseResult != null;
    }

    @Override
    protected T @Nullable [] get(Event event) {
        if (parseResult == null)
            return null;

        try {
            final var result = DataStructureFactory.createDataStructure(getDataStructClass(), parseResult, event, null);
            return (T[]) new Object[] {result};
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    public abstract Class<D> getDataStructClass();

}
