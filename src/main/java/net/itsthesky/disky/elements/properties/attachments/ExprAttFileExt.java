package net.itsthesky.disky.elements.properties.attachments;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import net.dv8tion.jda.api.entities.Message;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Attachments File Extension")
@Description("Get the file extension of an attachment.")
@Since("1.7")
public class ExprAttFileExt extends SimplePropertyExpression<Message.Attachment, String> {

    static {
        register(ExprAttFileExt.class, String.class,
                "[discord] file ext[ension]",
                "attachment"
        );
    }

    @Nullable
    @Override
    public String convert(Message.Attachment entity) {
        return entity.getFileExtension();
    }

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    protected String getPropertyName() {
        return "attachments file extension";
    }

    @Nullable
    @Override
    public Class<?>[] acceptChange(Changer.ChangeMode mode) {
        return CollectionUtils.array();
    }

    @Override
    public void change(Event e, @Nullable Object[] delta, Changer.ChangeMode mode) {

    }
}