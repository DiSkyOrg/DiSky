package info.itsthesky.disky.elements.properties.messages;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import info.itsthesky.disky.api.skript.MultiplyPropertyExpression;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.EmbedBuilder;
import org.jetbrains.annotations.NotNull;

@Name("Message Embeds")
@Description("Get every embeds of a specific messages. Keep in mind only webhook are allowed to send more than one embed!")
@Examples("embeds of event-message")
public class MessageEmbeds extends MultiplyPropertyExpression<Message, EmbedBuilder> {

    static {
        register(
                MessageEmbeds.class,
                EmbedBuilder.class,
                "[discord] [message] embeds",
                "message"
        );
    }

    @Override
    protected EmbedBuilder[] convert(Message t) {
        return t.getEmbeds()
                .stream()
                .map(EmbedBuilder::new)
                .toArray(EmbedBuilder[]::new);
    }

    @Override
    public @NotNull Class<? extends EmbedBuilder> getReturnType() {
        return EmbedBuilder.class;
    }

    @Override
    protected String getPropertyName() {
        return "embeds";
    }
}
