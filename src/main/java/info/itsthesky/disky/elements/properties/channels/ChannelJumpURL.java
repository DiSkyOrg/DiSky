package info.itsthesky.disky.elements.properties.channels;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.dv8tion.jda.api.entities.GuildChannel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Channel Jump URL")
@Description({"Returns the jump-to URL for this channel.",
        "Clicking this URL in the Discord client will cause the client to jump to the specified channel."})
@Examples("reply with channel url of event-channel")
public class ChannelJumpURL extends SimplePropertyExpression<GuildChannel, String> {

    static {
        register(
                ChannelJumpURL.class,
                String.class,
                "channel [jump] url",
                "channel"
        );
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "channel jump url";
    }

    @Override
    public @Nullable String convert(GuildChannel guildChannel) {
        return guildChannel.getJumpUrl();
    }

    @Override
    public @NotNull Class<? extends String> getReturnType() {
        return String.class;
    }
}
