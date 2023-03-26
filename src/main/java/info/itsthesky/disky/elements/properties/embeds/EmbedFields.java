package info.itsthesky.disky.elements.properties.embeds;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import info.itsthesky.disky.api.skript.MultiplyPropertyExpression;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;

@Name("Embed fields")
@Description("Get all fields of an embed builder")
@Examples("fields of last embed")
public class EmbedFields extends MultiplyPropertyExpression<EmbedBuilder, MessageEmbed.Field> {

    static {
        register(
                EmbedFields.class,
                MessageEmbed.Field.class,
                "[discord] [embed] field[s]",
                "embedbuilder"
        );
    }

    @Override
    public @NotNull Class<? extends MessageEmbed.Field> getReturnType() {
        return MessageEmbed.Field.class;
    }

    @Override
    protected String getPropertyName() {
        return "embedfield";
    }

    @Override
    protected MessageEmbed.Field[] convert(EmbedBuilder embedBuilder) {
        return embedBuilder.getFields().toArray(new MessageEmbed.Field[0]);
    }
}
