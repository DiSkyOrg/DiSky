package net.itsthesky.disky.api.skript.entries;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.config.Node;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.config.SimpleNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryData;
import org.skriptlang.skript.lang.entry.EntryValidator;
import org.skriptlang.skript.lang.entry.KeyValueEntryData;

import java.util.function.Function;

public class MutexEntryData<T> extends EntryData<MutexEntryData.MutexEntry<T>> {

    private EntryValidator entryValidator;
    private KeyValueEntryData<T> valueEntryData;

    public MutexEntryData(String key, @Nullable T defaultValue, boolean optional,
                          EntryValidator entryValidator, Function<String, T> parser) {
        super(key, MutexEntryData.MutexEntry.ofValue(defaultValue), optional);
        this.entryValidator = entryValidator;
        this.valueEntryData = new KeyValueEntryData<T>(key,
                defaultValue, false) {

            @Override
            protected T getValue(@NotNull String value) {
                return parser.apply(value);
            }

            @Override
            public @NotNull String getSeparator() {
                return ":";
            }

        };
    }

    @Override
    public MutexEntryData.MutexEntry<T> getValue(@NotNull Node node) {
        if (node instanceof SimpleNode) {
            final T value = valueEntryData.getValue(node);
            return MutexEntryData.MutexEntry.ofValue(value);
        } else if (node instanceof SectionNode) {
            final @Nullable EntryContainer entry = entryValidator.validate((SectionNode) node);
            if (entry == null)
                return MutexEntryData.MutexEntry.empty();

            return MutexEntryData.MutexEntry.ofEntry(entry);
        }

        return MutexEntryData.MutexEntry.empty();
    }

    @Override
    public boolean canCreateWith(@NotNull Node node) {
        if (node instanceof SectionNode) {
            String key = node.getKey();
            if (key == null)
                return false;
            key = ScriptLoader.replaceOptions(key);
            return getKey().equalsIgnoreCase(key);
        }

        return valueEntryData.canCreateWith(node);
    }

    public static class MutexEntry<T> {

        public static <T> MutexEntry<T> ofEntry(@NotNull EntryContainer entry) {
            return new MutexEntry<>(null, entry);
        }

        public static <T> MutexEntry<T> ofValue(@NotNull T value) {
            return new MutexEntry<>(value, null);
        }

        public static <T> MutexEntry<T> empty() {
            return new MutexEntry<>(null, null);
        }

        private final @Nullable T value;
        private final @Nullable EntryContainer entry;

        private MutexEntry(@Nullable T value,
                           @Nullable EntryContainer entry) {
            this.value = value;
            this.entry = entry;
        }

        public @Nullable T getValue() {
            return value;
        }

        public @Nullable EntryContainer getEntryContainer() {
            return entry;
        }

        public boolean isComplex() {
            return entry != null;
        }

        public boolean isValid() {
            return value != null || entry != null;
        }
    }

}
