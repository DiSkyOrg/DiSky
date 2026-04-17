package net.itsthesky.diskytest.skript;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.Trigger;
import ch.njol.skript.variables.Variables;
import net.itsthesky.diskytest.framework.TestFixture;
import net.itsthesky.diskytest.framework.TestRegistry;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.Nullable;

/**
 * Skript event:  {@code disky test "<name>":}
 *
 * <p>Self-registers each test trigger into {@link TestRegistry} at parse time.
 * The actual body runs only when the test runner calls {@link #execute(TestFixture)}.
 */
public class EvtDiSkyTest extends ch.njol.skript.lang.SelfRegisteringSkriptEvent {

    /**
     * Bukkit event raised by the runner when executing a test body. Carries the
     * {@link TestFixture} so the assertion effect and section can record results.
     */
    public static class TestRunEvent extends org.bukkit.event.Event {
        private static final HandlerList HANDLERS = new HandlerList();
        private final TestFixture fixture;

        public TestRunEvent(TestFixture fixture) { this.fixture = fixture; }
        public TestFixture getFixture() { return fixture; }

        @Override
        public org.bukkit.event.HandlerList getHandlers() { return HANDLERS; }
        public static HandlerList getHandlerList() { return HANDLERS; }
    }

    static {
        Skript.registerEvent("DiSky Test", EvtDiSkyTest.class, TestRunEvent.class,
                "disky test %string%");
    }

    private String testName;
    private @Nullable Trigger trigger;

    @Override
    public boolean init(Literal<?>[] args, int matchedPattern, SkriptParser.ParseResult parser) {
        if (args.length == 0 || args[0].getSingle() == null) {
            Skript.error("'disky test' requires a name string");
            return false;
        }
        testName = String.valueOf(args[0].getSingle());
        return true;
    }

    @Override
    public void register(Trigger t) {
        this.trigger = t;
        TestRegistry.register(testName, this);
    }

    @Override
    public void unregister(Trigger t) {
        TestRegistry.unregister(testName);
        this.trigger = null;
    }

    @Override
    public void unregisterAll() {
        TestRegistry.clear();
        this.trigger = null;
    }

    /** Invoked by {@code TestRunner}. */
    public void execute(TestFixture fixture) {
        if (trigger == null) {
            fixture.recordError("Test '" + testName + "' has no trigger registered.");
            return;
        }
        TestRunEvent event = new TestRunEvent(fixture);
        injectLocals(event, fixture);
        trigger.execute(event);
    }

    private void injectLocals(org.bukkit.event.Event event, TestFixture fixture) {
        Variables.setVariable("bot", fixture.getBot(), event, true);
        Variables.setVariable("guild", fixture.getGuild(), event, true);
        Variables.setVariable("channel", fixture.getChannel(), event, true);
        Variables.setVariable("member", fixture.getMember(), event, true);
        Variables.setVariable("user", fixture.getUser(), event, true);
        Variables.setVariable("self", fixture.getSelfMember(), event, true);
    }

    public String getTestName() { return testName; }

    @Override
    public String toString(@Nullable org.bukkit.event.Event e, boolean debug) {
        return "disky test \"" + testName + "\"";
    }
}
