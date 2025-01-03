package info.itsthesky.disky.api.datastruct;

import ch.njol.skript.config.Node;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.parser.ParserInstance;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryData;
import org.skriptlang.skript.lang.entry.EntryValidator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * A "better" version of the {@link EntryContainer} which allows multiple node per entry key (basically, allows entry with the same key).
 * This is useful for data structure where you can have multiple value for the same key.
 * @author Sky & SkriptLang Team
 * @since 1.0
 * @see EntryContainer
 * @see DataStructureFactory#validate(EntryValidator, SectionNode)
 */
public class BetterEntryContainer {

    private final SectionNode source;
    @Nullable
    private final EntryValidator entryValidator;
    @Nullable
    private final Map<String, List<Node>> handledNodes;
    private final List<Node> unhandledNodes;

    public BetterEntryContainer(
            SectionNode source,
            @Nullable EntryValidator entryValidator,
            @Nullable Map<String, List<Node>> handledNodes,
            List<Node> unhandledNodes
    ) {
        this.source = source;
        this.entryValidator = entryValidator;
        this.handledNodes = handledNodes;
        this.unhandledNodes = unhandledNodes;
    }

    public SectionNode getSource() {
        return source;
    }

    public List<Node> getUnhandledNodes() {
        return unhandledNodes;
    }

    public List<Node> getNodesForEntry(String key) {
        if (handledNodes == null)
            return Collections.emptyList();
        return handledNodes.getOrDefault(key, Collections.emptyList());
    }

    public <E, R extends E> List<R> getAllValues(String key, Class<E> expectedType, boolean useDefaultValue) {
        List<R> values = new ArrayList<>();
        for (Node node : getNodesForEntry(key)) {
            R value = getValueFromNode(node, key, expectedType, useDefaultValue);
            if (value != null) {
                values.add(value);
            }
        }
        return values;
    }

    public <E, R extends E> R get(String key, Class<E> expectedType, boolean useDefaultValue) {
        R value = getOptional(key, expectedType, useDefaultValue);
        if (value == null)
            throw new RuntimeException("Null value for asserted non-null value");
        return value;
    }

    public Object get(String key, boolean useDefaultValue) {
        Object parsed = getOptional(key, useDefaultValue);
        if (parsed == null)
            throw new RuntimeException("Null value for asserted non-null value");
        return parsed;
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public <E, R extends E> R getOptional(String key, Class<E> expectedType, boolean useDefaultValue) {
        Object parsed = getOptional(key, useDefaultValue);
        if (parsed == null)
            return null;
        if (!expectedType.isInstance(parsed))
            throw new RuntimeException("Expected entry with key '" + key + "' to be '" + expectedType + "', but got '" + parsed.getClass() + "'");
        return (R) parsed;
    }

    @Nullable
    public Object getOptional(String key, int index, boolean useDefaultValue) {
        if (entryValidator == null || handledNodes == null)
            return null;

        EntryData<?> entryData = findEntryData(key);
        if (entryData == null)
            return null;

        List<Node> nodes = handledNodes.get(key);
        if (nodes == null || nodes.isEmpty())
            return entryData.getDefaultValue();

        if (index < 0 || index >= nodes.size())
            return null;

        Node node = nodes.get(index);
        return getValueFromNode(node, key, Object.class, useDefaultValue);
    }

    @Nullable
    private <E, R extends E> R getValueFromNode(Node node, String key, Class<E> expectedType, boolean useDefaultValue) {
        if (entryValidator == null)
            return null;

        EntryData<?> entryData = findEntryData(key);
        if (entryData == null)
            return null;

        ParserInstance parser = ParserInstance.get();
        Node oldNode = parser.getNode();
        parser.setNode(node);
        Object value = entryData.getValue(node);
        if (value == null && useDefaultValue)
            value = entryData.getDefaultValue();
        parser.setNode(oldNode);

        if (value == null || !expectedType.isInstance(value))
            return null;

        @SuppressWarnings("unchecked")
        R result = (R) value;
        return result;
    }

    @Nullable
    private EntryData<?> findEntryData(String key) {
        if (entryValidator == null)
            return null;

        for (EntryData<?> data : entryValidator.getEntryData()) {
            if (data.getKey().equals(key)) {
                return data;
            }
        }
        return null;
    }

    public boolean hasEntry(@NotNull String key) {
        return handledNodes != null && handledNodes.containsKey(key) && !handledNodes.get(key).isEmpty();
    }

    public int getEntryCount(@NotNull String key) {
        if (handledNodes == null)
            return 0;
        List<Node> nodes = handledNodes.get(key);
        return nodes == null ? 0 : nodes.size();
    }
}
