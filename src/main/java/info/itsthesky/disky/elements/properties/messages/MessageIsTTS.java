package info.itsthesky.disky.elements.properties.messages;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import info.itsthesky.disky.api.skript.PropertyCondition;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;

@Name("Is TTS")
@Description("Return true if the message is TTS (TextToSpeech).")
@Examples("event-message is tts")
public class MessageIsTTS extends PropertyCondition<Message> {

    static {
                register(
                MessageIsTTS.class,
                PropertyType.BE,
                "(tts|text to speech)",
                "message"
        );
    }

    @Override
    public boolean check(@NotNull Message msg) {
        return msg.isTTS();
    }

    @Override
    protected String getPropertyName() {
        return "tts";
    }
}
