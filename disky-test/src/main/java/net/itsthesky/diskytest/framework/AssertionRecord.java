package net.itsthesky.diskytest.framework;

import org.jetbrains.annotations.Nullable;

/**
 * One assertion result captured by {@code disky assert}.
 *
 * @param passed   {@code true} if the asserted condition held
 * @param expression source-side rendering of the condition (for reporting)
 * @param message  optional user-supplied failure message
 */
public record AssertionRecord(boolean passed, String expression, @Nullable String message) {

    @Override
    public String toString() {
        return (passed ? "PASS  " : "FAIL  ") + expression
                + (message != null ? "  -- " + message : "");
    }
}
