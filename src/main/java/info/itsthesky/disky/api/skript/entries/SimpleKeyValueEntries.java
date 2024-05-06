package info.itsthesky.disky.api.skript.entries;

import ch.njol.skript.Skript;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.KeyValueEntryData;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;

public final class SimpleKeyValueEntries {
    private static final Pattern LIST = Pattern.compile("\\s*,\\s*/?");

    public static KeyValueEntryData<Boolean> createBooleanEntry(String key, boolean def, boolean optional) {
        return new KeyValueEntryData<Boolean>(key, def, optional) {
            @Override
            protected @Nullable Boolean getValue(@NotNull String value) {
                try {
                    return Boolean.parseBoolean(value);
                } catch (Exception e) {
                    Skript.error("Cannot parse the value as a boolean! (got " + value + ")");
                    return null;
                }
            }
        };
    }

    public static <T> KeyValueEntryData<List<T>> createList(String key, List<T> def, boolean optional,
                                                            Function<String, T> singleParser) {
        return new KeyValueEntryData<List<T>>(key, def, optional) {
            @Override
            protected @Nullable List<T> getValue(@NotNull String value) {
                final String[] values = LIST.split(value);
                final List<T> result = new ArrayList<>();
                for (String val : values) {
                    try {
                        final T parsed = singleParser.apply(val);
                        if (parsed == null) {
                            return null;
                        }
                        result.add(parsed);
                    } catch (Exception e) {
                        Skript.error("Cannot parse the value as a list! (got " + value + ")");
                        return null;
                    }
                }

                return result;
            }
        };
    }

}
