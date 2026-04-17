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

    public String summary() {
        if (isSuccess()) return "PASS  " + name + "  (" + assertions.size() + " assertion(s))";
        StringBuilder sb = new StringBuilder("FAIL  ").append(name)
                .append("  (").append(passed()).append('/').append(assertions.size())
                .append(" assertion(s))");
        for (AssertionRecord a : assertions)
            if (!a.passed()) sb.append("\n    - ").append(a);
        for (String err : errors) sb.append("\n    ! ").append(err);
        return sb.toString();
    }
}
