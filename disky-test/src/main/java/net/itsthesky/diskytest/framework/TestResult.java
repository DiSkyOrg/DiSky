package net.itsthesky.diskytest.framework;

import java.util.List;

/**
 * Aggregate result of one {@code disky test} run.
 */
public record TestResult(String name, List<AssertionRecord> assertions, List<String> errors) {

    public int passed() {
        return (int) assertions.stream().filter(AssertionRecord::passed).count();
    }

    public int failed() {
        return assertions.size() - passed();
    }

    public boolean isSuccess() {
        return errors.isEmpty() && failed() == 0;
    }

    /** One-line plain-text summary (no color codes). Used in logs/tests. */
    public String summary() {
        String tag = isSuccess() ? "PASS" : "FAIL";
        return tag + "  " + name + "  (" + passed() + "/" + assertions.size() + " assertion(s))";
    }
}
