package net.itsthesky.diskytest.skript;

import ch.njol.skript.Skript;
import ch.njol.skript.conditions.CondCompare;
import ch.njol.skript.lang.*;
import ch.njol.util.Kleenean;
import net.itsthesky.diskytest.framework.AssertionRecord;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.comparator.Relation;

import java.lang.reflect.Field;

/**
 * Skript effect: {@code disky assert <condition> [with <message>]}
 *
 * <p>Parses the condition at parse-time using {@link Condition#parse(String, String)}
 * and records the result on the active {@link EvtDiSkyTest.TestRunEvent}'s fixture.
 */
public class EffDiSkyAssert extends Effect {

    static {
        Skript.registerEffect(EffDiSkyAssert.class,
                "disky assert <.+?>[ with %-string%]");
    }

    private Condition condition;
    private @Nullable Expression<String> message;
    private String rawSource;

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed,
                        SkriptParser.ParseResult parseResult) {
        rawSource = parseResult.regexes.get(0).group();
        condition = Condition.parse(rawSource,
                "Cannot parse 'disky assert' condition: " + rawSource);
        if (condition == null) return false;
        message = exprs.length > 0 ? (Expression<String>) exprs[0] : null;
        return true;
    }

    @Override
    protected void execute(Event e) {
        if (!(e instanceof EvtDiSkyTest.TestRunEvent testEvent)) {
            // Outside of a test scope: noisy but non-fatal.
            Skript.warning("'disky assert' executed outside of 'disky test' — ignored.");
            return;
        }


        boolean passed;
        try {
            passed = condition.check(e);
        } catch (Throwable t) {
            testEvent.getFixture().recordError(
                    "Exception evaluating assertion '" + rawSource + "': " + t);
            return;
        }

        String msg = message == null ? null : message.getSingle(e);

        // Build an automatic message when none is provided and the condition is a comparison
        if (msg == null && condition instanceof CondCompare condCompare) {
            msg = buildCompareMessage(condCompare, e);
        }

        testEvent.getFixture().recordAssertion(new AssertionRecord(passed, rawSource, msg));
    }

    /**
     * Builds a human-readable assertion message from a {@link CondCompare} using reflection
     * to access its private {@code first}, {@code second}, {@code third} and {@code relation} fields.
     * <p>Examples: {@code "3 should be equal to 5"}, {@code "10 should be greater than 20"}
     */
    @Nullable
    private static String buildCompareMessage(CondCompare condCompare, Event event) {
        try {
            Field fFirst    = CondCompare.class.getDeclaredField("first");
            Field fSecond   = CondCompare.class.getDeclaredField("second");
            Field fThird    = CondCompare.class.getDeclaredField("third");
            Field fRelation = CondCompare.class.getDeclaredField("relation");
            fFirst.setAccessible(true);
            fSecond.setAccessible(true);
            fThird.setAccessible(true);
            fRelation.setAccessible(true);

            Expression<?> first    = (Expression<?>) fFirst.get(condCompare);
            Expression<?> second   = (Expression<?>) fSecond.get(condCompare);
            Expression<?> third    = (Expression<?>) fThird.get(condCompare);
            Relation      relation = (Relation) fRelation.get(condCompare);

            String leftVal  = wrap(exprToString(first, event));
            String rightVal = wrap(exprToString(second, event));
            boolean negated = condCompare.isNegated();

            String actualLeft = wrap(exprToActualString(first, event));

            if (third != null) {
                // "between X and Y" case
                String thirdVal = wrap(exprToString(third, event));
                return leftVal + " should " + (negated ? "not " : "") + "be between " + rightVal + " and " + thirdVal
                        + " but is " + actualLeft;
            }

            String keyword = relationKeyword(relation, negated);
            return leftVal + " should be " + keyword + " " + rightVal + " but is " + actualLeft;

        } catch (Exception ex) {
            return null; // fallback: no auto-message
        }
    }

    private static String exprToActualString(Expression<?> expr, Event event) {
        if (expr == null) return "null";
        Object[] values = expr.getArray(event);
        if (values == null || values.length == 0) return "null";
        if (values.length == 1) return String.valueOf(values[0]);
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < values.length; i++) {
            if (i > 0) sb.append(", ");
            sb.append(values[i]);
        }
        return sb.append("]").toString();
    }

    private static String exprToString(Expression<?> expr, Event event) {
        if (expr == null) return "null";
        if (expr instanceof LiteralString litStr)
            return "\"" + litStr.getSingle(event) + "\"";

        Object[] values = expr.getArray(event);
        if (values == null || values.length == 0) return expr.toString(event, false);
        if (values.length == 1) return String.valueOf(values[0]);
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < values.length; i++) {
            if (i > 0) sb.append(", ");
            sb.append(values[i]);
        }
        return sb.append("]").toString();
    }

    private static String relationKeyword(Relation relation, boolean negated) {
        if (negated) {
            return switch (relation) {
                case EQUAL            -> "not equal to";
                case NOT_EQUAL        -> "equal to";
                case GREATER          -> "not greater than";
                case GREATER_OR_EQUAL -> "not greater than or equal to";
                case SMALLER          -> "not less than";
                case SMALLER_OR_EQUAL -> "not less than or equal to";
            };
        }
        return switch (relation) {
            case EQUAL            -> "equal to";
            case NOT_EQUAL        -> "not equal to";
            case GREATER          -> "greater than";
            case GREATER_OR_EQUAL -> "greater than or equal to";
            case SMALLER          -> "less than";
            case SMALLER_OR_EQUAL -> "less than or equal to";
        };
    }

    private static String wrap(String s) {
        return "'" + s + "'";
    }

    @Override
    public String toString(@Nullable Event e, boolean debug) {
        return "disky assert " + rawSource;
    }
}
