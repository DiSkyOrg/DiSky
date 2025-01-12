package info.itsthesky.disky.api.datastruct;

import ch.njol.skript.config.SectionNode;
import ch.njol.skript.expressions.base.SectionExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.api.datastruct.base.DataStruct;
import info.itsthesky.disky.elements.sections.handler.DiSkyRuntimeHandler;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseDataStructElement<T, D extends DataStruct<T>> extends SectionExpression<T> {

    protected EntryContainer container;

    @Override
    public boolean init(Expression<?>[] expressions, int pattern, Kleenean delayed,
                        SkriptParser.ParseResult result, @Nullable SectionNode node,
                        @Nullable List<TriggerItem> triggerItems) {
        final var validator = DataStructureFactory.createValidator(getDataStructClass());
        container = validator.validate(node);

        final var presentNodes = new ArrayList<String>();
        if (container != null) {
            for (final var entryData : validator.getEntryData()) {
                if (container.hasEntry(entryData.getKey()))
                    presentNodes.add(entryData.getKey());
            }
        }

        final var errorMessage = DataStructureFactory.preValidate(getDataStructClass(), presentNodes);
        if (errorMessage != null) {
            // Skript.error(errorMessage);
            // like wtf skript? why don't you want my error message? ;-;
            DiSkyRuntimeHandler.error(new IllegalStateException(errorMessage), node);
            return false;
        }

        return container != null;
    }

    @Override
    protected T @Nullable [] get(Event event) {
        if (container == null)
            return null;

        try {
            final var result = DataStructureFactory.createDataStructure(getDataStructClass(), container, event, null);
            return (T[]) new Object[] {result};
        } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    public abstract Class<D> getDataStructClass();

}
