package info.itsthesky.disky.elements.conditions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.conditions.base.PropertyCondition;
import net.dv8tion.jda.api.entities.MessageEmbed;

@Name("Field is inline")
@Description("Check whether the provided field is inline or not.")
@Examples({"if first element of (fields of last embed) is inline:",
        "if first element of (fields of last embed) is not in-line:"})
public class isInline extends PropertyCondition<MessageEmbed.Field> {

    static {
        register(
                isInline.class,
                PropertyType.BE,
                "[an] in[-]line [field]",
                "embedfield"
        );
    }

    @Override
    public boolean check(MessageEmbed.Field field) {
        return field.isInline();
    }

    @Override
    protected String getPropertyName() {
        return "inline";
    }
}
