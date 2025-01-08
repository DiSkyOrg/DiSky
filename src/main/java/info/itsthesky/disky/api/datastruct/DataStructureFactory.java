package info.itsthesky.disky.api.datastruct;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.registrations.Classes;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.api.datastruct.base.BasicDS;
import info.itsthesky.disky.api.datastruct.base.ChainDS;
import info.itsthesky.disky.api.datastruct.base.DataStruct;
import info.itsthesky.disky.api.skript.BetterExpressionEntryData;
import info.itsthesky.disky.core.SkriptUtils;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class DataStructureFactory {

    public static EntryValidator createValidator(@NotNull Class<?> structClass) {

        final var validator = EntryValidator.builder();
        final var allNodes = new HashMap<String, String>();

        for (final var field : structClass.getDeclaredFields()) {
            final var entry = field.getAnnotation(DataStructureEntry.class);
            if (entry == null)
                continue;

            final var key = entry.value();
            final Class type = field.getType();

            Object defaultValue = null;
            try {
                var instance = structClass.getDeclaredConstructor().newInstance();
                field.setAccessible(true);
                defaultValue = field.get(instance);
            } catch (IllegalAccessException | NoSuchMethodException | InstantiationException | InvocationTargetException e) {
                DiSky.debug("Cannot get default value of field " + field.getName() + " in class " + structClass.getName() + "! Are you sure a public and empty constructor is present?");
            }

            // get the natural name for the type
            final var typeName = Classes.getExactClassName(type);
            allNodes.put(key.split(":")[0], typeName);

            validator.addEntryData(new BetterExpressionEntryData<Object>(
                    key,
                    SkriptUtils.convertToExpressions(defaultValue),
                    entry.optional(),
                    type
            ));
        }

        // final changes
        return validator.unexpectedNodeTester(node -> false).build();
    }

    public static <F, T extends DataStruct<F>> F createDataStructure(@NotNull Class<T> structClass,
                                                                     @NotNull EntryContainer container,
                                                                     @NotNull Event event,
                                                                     @Nullable F chainInstance)
            throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        final var instance = structClass.getConstructor().newInstance();

        for (final var field : structClass.getDeclaredFields()) {
            final var entry = field.getAnnotation(DataStructureEntry.class);
            if (entry == null)
                continue;

            final var key = entry.value();
            final var type = field.getType();
            final var isList = List.class.isAssignableFrom(type) || type.isArray();

            field.setAccessible(true);

            var list = container.getOptional(key, List.class, true);
            if (list == null)
                list = new ArrayList<>();

            // be sure it's a list, if it's not, check if the list contain only one expr
            if (!isList && list.size() == 1) {
                final var expr = (Expression<?>) list.get(0);
                if (expr != null)
                    field.set(instance, expr.getSingle(event));
            } else if (isList) {
                final var parsedList = new ArrayList<>();
                for (final var expr : list)
                    if (expr != null)
                        parsedList.add(((Expression<?>) expr).getSingle(event));

                field.set(instance, parsedList);
            }
        }

        if (instance instanceof BasicDS) {
            return ((BasicDS<F>) instance).build();
        } else if (instance instanceof ChainDS) {
            if (chainInstance == null)
                throw new IllegalArgumentException("Cannot edit a chain data structure without a chain instance!");

            return ((ChainDS<F>) instance).edit(chainInstance);
        }

        throw new IllegalArgumentException("The data structure class " + structClass.getName() + " must implement either BasicDS or ChainDS interface!");
    }

    public static <F, T extends DataStruct<F>> @Nullable String preValidate(@NotNull Class<T> structClass, @NotNull List<String> presentNodes) {
        final T instance;
        try {
            instance = structClass.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        return instance.preValidate(presentNodes);
    }
}
