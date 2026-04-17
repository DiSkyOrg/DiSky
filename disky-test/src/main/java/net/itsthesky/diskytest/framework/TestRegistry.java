package net.itsthesky.diskytest.framework;

import net.itsthesky.diskytest.skript.EvtDiSkyTest;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Global registry of {@code disky test} entries, populated when the
 * {@link EvtDiSkyTest} structures register their triggers.
 */
public final class TestRegistry {

    private static final Map<String, EvtDiSkyTest> TESTS = new LinkedHashMap<>();

    private TestRegistry() {}

    public static void register(String name, EvtDiSkyTest test) {
        TESTS.put(name, test);
    }

    public static void unregister(String name) {
        TESTS.remove(name);
    }

    public static void clear() {
        TESTS.clear();
    }

    public static Map<String, EvtDiSkyTest> all() {
        return new LinkedHashMap<>(TESTS);
    }

    public static int size() { return TESTS.size(); }
}
