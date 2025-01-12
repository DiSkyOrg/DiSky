package info.itsthesky.disky.elements.properties.attachments;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import net.dv8tion.jda.api.entities.Message;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Attachments URL")
@Description("Get the url of an attachment.")
@Since("1.7")
public class ExprAttURL extends SimplePropertyExpression<Message.Attachment, String> {

    static {
        register(ExprAttURL.class, String.class,
                "[discord] [attachment] ur(l|i)",
                "attachment"
        );
    }

    @Nullable
    @Override
    public String convert(Message.Attachment entity) {
        return entity.getUrl();
    }

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    protected String getPropertyName() {
        return "attachments url";
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