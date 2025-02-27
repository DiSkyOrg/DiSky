package net.itsthesky.disky.elements.properties.messages;

import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;

@Name("Is Voice Message")
@Description("Return true if the message is a voice message.")
@Examples("event-message is voice message")
public class MessageIsVoiceMessage extends PropertyCondition<Message> {

    static {
        register(
                MessageIsVoiceMessage.class,
                PropertyType.BE,
                "voice message",
                "message"
        );
    }

    @Override
    public boolean check(@NotNull Message msg) {
        return msg.isVoiceMessage();
    }

    @Override
    protected String getPropertyName() {
        return "voice message";
    }
}