package info.itsthesky.disky.api.datastruct;

import ch.njol.skript.Skript;
import ch.njol.skript.config.Node;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.expressions.base.SectionExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.api.datastruct.base.DataStruct;
import info.itsthesky.disky.elements.sections.handler.DiSkyRuntimeHandler;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
