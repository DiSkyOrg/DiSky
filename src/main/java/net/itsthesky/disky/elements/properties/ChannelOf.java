package net.itsthesky.disky.elements.properties;

import ch.njol.skript.config.Node;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.itsthesky.disky.elements.sections.handler.DiSkyRuntimeHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ChannelOf extends SimplePropertyExpression<Object, GuildChannel> {

    static {
        register(
                ChannelOf.class,
                GuildChannel.class,
                "[discord] [(message|webhook)] [text]( |-)channel",
                "message/webhook"
        );
    }

    private Node node;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        node = getParser().getNode();
        return super.init(expressions, matchedPattern, isDelayed, parseResult);
    }

    @Override
    public @Nullable GuildChannel convert(Object entity) {
        if (entity instanceof final Message message) {
            final var channel = message.getChannel();
            if (channel.getType().isGuild() && channel.getType().isMessage())
                return channel.asGuildMessageChannel();

            DiSkyRuntimeHandler.error(new IllegalArgumentException("The given message (" + message.getId() + ") is not in a guild text channel, " +
                    "therefore it cannot have a channel attached to it. "), node, false);
            return null;
        }

        if (entity instanceof final Webhook webhook) {
            if (webhook.getChannel().getType().equals(ChannelType.FORUM))
                return webhook.getChannel().asForumChannel();

            if (webhook.getChannel().getType().isGuild() && webhook.getChannel().getType().isMessage())
                return webhook.getChannel().asGuildMessageChannel();
        }

        return null;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "discord channel";
    }

    @Override
    public @NotNull Class<? extends GuildChannel> getReturnType() {
        return GuildChannel.class;
    }

}
