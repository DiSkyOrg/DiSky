package info.itsthesky.disky.api.skript;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.config.Node;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.*;
import ch.njol.skript.lang.util.SimpleLiteral;
import ch.njol.skript.log.SkriptLogger;
import ch.njol.skript.util.StringMode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represent a scope that work as an event, with possible entries and sections when parsed.
 * @param <T> The object that the scope will parse into.
 * @author ItsTheSky
 */
public abstract class BaseScope<T> extends SelfRegisteringSkriptEvent {

    protected static final String listPattern = "\\s*,\\s*|\\s+(and|or|, )\\s+";

    public abstract @Nullable T parse(@NotNull SectionNode node);

    public abstract boolean validate(@Nullable T parsedEntity);

    public void init(Literal<?>[] args) {};

    public String parseEntry(SectionNode node, String key) {
        return parseEntry(node, key, "");
    }

    /**
     * Parse a string from a node & its key to the more accurate result possible:
     * <ul>
     *     <li>Parse options inside the string</li>
     *     <li>Convert it to an expression and get its single value, allowing global variable and constants expressions.</li>
     * </ul>
     * @param node The {@link SectionNode} to get the key from
     * @param key  The key that represent the entry
     * @param defaultValue The default String value to return if the key doesn't exist.
     * @return The parse value according to the two point above.
     */
    public String parseEntry(SectionNode node, String key, String defaultValue) {
        String text = ScriptLoader.replaceOptions(node.get(key, defaultValue));
        if (text.startsWith("\"") && text.endsWith("\"")) {
            text = text.substring(1, text.length() - 1);
        }
        Expression<String> expr = VariableString.newInstance(text, StringMode.MESSAGE);
        try {
            if (((VariableString) expr).isSimple()) {
                expr = new SimpleLiteral<>(text, false);
            }
        } catch (NullPointerException ignored) { }
        return expr.getSingle(null);
    }

    /**
     * Make an error through Skript and return null.
     * <br> Only made for easier and faster development, since we ust have to use <code>return error("Hi");</code> instead of two statement.
     * @param message The message to send as error
     * @return Always null
     */
    public <E> @Nullable E error(String message) {
        Skript.error(message);
        return null;
    }

    @Override
    public boolean init(Literal<?> @NotNull [] args, int matchedPattern, SkriptParser.@NotNull ParseResult parseResult) {
        final Node node = SkriptLogger.getNode();
        if (!(node instanceof SectionNode))
            return false;
        init(args);
        final @Nullable T entity = parse((SectionNode) node);
        SkriptLogger.setNode(node);
        return validate(entity);
    }

    @Override
    public void register(@NotNull Trigger t) {}
    @Override
    public void unregister(@NotNull Trigger t) {}
    @Override
    public void unregisterAll() {}
}
