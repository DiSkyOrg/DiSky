package info.itsthesky.disky.api.datastruct;

import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.Section;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.api.datastruct.base.DataStruct;
import info.itsthesky.disky.elements.sections.handler.DiSkyRuntimeHandler;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;

import java.util.ArrayList;
import java.util.List;

public abstract class EditDataStructElement<T, D extends DataStruct<T>> extends Section {

    protected EntryContainer container;

    @Override
    public boolean init(Expression<?>[] expressions,
                        int matchedPattern, Kleenean isDelayed,
                        SkriptParser.ParseResult parseResult,
                        SectionNode node, List<TriggerItem> triggerItems) {
        final var validator = DataStructureFactory.createValidator(getDataStructClass());
        container = validator.validate(node);

        final var presentNodes = new ArrayList<String>();
        if (container != null) {
            for (final var entryData : validator.getEntryData()) {
                if (container.hasEntry(entryData.getKey()))
                    presentNodes.add(entryData.getKey());
            }
        }

        final var errorMessage = DataStructureFactory.preValidate(getDataStructClass(), presentNodes, container);
        if (errorMessage != null) {
            // DiSky.debug("--- Error while validating data structure: " + errorMessage);
            // Skript.error(errorMessage);
            // why skript? why don't you want my error message? ;-;
            DiSkyRuntimeHandler.error(new IllegalStateException(errorMessage), node);
            return false;
        }

        return container != null;
    }

    @Override
    protected @Nullable TriggerItem walk(Event event) {
        return null;
    }

    public abstract Class<D> getDataStructClass();

}
