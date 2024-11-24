package info.itsthesky.disky.api.datastruct;

import ch.njol.skript.lang.util.SimpleLiteral;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.api.skript.BetterExpressionEntryData;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryValidator;
import org.skriptlang.skript.lang.entry.util.ExpressionEntryData;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public final class DataStructureFactory {

    private static final DataStructureFactory INSTANCE = new DataStructureFactory();
    public static DataStructureFactory getInstance() {
        if (INSTANCE == null)
            return new DataStructureFactory();

        return INSTANCE;
    }

    public DataStructureInfo getStructure(String name) {
        return registeredStructures.stream()
                .filter(info -> info.name().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public record DataStructureInfo(Class<?> structureClazz, Class<?> returnedClazz,
                                    String name,
                                    List<DataStructureEntryInfo> entries,
                                    EntryValidator validator) {};
    public record DataStructureEntryInfo(String name, String fieldName,
                                          Class<?> returnType,
                                          boolean optional,
                                          @Nullable Object defaultValue) {};

    private List<DataStructureInfo> registeredStructures;

    public DataStructureFactory() {
        this.registeredStructures = new ArrayList<>();
    }

    public void registerDataStructure(Class<?> clazz) {
        if (!clazz.isAnnotationPresent(DataStructure.class))
            throw new IllegalArgumentException("The class " + clazz.getName() + " is not a valid DataStructure class!");

        DataStructure annotation = clazz.getAnnotation(DataStructure.class);

        List<DataStructureEntryInfo> entries = new ArrayList<>();
        for (var field : clazz.getDeclaredFields()) {
            if (!field.isAnnotationPresent(DataStructureEntry.class))
                continue;

            @Nullable Object defaultValue = null;
            try {
                defaultValue = field.get(clazz.getConstructor().newInstance());
            } catch (IllegalAccessException | NoSuchMethodException | InstantiationException | InvocationTargetException e) {
                DiSky.debug("Cannot get the default value of the field " + field.getName() + " in the class " + clazz.getName());
                DiSky.debug("The field must be public and have a default constructor!");
            }

            DataStructureEntry entry = field.getAnnotation(DataStructureEntry.class);
            entries.add(new DataStructureEntryInfo(entry.value(), field.getName(), field.getType(),
                    entry.optional(), defaultValue));
        }

        var entryValidatorBuilder = EntryValidator.builder();
        for (var entry : entries) {
            // TODO: Handle lists/arrays
            entryValidatorBuilder.addEntryData(new ExpressionEntryData<Object>(entry.name(),
                    null, entry.optional(), (Class) entry.returnType));
        }

        var structInfo = new DataStructureInfo(clazz, annotation.clazz(), annotation.value(), entries, entryValidatorBuilder.build());
        this.registeredStructures.add(structInfo);

        DiSky.debug("Registered data structure " + annotation.value() + " with " + entries.size() + " entries.");
    }

}
