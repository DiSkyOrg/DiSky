package info.itsthesky.disky.elements.properties;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
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

    @Override
    public @Nullable GuildChannel convert(Object entity) {
        if (entity instanceof Message)
            return ((Message) entity).getChannel().asGuildMessageChannel();
        if (entity instanceof Webhook) {
            final Webhook webhook = (Webhook) entity;
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
