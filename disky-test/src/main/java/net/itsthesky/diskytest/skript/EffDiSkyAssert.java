package net.itsthesky.diskytest.skript;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import net.itsthesky.diskytest.framework.AssertionRecord;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

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
        testEvent.getFixture().recordAssertion(new AssertionRecord(passed, rawSource, msg));
    }

    @Override
    public String toString(@Nullable Event e, boolean debug) {
        return "disky assert " + rawSource;
    }
}
