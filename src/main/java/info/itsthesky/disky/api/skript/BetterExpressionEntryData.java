package info.itsthesky.disky.api.skript;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.config.Node;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.config.SimpleNode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.localization.Message;
import ch.njol.skript.log.ErrorQuality;
import ch.njol.skript.log.ParseLogHandler;
import info.itsthesky.disky.DiSky;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryData;
import org.skriptlang.skript.lang.entry.EntryValidator;

import java.util.ArrayList;
import java.util.List;

/**
 * An {@link org.skriptlang.skript.lang.entry.util.ExpressionEntryData} that allows multiple expressions to be defined in a single entry.
 * It also let the developer write the different expressions in a section.
 * @param <T> the return type of the expressions
 * @author Sky
 */
public class BetterExpressionEntryData<T> extends EntryData<List<Expression<? extends T>>> {

    private static final Message M_IS = new Message("is");
    private final Class<T> returnType;
    private final int flags;

    public BetterExpressionEntryData(String key, @Nullable List<Expression<? extends T>> defaultValue, boolean optional,
                                     Class<T> returnType, int flags) {
        super(key, defaultValue, optional);

        this.returnType = returnType;
        this.flags = flags;
    }

    public BetterExpressionEntryData(String key, @Nullable List<Expression<? extends T>> defaultValue,
                                     boolean optional, Class<T> returnType) {
        this(key, defaultValue, optional, returnType, SkriptParser.ALL_FLAGS);
    }

    @Override
    @Nullable
    public List<Expression<? extends T>> getValue(@NotNull Node node) {
        if (node instanceof final SectionNode sectionNode) {
            final List<Expression<? extends T>> expressions = new ArrayList<>();
            for (Node subNode : sectionNode) {
                final String value = subNode.getKey();
                final Expression<? extends T> expression = parseExpression(value);
                if (expression != null)
                    expressions.add(expression);
            }
            return expressions;
        } else if (node instanceof final SimpleNode simpleNode) {
            final String key = simpleNode.getKey();
            if (key == null)
                return null;

            final String value = ScriptLoader.replaceOptions(key).substring(getKey().length() + EntryValidator.EntryValidatorBuilder.DEFAULT_ENTRY_SEPARATOR.length());
            final Expression<? extends T> expression = parseExpression(value);
            if (expression != null)
                return List.of(expression);

            return null;
        } else {
            return null;
        }
    }

    private Expression<? extends T> parseExpression(String value) {
        Expression<? extends T> expression;
        try (ParseLogHandler log = new ParseLogHandler().start()) {
            expression = new SkriptParser(value, flags, ParseContext.DEFAULT)
                    .parseExpression(returnType);
            if (expression == null) { // print an error if it couldn't parse
                log.printError(
                        "'" + value + "' " + M_IS + " " + SkriptParser.notOfType(returnType),
                        ErrorQuality.NOT_AN_EXPRESSION
                );
            }
        }
        return expression;
    }

    @Override
    public boolean canCreateWith(@NotNull Node node) {
        if (node instanceof SectionNode) {
            return true;
        } else if (node instanceof SimpleNode) {
            String key = node.getKey();
            if (key == null)
                return false;
            key = ScriptLoader.replaceOptions(key);
            return key.startsWith(getKey() + EntryValidator.EntryValidatorBuilder.DEFAULT_ENTRY_SEPARATOR);
        }

        return false;
    }
}
