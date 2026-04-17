package net.itsthesky.diskytest.skript.utilities;

import ch.njol.skript.Skript;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.ExpressionType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.itsthesky.diskytest.fake.FakeJDA;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Proxy;

/**
 * <p>Test-only inspector returning every message that was loopback-dispatched
 * into a {@link TextChannel} during the current test, oldest-first.</p>
 *
 * <p>Unlike DiSky's production {@code retrieve last X messages from %channel%}
 * effect (which calls JDA's {@code MessageHistory.retrievePast(...).complete()}
 * over the network), this expression reads the in-memory deque held by the
 * {@code FakeTextChannel} synchronously, so it can be used directly inside a
 * {@code disky assert}.</p>
 *
 * <p>Returns an empty array if the given channel is not a fake — never throws.</p>
 */
public class PropFakeHistory extends SimplePropertyExpression<TextChannel, Message> {

    static {
        Skript.registerExpression(
                PropFakeHistory.class,
                Message.class,
                ExpressionType.PROPERTY,
                "[the] fake (history|messages) of %textchannel%",
                "%textchannel%'[s] fake (history|messages)"
        );
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "fake history";
    }

    /** SimplePropertyExpression's single-element path is unused; we override get(). */
    @Override
    public @Nullable Message convert(TextChannel channel) {
        return null;
    }

    @Override
    protected Message @NotNull [] get(@NotNull org.bukkit.event.Event event,
                                       TextChannel @NotNull [] source) {
        if (source.length == 0) return new Message[0];
        TextChannel channel = source[0];
        if (channel == null) return new Message[0];

        // The proxy returned by FakeTextChannel.asProxy() routes through a
        // FakeEntity InvocationHandler — that's how we recover the underlying fake.
        if (!Proxy.isProxyClass(channel.getClass())) return new Message[0];
        Object handler = Proxy.getInvocationHandler(channel);
        if (!(handler instanceof net.itsthesky.diskytest.fake.FakeTextChannel fake))
            return new Message[0];

        return fake.getFakeHistory().stream()
                .map(fm -> fm.asProxy())
                .toArray(Message[]::new);
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public @NotNull Class<? extends Message> getReturnType() {
        return Message.class;
    }
}