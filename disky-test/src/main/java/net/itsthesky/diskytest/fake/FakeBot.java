package net.itsthesky.diskytest.fake;

import net.itsthesky.disky.core.Bot;
import net.itsthesky.disky.core.BotOptions;

/**
 * Convenience factory: assembles a DiSky {@link Bot} backed by a {@link FakeJDA}.
 *
 * <p>The resulting {@code Bot} can be passed to
 * {@link net.itsthesky.disky.managers.BotManager#addBot(Bot)} so that DiSky's
 * standard listener wiring (CommandListener, ReactionListener, MessageManager,
 * MemberRemoveEventListener and especially {@code CoreEventListener}) attaches
 * itself to the fake JDA exactly like in production.
 */
public final class FakeBot {

    private FakeBot() {}

    public static Bot create(String botName) {
        FakeJDA fakeJDA = new FakeJDA(botName);
        BotOptions options = new BotOptions();
        return new Bot(botName, fakeJDA.asProxy(), options, null, false);
    }

    public static Bot create(String botName, FakeJDA fakeJDA) {
        BotOptions options = new BotOptions();
        return new Bot(botName, fakeJDA.asProxy(), options, null, false);
    }
}
