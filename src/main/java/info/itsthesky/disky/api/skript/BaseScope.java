package info.itsthesky.disky.api.skript;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.config.Node;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.*;
import ch.njol.skript.lang.util.SimpleLiteral;
import ch.njol.skript.log.SkriptLogger;
import ch.njol.skript.util.StringMode;
import info.itsthesky.disky.core.SkriptUtils;
import info.itsthesky.disky.structures.scope.BotScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Represent a scope that work as an event, with possible entries and sections when parsed.
 * @param <T> The object that the scope will parse into.
 * @author ItsTheSky
 */
public abstract class BaseScope<T> extends SelfRegisteringSkriptEvent {

    protected static final String listPattern = "\\s*,\\s*|\\s+(and|or|, )\\s+";

    public abstract @Nullable T parse(@NotNull SectionNode node);

    public abstract @Nullable String validate(@Nullable T parsedEntity);

    public void init(Literal<?> @NotNull [] args, int matchedPattern, @NotNull SkriptParser.ParseResult parseResult, SectionNode node) {};

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
        return expr.getSingle(new BotScope.BotScopeEvent());
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

    /**
     * Nuke a specific {@link SectionNode} in order to delete every triggers present in it.
     * <br>aka disable the execution of every next trigger of this section node.
     * @param sectionNode The section node to nuke
     */
    public static void nukeSectionNode(SectionNode sectionNode) {
        List<Node> nodes = new ArrayList<>();
        for (Node node : sectionNode) nodes.add(node);
        for (Node n : nodes) sectionNode.remove(n);
    }

    @Override
    public boolean init(Literal<?> @NotNull [] args, int matchedPattern, @NotNull SkriptParser.ParseResult parseResult) {
        final Node node = SkriptLogger.getNode();
        if (!(node instanceof SectionNode))
            return false;
        init(args, matchedPattern, parseResult, (SectionNode) node);
        final @Nullable T entity = parse((SectionNode) node);
        SkriptLogger.setNode(node);
        SkriptUtils.nukeSectionNode((SectionNode) node);

        final String error = validate(entity);
        if (error == null)
            return true;

        Skript.error(error);
        return false;
    }

    @Override
    public void register(@NotNull Trigger t) {}
    @Override
    public void unregister(@NotNull Trigger t) {}
    @Override
    public void unregisterAll() {}
}
