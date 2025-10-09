package net.itsthesky.disky.elements.properties.embeds;

import ch.njol.skript.classes.Changer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.itsthesky.disky.api.skript.MultiplyPropertyExpression;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PropEmbedFields extends MultiplyPropertyExpression<EmbedBuilder, MessageEmbed.Field> {

    static {
        register(
                PropEmbedFields.class,
                MessageEmbed.Field.class,
                "[embed] fields",
                "embedbuilder"
        );
    }

    @Override
    protected MessageEmbed.Field[] convert(EmbedBuilder embedBuilder) {
        if (embedBuilder == null || embedBuilder.isEmpty()) return new MessageEmbed.Field[0];
        return embedBuilder.build().getFields().toArray(new MessageEmbed.Field[0]);
    }

    @Override
    public Class<?> @Nullable [] acceptChange(Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.ADD || mode == Changer.ChangeMode.REMOVE_ALL || mode == Changer.ChangeMode.SET || mode == Changer.ChangeMode.RESET)
            return new Class[]{MessageEmbed.Field.class,
                    MessageEmbed.Field[].class};
        return null;
    }

    @Override
    public void change(Event event, Object @Nullable [] delta, Changer.ChangeMode mode) {
        final var embeds = getExpr().getArray(event);
        if (embeds == null) return;
        for (EmbedBuilder embed : embeds) {
            if (embed == null) continue;
            switch (mode) {
                case ADD:
                    if (delta == null) return;
                    for (Object o : delta) {
                        if (!(o instanceof MessageEmbed.Field field)) continue;
                        embed.addField(field);
                    }
                    break;
                case SET:
                    embed.clearFields();
                    if (delta == null) return;
                    for (Object o : delta) {
                        if (!(o instanceof MessageEmbed.Field field)) continue;
                        embed.addField(field);
                    }
                    break;
                case RESET:
                case REMOVE_ALL:
                    embed.clearFields();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public @NotNull Class<? extends MessageEmbed.Field> getReturnType() {
        return MessageEmbed.Field.class;
    }

    @Override
    protected String getPropertyName() {
        return "embed fields";
    }
}
