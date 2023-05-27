package info.itsthesky.disky.elements.properties.attachments;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.util.Timespan;
import ch.njol.util.coll.CollectionUtils;
import info.itsthesky.disky.core.Debug;
import net.dv8tion.jda.api.entities.Message;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprAttDuration extends SimplePropertyExpression<Message.Attachment, Timespan> {

    static {
        register(ExprAttDuration.class, Timespan.class,
                "[discord] duration",
                "attachment"
        );
    }

    @Nullable
    @Override
    public Timespan convert(Message.Attachment entity) {
        if (entity.getDuration() <= 0) {
            Debug.debug(this, Debug.Type.INVALID_STATE, "The attachment is not an audio file!");
            return null;
        }

        return new Timespan((long) (entity.getDuration() * 1000L));
    }

    @Override
    public @NotNull Class<? extends Timespan> getReturnType() {
        return Timespan.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "attachments audio duration";
    }

    @Override
    public Class<?> @NotNull [] acceptChange(Changer.@NotNull ChangeMode mode) {
        return CollectionUtils.array();
    }

    @Override
    public void change(@NotNull Event e, Object @NotNull [] delta, Changer.@NotNull ChangeMode mode) {

    }
}