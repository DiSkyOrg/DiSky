package net.itsthesky.disky.api.datastruct;

import ch.njol.skript.Skript;
import net.itsthesky.disky.api.DiSkyRegistry;
import ch.njol.skript.config.Node;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.registrations.Classes;
import net.itsthesky.disky.DiSky;
import net.itsthesky.disky.api.ReflectionUtils;
import net.itsthesky.disky.api.datastruct.base.BasicDS;
import net.itsthesky.disky.api.datastruct.base.ChainDS;
import net.itsthesky.disky.api.datastruct.base.DataStruct;
import net.itsthesky.disky.api.skript.BetterExpressionEntryData;
import net.itsthesky.disky.core.SkriptUtils;
import net.itsthesky.disky.elements.sections.handler.DiSkyRuntimeHandler;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Factory class responsible for creating and managing data structures in the DiSky system.
 * This factory handles the parsing, validation, and instantiation of data structures
 * defined through annotations and configuration nodes.
 *
 * The workflow consists of three main phases:
 * 1. Initialization - Parse and validate the structure definition ({@link #initDataStructure})
 * 2. Validation - Create and configure the entry validator ({@link #createValidator})
 * 3. Creation - Build the actual data structure instance ({@link #createDataStructure})
 *
 * @see DataStructure
 * @see DataStructureEntry
 * @see BasicDS
 * @see ChainDS
 */
public final class DataStructureFactory {

    /**
     * Represents the parsed result of a data structure configuration,
     * containing all necessary information to create instances.
     */
    public static record DataStructureParseResult(
            @NotNull SectionNode relatedNode,
            @NotNull EntryValidator validator,
            @NotNull EntryContainer container,
            @NotNull Map<String, List<Expression<?>>> expressions,
            @NotNull Map<String, List<DataStructureParseResult>> subStructures
    ) {}

    /**
     * Initializes a data structure by parsing and validating its configuration.
     *
     * @param dataStructClass The class of the data structure to initialize
     * @param node The section node containing the structure's configuration
     * @return A parse result containing all necessary information, or null if validation fails
     */
    public static DataStructureParseResult initDataStructure(
            @NotNull Class<? extends DataStruct> dataStructClass,
            @NotNull SectionNode node
    ) {
        final var validator = createValidator(dataStructClass);
        final var container = validator.validate(node);
        if (container == null) return null;

        final var keysToFields = mapKeysToFields(dataStructClass);
        DiSky.debug("=========== Starting parsing of data structure " + dataStructClass.getName() + " ===========");
        logContainerInfo(container, validator);

        final var presentNodes = collectPresentNodes(validator, container);
        final var expressions = parseExpressions(container, presentNodes);
        final var subStructures = parseSubStructures(dataStructClass, container, keysToFields);

        final var errorMessage = preValidate(dataStructClass, presentNodes);
        if (errorMessage != null) {
            DiSkyRuntimeHandler.error(new IllegalStateException(errorMessage), node);
            return null;
        }

        return new DataStructureParseResult(node, validator, container, expressions, subStructures);
    }

    /**
     * Creates a validator for the given data structure class.
     *
     * @param structClass The class to create a validator for
     * @return A configured entry validator
     */
    public static EntryValidator createValidator(@NotNull Class<?> structClass) {
        final var validator = EntryValidator.builder();
        DiSky.debug("=========== Starting validation of data structure " + structClass.getName() + " ===========");

        final var plannedKeys = new HashSet<String>();
        for (final var field : structClass.getDeclaredFields()) {
            final var entry = field.getAnnotation(DataStructureEntry.class);
            if (entry == null)
                continue;

            processFieldValidator(validator, field, entry, structClass);
            plannedKeys.add(entry.value());
        }

        validator.unexpectedNodeTester(node -> {
            final var key = node.getKey().split(":")[0];
            if (plannedKeys.contains(key))
                return false; // all good, it's a sub-structure or entry

            Skript.error("Unexpected node with key '" + key + "' in data structure " + structClass.getSimpleName());
            return false;
        });

        DiSky.debug("=========== End of validation of data structure " + structClass.getName() + " ===========");
        return validator.build();
    }

    /**
     * Creates an instance of the data structure with the parsed configuration.
     *
     * @param structClass The class of the data structure to create
     * @param parseResult The parsed configuration data
     * @param event The event context
     * @param chainInstance The chain instance for chain data structures
     * @return The created data structure instance
     * @throws ReflectiveOperationException if instance creation fails
     */
    public static Object createDataStructure(
            @NotNull Class<?> structClass,
            @NotNull DataStructureParseResult parseResult,
            @NotNull Event event,
            @Nullable Object chainInstance
    ) throws ReflectiveOperationException {
        DiSky.debug("################## Starting creation of data structure " + structClass.getName() + " ##################");
        final var instance = structClass.getConstructor().newInstance();
        final var container = parseResult.container();

        final var unhandledNodes = groupUnhandledNodes(container);
        processFields(structClass, instance, parseResult, event, unhandledNodes);
        if (!validateFields(parseResult.relatedNode(), structClass, instance))
            return null;

        DiSky.debug("################## End of creation of data structure " + structClass.getName() + " ##################");
        return finalizeInstance(instance, chainInstance);
    }

    /**
     * Validates the fields of a data structure instance.
     * This will check the instance's field value for optional and type constraints.
     * @param structClass The class of the data structure
     * @param instance The instance to validate
     */
    private static boolean validateFields(@NotNull Node node, @NotNull Class<?> structClass, @NotNull Object instance) {
        for (final var field : structClass.getDeclaredFields()) {
            final var entry = field.getAnnotation(DataStructureEntry.class);
            if (entry == null) continue;

            final var key = entry.value();
            final var value = ReflectionUtils.getFieldValue(field, instance);

            if (value == null && !entry.optional()) {
                DiSkyRuntimeHandler.error(new IllegalStateException(
                        "Field '" + key + "' in data structure " + structClass.getSimpleName() + " is null/none/empty, but is required!"
                ), node, false);
                return false;
            }
        }

        return true;
    }

    /**
     * Maps field keys to their corresponding field names.
     */
    private static Map<String, String> mapKeysToFields(@NotNull Class<?> dataStructClass) {
        final var keysToFields = new HashMap<String, String>();
        for (final var field : dataStructClass.getDeclaredFields()) {
            final var entry = field.getAnnotation(DataStructureEntry.class);
            if (entry == null) continue;
            keysToFields.put(entry.value(), field.getName());
        }
        return keysToFields;
    }

    /**
     * Processes a field for validation configuration.
     */
    private static void processFieldValidator(
            EntryValidator.EntryValidatorBuilder validator,
            Field field,
            DataStructureEntry entry,
            Class<?> structClass
    ) {
        final var key = entry.value();
        final Class type = field.getType();

        Object defaultValue = getDefaultValue(field, structClass);

        if (!isList(type) || entry.subStructureType() == DataStruct.class) {
            final var typeName = Classes.getExactClassName(type);
            validator.addEntryData(new BetterExpressionEntryData<Object>(
                    key,
                    SkriptUtils.convertToExpressions(defaultValue),
                    entry.optional(),
                    type
            ));
            DiSky.debug("- Added entry data for key " + key + " with type " + typeName);
        } else {
            DiSky.debug("- Added sub-structure for key " + key);
        }
    }

    /**
     * Gets the default value for a field.
     */
    private static Object getDefaultValue(Field field, Class<?> structClass) {
        try {
            var instance = structClass.getDeclaredConstructor().newInstance();
            field.setAccessible(true);
            return field.get(instance);
        } catch (ReflectiveOperationException e) {
            DiSky.debug("Cannot get default value of field " + field.getName() +
                    " in class " + structClass.getName() +
                    "! Are you sure a public and empty constructor is present?");
            return null;
        }
    }

    /**
     * Logs debug information about the container and validator.
     */
    private static void logContainerInfo(EntryContainer container, EntryValidator validator) {
        DiSky.debug("- Unhandled nodes (" + container.getUnhandledNodes().size() + "): " +
                container.getUnhandledNodes().stream().map(Node::getKey).toList());
        DiSky.debug("- Found entry data (" + validator.getEntryData().size() + "): " +
                validator.getEntryData().stream().map(data -> data.getKey()).toList());
    }

    /**
     * Collects all present nodes from the validator and container.
     */
    private static List<String> collectPresentNodes(EntryValidator validator, EntryContainer container) {
        final var presentNodes = new ArrayList<String>();
        for (final var entryData : validator.getEntryData()) {
            if (container.hasEntry(entryData.getKey()))
                presentNodes.add(entryData.getKey().split(":")[0]);
        }
        for (final var node : container.getUnhandledNodes())
            presentNodes.add(node.getKey().split(":")[0]);

        DiSky.debug("Present nodes: " + presentNodes);
        return presentNodes;
    }

    /**
     * Parses expressions from the container for given nodes.
     */
    private static Map<String, List<Expression<?>>> parseExpressions(
            EntryContainer container,
            List<String> presentNodes
    ) {
        final var expressions = new HashMap<String, List<Expression<?>>>();
        for (final var nodeKey : presentNodes) {
            var exprs = container.getOptional(nodeKey, List.class, true);
            if (exprs == null) exprs = new ArrayList<>();
            DiSky.debug("-> Adding expression for node " + nodeKey + " with " + exprs.size() + " expressions");
            expressions.put(nodeKey, exprs);
        }
        return expressions;
    }

    /**
     * Parses sub-structures from unhandled nodes.
     */
    private static Map<String, List<DataStructureParseResult>> parseSubStructures(
            Class<?> dataStructClass,
            EntryContainer container,
            Map<String, String> keysToFields
    ) {
        final var subStructures = new HashMap<String, List<DataStructureParseResult>>();
        DiSky.debug("-> Starting sub-structures handling ...");

        for (final var subNode : container.getUnhandledNodes()) {
            if (!(subNode instanceof SectionNode subSectionNode)) {
                DiSky.debug("-> Unhandled node is not a section node (" + subNode.getKey() + "), skipping ...");
                continue;
            }

            processSubStructure(dataStructClass, subNode, keysToFields, subStructures, subSectionNode);
        }

        return subStructures;
    }

    /**
     * Processes a single sub-structure node.
     */
    private static void processSubStructure(
            Class<?> dataStructClass,
            Node subNode,
            Map<String, String> keysToFields,
            Map<String, List<DataStructureParseResult>> subStructures,
            SectionNode subSectionNode
    ) {
        final var nodeKey = subNode.getKey().split(":")[0];
        DiSky.debug("Now processing sub-structure for key " + nodeKey + " ...");

        final var entryField = ReflectionUtils.getField(dataStructClass, keysToFields.get(nodeKey));
        if (entryField == null) {
            DiSky.debug("-> Cannot find field for key " + nodeKey + " in class " + dataStructClass.getName());
            return;
        }

        final var entryType = entryField.getDeclaredAnnotation(DataStructureEntry.class).subStructureType();
        final var subResult = initDataStructure(entryType, subSectionNode);

        if (subResult != null)
            subStructures.computeIfAbsent(nodeKey, k -> new ArrayList<>()).add(subResult);
        else
            DiSky.debug("Cannot init sub-structure for key " + nodeKey + " in class " + dataStructClass.getName());

        DiSky.debug("--> Adding sub-structure for key " + nodeKey + " with " + subResult);
    }

    /**
     * Groups unhandled nodes by their base key.
     */
    private static Map<String, List<Node>> groupUnhandledNodes(EntryContainer container) {
        final var unhandledNodes = new HashMap<String, List<Node>>();
        for (final var node : container.getUnhandledNodes())
            unhandledNodes.computeIfAbsent(node.getKey().split(":")[0], k -> new ArrayList<>()).add(node);
        return unhandledNodes;
    }

    /**
     * Processes all fields of the data structure instance.
     */
    private static void processFields(
            Class<?> structClass,
            Object instance,
            DataStructureParseResult parseResult,
            Event event,
            Map<String, List<Node>> unhandledNodes
    ) throws IllegalAccessException {
        for (final var field : structClass.getDeclaredFields()) {
            final var entry = field.getAnnotation(DataStructureEntry.class);
            if (entry == null) continue;

            processField(field, entry, instance, parseResult, event, unhandledNodes);
        }
    }

    /**
     * Processes a single field during instance creation.
     */
    private static void processField(
            Field field,
            DataStructureEntry entry,
            Object instance,
            DataStructureParseResult parseResult,
            Event event,
            Map<String, List<Node>> unhandledNodes
    ) throws IllegalAccessException {
        final var key = entry.value();
        final var type = field.getType();
        final var isList = isList(type);
        final var isDataStructs = entry.subStructureType() != DataStruct.class;

        field.setAccessible(true);

        if (isDataStructs) {
            processDataStructField(field, entry, instance, key, unhandledNodes, parseResult, event);
        } else {
            processSimpleField(field, instance, key, isList, parseResult, event);
        }
    }

    /**
     * Processes a field that contains a data structure.
     */
    private static void processDataStructField(
            Field field,
            DataStructureEntry entry,
            Object instance,
            String key,
            Map<String, List<Node>> unhandledNodes,
            DataStructureParseResult parseResult,
            Event event
    ) throws IllegalAccessException {
        final var subNodes = unhandledNodes.get(key);
        if (subNodes == null) return;

        final var list = new ArrayList<>();
        for (int i = 0; i < subNodes.size(); i++) {
            final var subNode = subNodes.get(i);
            final var targetDataStructureType = entry.subStructureType();
            final var nodeKey = subNode.getKey().split(":")[0];
            final var subResultList = parseResult.subStructures().get(nodeKey);

            if (subResultList == null || subResultList.isEmpty()) {
                DiSky.debug("Cannot find sub-structure for key " + nodeKey + " in class " + instance.getClass().getName());
                continue;
            }

            DiSky.debug("Creating sub-structure for key " + key + " with " + subResultList.size() + " sub-structures ...");
            final var subResultItem = subResultList.get(i);
            try {
                final var subInstance = createDataStructure(targetDataStructureType, subResultItem, event, null);
                if (subInstance != null)
                    list.add(subInstance);
            } catch (ReflectiveOperationException e) {
                DiSky.debug("Failed to create sub-structure: " + e.getMessage());
            }
        }
        field.set(instance, list);
    }

    /**
     * Processes a field that contains simple values (non-data structures).
     */
    private static void processSimpleField(
            Field field,
            Object instance,
            String key,
            boolean isList,
            DataStructureParseResult parseResult,
            Event event
    ) throws IllegalAccessException {
        final var expressions = parseResult.expressions().getOrDefault(key, new ArrayList<>());

        if (!isList && expressions.size() == 1) {
            final var expr = (Expression<?>) expressions.get(0);
            if (expr != null) {
                field.set(instance, expr.getSingle(event));
            }
        } else if (isList) {
            final var parsedList = new ArrayList<>();
            for (final var expr : expressions) {
                if (expr != null) {
                    final var value = expr.getSingle(event);
                    if (value != null) {
                        parsedList.add(value);
                    }
                }
            }
            field.set(instance, parsedList);
        }
    }

    /**
     * Determines if a type represents a list or array.
     */
    private static boolean isList(Class<?> type) {
        return List.class.isAssignableFrom(type) || type.isArray();
    }

    /**
     * Finalizes the instance creation by building or editing based on its type.
     */
    private static Object finalizeInstance(Object instance, @Nullable Object chainInstance) {
        if (instance instanceof BasicDS) {
            return ((BasicDS) instance).build();
        } else if (instance instanceof ChainDS) {
            if (chainInstance == null) {
                throw new IllegalArgumentException("Cannot edit a chain data structure without a chain instance!");
            }
            return ((ChainDS) instance).edit(chainInstance);
        }
        throw new IllegalArgumentException(
                "The data structure class " + instance.getClass().getName() +
                        " must implement either BasicDS or ChainDS interface!");
    }

    /**
     * Pre-validates a data structure class with the given nodes.
     *
     * @param structClass The class to validate
     * @param presentNodes The list of present node keys
     * @return An error message if validation fails, null otherwise
     */
    public static <F, T extends DataStruct<F>> @Nullable String preValidate(
            @NotNull Class<T> structClass,
            @NotNull List<String> presentNodes
    ) {
        try {
            final T instance = structClass.getConstructor().newInstance();
            return instance.preValidate(presentNodes);
        } catch (ReflectiveOperationException e) {
            return "Failed to create instance for validation: " + e.getMessage();
        }
    }
}