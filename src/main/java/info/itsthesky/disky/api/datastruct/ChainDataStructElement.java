package info.itsthesky.disky.api.datastruct;

import ch.njol.skript.config.Node;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.expressions.base.SectionExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.api.datastruct.base.DataStruct;
import info.itsthesky.disky.elements.changers.IAsyncGettableExpression;
import info.itsthesky.disky.elements.sections.handler.DiSkyRuntimeHandler;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ChainDataStructElement<F, T, D extends DataStruct<T>>
        extends SectionExpression<F> implements IAsyncGettableExpression<F> {

    protected DataStructureFactory.DataStructureParseResult parseResult;
    protected Node node;

    @Override
    public boolean init(Expression<?>[] givenExprs, int pattern, Kleenean delayed,
                        SkriptParser.ParseResult result, @Nullable SectionNode node,
                        @Nullable List<TriggerItem> triggerItems) {
        this.node = getParser().getNode();
        this.parseResult = DataStructureFactory.initDataStructure(getDataStructClass(), node);
        return this.parseResult != null;
    }

    @Override
    protected F @Nullable [] get(Event event) {
        DiSkyRuntimeHandler.validateAsync(false, this.node);
        return null;
    }

    @Override
    public F[] getAsync(Event event) {

        final var original = getOriginalInstance(event);
        if (original == null)
            return null;

        try {
            final var result = DataStructureFactory.createDataStructure(getDataStructClass(), parseResult, event, null);
            return (F[]) new Object[] {applyChanges(event, (T) result)};
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    public abstract Class<D> getDataStructClass();

    public abstract T getOriginalInstance(@NotNull Event event);

    public abstract F applyChanges(@NotNull Event event, @NotNull T edited);
}
