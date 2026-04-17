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

    private TestRunner() {}

    public static List<TestResult> runAll(CommandSender out, String filter) {
        List<TestResult> results = new ArrayList<>();
        Map<String, EvtDiSkyTest> tests = TestRegistry.all();

        if (tests.isEmpty()) {
            send(out, "&e[DiSkyTest] No tests registered. Drop .sk files in plugins/DiSkyTest/tests/.");
            return results;
        }

        String f = filter == null ? null : filter.toLowerCase(Locale.ROOT);
        send(out, "&7>>> &fDiSky Test Runner &7— discovered &b" + tests.size() + "&7 test(s)");

        boolean previousTestMode = SkriptUtils.TEST_MODE;
        SkriptUtils.TEST_MODE = true;
        try {
            for (Map.Entry<String, EvtDiSkyTest> e : tests.entrySet()) {
                if (f != null && !e.getKey().toLowerCase(Locale.ROOT).contains(f))
                    continue;
                results.add(runOne(out, e.getKey(), e.getValue()));
            }
        } finally {
            SkriptUtils.TEST_MODE = previousTestMode;
        }

        printSummary(out, results);
        return results;
    }

    private static TestResult runOne(CommandSender out, String name, EvtDiSkyTest test) {
        send(out, "&7>>> &fRunning: &b" + name);
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
        send(out, "    " + (res.isSuccess() ? "&a" : "&c") + res.summary());
        return res;
    }

    private static void printSummary(CommandSender out, List<TestResult> results) {
        int pass = 0, fail = 0, asserts = 0;
        for (TestResult r : results) {
            if (r.isSuccess()) pass++; else fail++;
            asserts += r.assertions().size();
        }
        send(out, "&7>>> &fTotal: &b" + results.size() + "&7 tests, &b" + asserts
                + "&7 assertions, &a" + pass + " PASS&7, &c" + fail + " FAIL");
    }

    private static void send(CommandSender out, String legacy) {
        // Replace § color codes — Bukkit accepts them via &-style if pre-translated.
        String translated = legacy.replace('&', '\u00A7');
        out.sendMessage(translated);
    }
}
