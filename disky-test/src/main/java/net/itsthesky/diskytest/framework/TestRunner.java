package net.itsthesky.diskytest.framework;

import net.itsthesky.disky.core.SkriptUtils;
import net.itsthesky.diskytest.skript.EvtDiSkyTest;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Executes registered {@code disky test} entries and reports results.
 *
 * <p>Toggles {@link SkriptUtils#TEST_MODE} for the duration of the run so that
 * DiSky's {@code sync()} / {@code async()} calls execute inline, making event
 * dispatch fully deterministic.
 */
public final class TestRunner {

    // ── Visual constants ─────────────────────────────────────────────────────
    private static final int  WIDTH      = 50;
    private static final char H_BAR      = '─';
    private static final char H_DOUBLE   = '═';
    private static final String PASS_ICON = "&a✔";
    private static final String FAIL_ICON = "&c✗";
    private static final String ERR_ICON  = "&c!";

    private TestRunner() {}

    // ── Public entry point ───────────────────────────────────────────────────

    public static List<TestResult> runAll(CommandSender out, String filter) {
        List<TestResult> results = new ArrayList<>();
        Map<String, EvtDiSkyTest> tests = TestRegistry.all();

        if (tests.isEmpty()) {
            send(out, "&e  No tests registered. Drop .sk files in plugins/Skript/scripts/.");
            return results;
        }

        String f = filter == null ? null : filter.toLowerCase(Locale.ROOT);

        // ── Header ──────────────────────────────────────────────────────────
        send(out, "&8" + repeat(H_DOUBLE, WIDTH));
        send(out, "&f&l  DiSkyTest &r&7— &b" + tests.size() + "&7 test(s) discovered"
                + (f != null ? "  &8[filter: &7" + f + "&8]" : ""));
        send(out, "&8" + repeat(H_DOUBLE, WIDTH));
        send(out, "");

        boolean previousTestMode = SkriptUtils.TEST_MODE;
        SkriptUtils.TEST_MODE = true;
        try {
            for (Map.Entry<String, EvtDiSkyTest> e : tests.entrySet()) {
                if (f != null && !e.getKey().toLowerCase(Locale.ROOT).contains(f))
                    continue;
                results.add(runOne(out, e.getKey(), e.getValue()));
                send(out, "");
            }
        } finally {
            SkriptUtils.TEST_MODE = previousTestMode;
        }

        printSummary(out, results);
        return results;
    }

    // ── Per-test rendering ───────────────────────────────────────────────────

    private static TestResult runOne(CommandSender out, String name, EvtDiSkyTest test) {
        // Header line:  ┌─ <name> ─────
        String title = " &f&l" + name + " &8";
        send(out, "&8┌" + repeat(H_BAR, 2) + title + repeat(H_BAR, Math.max(1, WIDTH - 4 - stripped(title))));

        TestFixture fixture = TestFixture.create(name);
        try {
            test.execute(fixture);
        } catch (Throwable t) {
            fixture.recordError("Uncaught exception: " + t.getClass().getSimpleName() + ": " + t.getMessage());
            t.printStackTrace();
        } finally {
            try {
                fixture.cleanup();
            } catch (Throwable t) {
                System.err.println("[DiSkyTest] cleanup failed for '" + name + "': " + t);
            }
        }

        TestResult res = fixture.toResult();

        // One line per assertion
        for (AssertionRecord a : res.assertions()) {
            String icon = a.passed() ? PASS_ICON : FAIL_ICON;
            send(out, "&8│  " + icon + " &7" + a.expression());
            if (!a.passed() && a.message() != null)
                send(out, "&8│     &8└ &e" + a.message());
        }

        // Errors (exceptions)
        for (String err : res.errors())
            send(out, "&8│  " + ERR_ICON + " &c" + err);

        // Footer line:  └─ PASS/FAIL (x/y) ─────
        String verdict = res.isSuccess()
                ? "&a PASS &7(" + res.assertions().size() + " assertion(s)) "
                : "&c FAIL &7(" + res.passed() + "/" + res.assertions().size() + " assertion(s)) ";
        send(out, "&8└" + repeat(H_BAR, 2) + verdict + "&8" + repeat(H_BAR, Math.max(1, WIDTH - 4 - stripped(verdict))));

        return res;
    }

    // ── Summary block ────────────────────────────────────────────────────────

    private static void printSummary(CommandSender out, List<TestResult> results) {
        if (results.isEmpty()) return;

        int pass   = (int) results.stream().filter(TestResult::isSuccess).count();
        int fail   = results.size() - pass;
        int total  = results.stream().mapToInt(r -> r.assertions().size()).sum();
        int passed = results.stream().mapToInt(TestResult::passed).sum();

        send(out, "&8" + repeat(H_DOUBLE, WIDTH));
        send(out, "&f&l  Summary");
        send(out, "&8" + repeat(H_BAR, WIDTH));
        send(out, "  &7Tests      &f" + results.size()
                + "  &a" + pass + " passed&7, &c" + fail + " failed");
        send(out, "  &7Assertions &f" + total
                + "  &a" + passed + " passed&7, &c" + (total - passed) + " failed");
        send(out, "&8" + repeat(H_BAR, WIDTH));

        // Per-test one-liner recap
        for (TestResult r : results) {
            String icon = r.isSuccess() ? PASS_ICON : FAIL_ICON;
            send(out, "  " + icon + " &7" + r.name()
                    + " &8(" + r.passed() + "/" + r.assertions().size() + ")");
            if (!r.isSuccess()) {
                for (AssertionRecord a : r.assertions())
                    if (!a.passed())
                        send(out, "     &8└ &c" + a.expression()
                                + (a.message() != null ? " &8— &e" + a.message() : ""));
                for (String err : r.errors())
                    send(out, "     &8! &c" + err);
            }
        }

        send(out, "&8" + repeat(H_DOUBLE, WIDTH));
        String overall = fail == 0 ? "&a&l  ALL TESTS PASSED" : "&c&l  " + fail + " TEST(S) FAILED";
        send(out, overall);
        send(out, "&8" + repeat(H_DOUBLE, WIDTH));
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    /** Repeats character {@code c} exactly {@code n} times. */
    private static String repeat(char c, int n) {
        if (n <= 0) return "";
        return String.valueOf(c).repeat(n);
    }

    /**
     * Approximates the visible length of a string after stripping {@code &X} color codes.
     * Used to right-pad separator lines to {@link #WIDTH}.
     */
    private static int stripped(String s) {
        return s.replaceAll("&[0-9a-fk-orA-FK-OR]", "").length();
    }

    private static void send(CommandSender out, String legacy) {
        out.sendMessage(legacy.replace('&', '\u00A7'));
    }
}
