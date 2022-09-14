package info.itsthesky.disky.elements.properties;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.*;
import net.dv8tion.jda.api.entities.channel.attribute.*;
import net.dv8tion.jda.api.entities.channel.middleman.*;
import net.dv8tion.jda.api.entities.channel.concrete.*;
import net.dv8tion.jda.api.entities.sticker.GuildSticker;
import net.dv8tion.jda.api.entities.sticker.RichSticker;
import net.dv8tion.jda.api.entities.sticker.Sticker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Guild Of")
@Description({"Return the guild of a specific entity.",
"This can return null if the entity is not guild-based, like private message channel or message."})
@Examples({"guild of event-member",
"guild of event-channel"})
public class GuildOf extends SimplePropertyExpression<Object, Guild> {

    static {
        register(
                GuildOf.class,
                Guild.class,
                "guild",
                "channel/role/sticker/member/message"
        );
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "guild";
    }

    @Override
    public @Nullable Guild convert(Object entity) {
        if (entity instanceof Role)
            return ((Role) entity).getGuild();
        if (entity instanceof StandardGuildMessageChannel)
            return ((StandardGuildMessageChannel) entity).getGuild();
        if (entity instanceof Member)
            return ((Member) entity).getGuild();
        if (entity instanceof RichSticker)
            return ((RichSticker) entity).getTags().equals(Sticker.Type.GUILD) ? ((GuildSticker) entity).getGuild() : null;
        return null;
    }

    @Override
    public @NotNull Class<? extends Guild> getReturnType() {
        return Guild.class;
    }
}
