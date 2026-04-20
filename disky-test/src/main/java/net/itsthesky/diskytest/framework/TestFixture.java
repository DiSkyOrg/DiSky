package net.itsthesky.diskytest.framework;

import net.itsthesky.disky.DiSky;
import net.itsthesky.disky.api.events.EventListener;
import net.itsthesky.disky.core.Bot;
import net.itsthesky.disky.managers.CoreEventListener;
import net.itsthesky.diskytest.fake.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Per-test bundle of fake entities plus result tracking.
 *
 * <p>Each {@code disky test} runs against a fresh fixture: a registered
 * {@link FakeBot}, a default guild, a default text channel, a default member
 * and the bot's self-member.
 *
 * <p>{@link #cleanup()} must be called after the test completes, regardless of
 * outcome, to remove temporary listeners and unregister the fake bot.
 */
public class TestFixture {

    private final String testName;
    private final Bot bot;
    private final FakeJDA fakeJDA;
    private final FakeGuild guild;
    private final FakeTextChannel channel;
    private final FakeMember member;
    private final FakeUser user;
    private final FakeMember selfMember;

    private final List<EventListener<?>> temporaryListeners = new ArrayList<>();
    private final List<AssertionRecord> assertions = new ArrayList<>();
    private final List<String> errors = new ArrayList<>();

    private TestFixture(String testName, Bot bot, FakeJDA fakeJDA, FakeGuild guild,
                        FakeTextChannel channel, FakeMember member, FakeUser user,
                        FakeMember selfMember) {
        this.testName = testName;
        this.bot = bot;
        this.fakeJDA = fakeJDA;
        this.guild = guild;
        this.channel = channel;
        this.member = member;
        this.user = user;
        this.selfMember = selfMember;
    }

    public static TestFixture create(String testName) {
        // Build the fake JDA + self member first.
        String botName = "test-bot-" + Math.abs(testName.hashCode());
        FakeJDA fakeJDA = new FakeJDA(botName);

        // Self user proxy → self member of the default guild
        FakeUser selfUserView = new FakeUser(fakeJDA, botName, true);
        // Note: selfUserView is a separate User entity; the SelfUser is fakeJDA.getFakeSelfUser().
        // For the default guild's self-member we use the SelfUser proxy directly.
        FakeMember selfMember = new FakeMember(null, selfUserView); // guild assigned next

        FakeGuild guild = new FakeGuild(fakeJDA, "Test Guild", selfMember);
        // Re-link selfMember.guild via reflection? Simpler: rebuild selfMember once guild exists.
        // Since FakeMember holds final fields, we replace it:
        FakeMember realSelfMember = new FakeMember(guild, selfUserView);
        fakeJDA.registerFakeGuild(guild);

        FakeTextChannel channel = new FakeTextChannel(guild, "general");

        FakeUser user = new FakeUser(fakeJDA, "TestUser", false);
        FakeMember member = new FakeMember(guild, user);

        Bot bot = FakeBot.create(botName, fakeJDA);
        DiSky.getManager().addBot(bot);

        return new TestFixture(testName, bot, fakeJDA, guild, channel, member, user, realSelfMember);
    }

    // ===== Recording =====

    public void recordAssertion(AssertionRecord record) { assertions.add(record); }
    public void recordError(String error) { errors.add(error); }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public void registerTemporaryListener(EventListener<?> listener) {
        temporaryListeners.add(listener);
        CoreEventListener.addListener((EventListener) listener);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public void cleanup() {
        for (EventListener<?> l : temporaryListeners) {
            try { CoreEventListener.removeListener((EventListener) l); }
            catch (Throwable ignored) {}
        }
        temporaryListeners.clear();
        try { DiSky.getManager().removeBot(bot); } catch (Throwable ignored) {}
    }

    public TestResult toResult() {
        return new TestResult(testName, new ArrayList<>(assertions), new ArrayList<>(errors));
    }

    // ===== Getters for variable injection =====

    public String getTestName() { return testName; }
    public Bot getBot() { return bot; }
    public FakeJDA getFakeJDA() { return fakeJDA; }

    /** Returns the JDA-level guild proxy (suitable for Skript variables). */
    public net.dv8tion.jda.api.entities.Guild getGuild() { return guild.asProxy(); }
    public net.dv8tion.jda.api.entities.channel.concrete.TextChannel getChannel() { return channel.asProxy(); }
    public net.dv8tion.jda.api.entities.Member getMember() { return member.asProxy(); }
    public net.dv8tion.jda.api.entities.User getUser() { return user.asProxy(); }
    public net.dv8tion.jda.api.entities.Member getSelfMember() { return selfMember.asProxy(); }

    public FakeTextChannel getFakeChannel() { return channel; }
    public FakeGuild getFakeGuild() { return guild; }
}
