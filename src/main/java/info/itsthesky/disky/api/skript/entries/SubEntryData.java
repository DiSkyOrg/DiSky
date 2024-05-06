package info.itsthesky.disky.api.skript.entries;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.config.Node;
import ch.njol.skript.config.SectionNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryData;
import org.skriptlang.skript.lang.entry.EntryValidator;

public class SubEntryData extends EntryData<EntryContainer> {

    private final EntryValidator validator;

    public SubEntryData(String key, boolean optional, EntryValidator validator) {
        super(key, null, optional);
        this.validator = validator;
    }

    @Override
    public @Nullable EntryContainer getValue(@NotNull Node node) {
        assert node instanceof SectionNode;
        return validator.validate((SectionNode) node);
    }

    @Override
    public boolean canCreateWith(@NotNull Node node) {
        if (!(node instanceof SectionNode))
            return false;
        String key = node.getKey();
        if (key == null)
            return false;
        key = ScriptLoader.replaceOptions(key);
        return getKey().equalsIgnoreCase(key);
    }

}
