package info.itsthesky.disky.elements.properties.messages;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Message/Event Jump URL")
@Description("Get the jump URL of a specific message/scheduled event")
@Examples({"jump url of event-message",
"jump url of scheduled event with id \"000\""})
public class MessageJumpURL extends SimplePropertyExpression<Object, String> {

    static {
        register(
                MessageJumpURL.class,
                String.class,
                "[discord] [message] [jump] url",
                "message"
        );
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "jump url";
    }

    @Override
    public @Nullable String convert(Object entity) {
        if (entity instanceof Message)
            return ((Message) entity).getJumpUrl();
        else return "https://discord.com/events/" + ((ScheduledEvent) entity).getGuild().getId() + "/" + ((ScheduledEvent) entity).getId();
    }

    @Override
    public @NotNull Class<? extends String> getReturnType() {
        return String.class;
    }
}
