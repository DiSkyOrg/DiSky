package info.itsthesky.disky.api.datastruct;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.config.Node;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.util.SimpleLiteral;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.api.ReflectionUtils;
import info.itsthesky.disky.api.datastruct.base.DataStruct;
import info.itsthesky.disky.api.skript.BetterExpressionEntryData;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryData;
import org.skriptlang.skript.lang.entry.EntryValidator;
import org.skriptlang.skript.lang.entry.util.ExpressionEntryData;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

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
    public record DataStructureEntryInfo(DataStructureEntry entry,
                                         String fieldName,
                                         Class<?> returnType,
                                         boolean array,
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
            entries.add(new DataStructureEntryInfo(entry, field.getName(),
                    field.getType(), field.getType().isArray() || List.class.isAssignableFrom(field.getType()), defaultValue));
        }

        var entryValidatorBuilder = EntryValidator.builder();
        for (var entry : entries) {

            // FIRST CASE: it's not an array, and requires a data structure
            if (!entry.array() && DataStruct.class.isAssignableFrom(entry.returnType())) { {

                entryValidatorBuilder.addEntryData(new ExpressionEntryData<Object>(entry.entry().value(),
                        null, entry.entry().optional(), (Class) entry.returnType));

            }}

            // SECOND CASE: it's an array, and requires a data structure
            else if (entry.array() && DataStruct.class.isAssignableFrom(entry.returnType())) {

                entryValidatorBuilder.addEntryData(new BetterExpressionEntryData<Object>(entry.entry().value(),
                        null, entry.entry().optional(), (Class) entry.returnType));

            }

        }

        var structInfo = new DataStructureInfo(clazz, annotation.clazz(), annotation.value(), entries, entryValidatorBuilder.build());
        this.registeredStructures.add(structInfo);

        DiSky.debug("Registered data structure " + annotation.value() + " with " + entries.size() + " entries.");
    }

    @Nullable
    public static BetterEntryContainer validate(EntryValidator entryValidator, SectionNode sectionNode) {
        List<EntryData<?>> entries = new ArrayList<>(entryValidator.getEntryData());
        Map<String, List<Node>> handledNodes = new HashMap<>();
        List<Node> unhandledNodes = new ArrayList<>();

        var unexpectedNodeTester = (Predicate<Node>) ReflectionUtils.getFieldValueViaInstance(entryValidator, "unexpectedNodeTester");
        var unexpectedEntryMessage = (Function<String, String>) ReflectionUtils.getFieldValueViaInstance(entryValidator, "unexpectedEntryMessage");
        var missingRequiredEntryMessage = (Function<String, String>) ReflectionUtils.getFieldValueViaInstance(entryValidator, "missingRequiredEntryMessage");

        boolean ok = true;
        nodeLoop: for (Node node : sectionNode) {
            if (node.getKey() == null)
                continue;

            // Le premier pas est de déterminer si le node est présent dans la liste entryData
            boolean foundMatch = false;
            for (EntryData<?> data : entries) {
                if (data.canCreateWith(node)) {
                    // C'est un node connu, on l'ajoute à la liste correspondante
                    handledNodes.computeIfAbsent(data.getKey(), k -> new ArrayList<>()).add(node);
                    foundMatch = true;
                    // Ne pas retirer l'EntryData car on peut avoir plusieurs nodes correspondants
                    continue nodeLoop;
                }
            }

            // Aucun EntryData correspondant trouvé
            if (!foundMatch) {
                if (unexpectedNodeTester == null || unexpectedNodeTester.test(node)) {
                    ok = false;
                    Skript.error(unexpectedEntryMessage.apply(ScriptLoader.replaceOptions(node.getKey())));
                } else {
                    unhandledNodes.add(node);
                }
            }
        }

        // Vérification des entrées requises
        for (EntryData<?> entryData : entries) {
            List<Node> matchingNodes = handledNodes.getOrDefault(entryData.getKey(), Collections.emptyList());
            if (!entryData.isOptional() && matchingNodes.isEmpty()) {
                Skript.error(missingRequiredEntryMessage.apply(entryData.getKey()));
                ok = false;
            }
        }

        if (!ok)
            return null;

        return new BetterEntryContainer(sectionNode, entryValidator, handledNodes, unhandledNodes);
    }

}
