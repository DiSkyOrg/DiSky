package net.itsthesky.disky.elements.properties.messages;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Message Guild")
@Description("Get the guild where the message was sent. Can be null if it's in PM or not in guild!")
@Examples("guild of event-message")
public class MessageGuild extends SimplePropertyExpression<Message, Guild> {

    static {
        register(
                MessageGuild.class,
                Guild.class,
                "[discord] [message] guild",
                "message"
        );
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "guild";
    }

    @Override
    public @Nullable Guild convert(Message original) {
        if (original.isFromGuild())
            return original.getGuild();
        return null;
    }

    @Override
    public @NotNull Class<? extends Guild> getReturnType() {
        return Guild.class;
    }
}
