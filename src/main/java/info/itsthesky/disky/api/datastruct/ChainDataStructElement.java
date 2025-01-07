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

import java.util.List;

public abstract class ChainDataStructElement<F, T, D extends DataStruct<T>>
        extends SectionExpression<F> implements IAsyncGettableExpression<F> {

    protected EntryContainer container;
    protected Node node;

    @Override
    public boolean init(Expression<?>[] expressions,
                        int pattern,
                        Kleenean delayed,
                        SkriptParser.ParseResult result,
                        @Nullable SectionNode node,
                        @Nullable List<TriggerItem> triggerItems) {
        this.node = getParser().getNode();

        final var validator = DataStructureFactory.createValidator(getDataStructClass());
        container = validator.validate(node);

        return container != null;
    }

    @Override
    protected F @Nullable [] get(Event event) {
        DiSkyRuntimeHandler.validateAsync(false, this.node);
        return null;
    }

    @Override
    public F[] getAsync(Event event) {
        if (container == null)
            return null;

        final var original = getOriginalInstance(event);
        if (original == null)
            return null;

        try {
            final var result = DataStructureFactory.createDataStructure(getDataStructClass(), container, event, original);
            return (F[]) new Object[] {applyChanges(event, result)};
        } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | java.lang.reflect.InvocationTargetException e) {
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
