package net.itsthesky.diskytest.skript.utilities;

import ch.njol.skript.Skript;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.ExpressionType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Proxy;

/**
 * <p>Returns the most-recently dispatched fake message in the given channel,
 * or {@code null} if none. Equivalent to {@code last element of fake history of X}
 * but reads more naturally in tests.</p>
 */
public class PropFakeLastMessage extends SimplePropertyExpression<TextChannel, Message> {

    static {
        Skript.registerExpression(
                PropFakeLastMessage.class,
                Message.class,
                ExpressionType.PROPERTY,
                "[the] last fake message (of|in) %textchannel%",
                "%textchannel%'[s] last fake message"
        );
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "last fake message";
    }

    @Override
    public @Nullable Message convert(TextChannel channel) {
        if (channel == null || !Proxy.isProxyClass(channel.getClass())) return null;
        Object handler = Proxy.getInvocationHandler(channel);
        if (!(handler instanceof net.itsthesky.diskytest.fake.FakeTextChannel fake))
            return null;
        var last = fake.getFakeHistory().peekLast();
        return last == null ? null : last.asProxy();
    }

    @Override
    public @NotNull Class<? extends Message> getReturnType() {
        return Message.class;
    }
}