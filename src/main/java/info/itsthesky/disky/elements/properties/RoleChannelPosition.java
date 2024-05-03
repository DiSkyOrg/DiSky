package info.itsthesky.disky.elements.properties;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import info.itsthesky.disky.api.skript.EasyElement;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.attribute.IPositionableChannel;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RoleChannelPosition extends SimplePropertyExpression<Object, Number> {

    static {
        register(
                RoleChannelPosition.class,
                Number.class,
                "[(role|channel)] position",
                "role/roleaction/channel/channelaction"
        );
    }

    @Override
    public @Nullable Number convert(Object entity) {
        if (entity instanceof Role)
            return ((Role) entity).getPosition();
        if (entity instanceof IPositionableChannel)
            return ((IPositionableChannel) entity).getPosition();

        return null;
    }

    @Override
    public Class<?> @NotNull [] acceptChange(Changer.@NotNull ChangeMode mode) {
        if (EasyElement.equalAny(mode, Changer.ChangeMode.SET, Changer.ChangeMode.ADD, Changer.ChangeMode.REMOVE))
            return new Class[] {Number.class};

        return new Class[0];
    }

    @Override
    public void change(@NotNull Event event, Object @NotNull [] delta, Changer.@NotNull ChangeMode mode) {
        if (!EasyElement.isValid(delta))
            return;

        final Number value = (Number) delta[0];
        int amount = value.intValue();

        RestAction action = null;

        switch (mode) {
            case ADD:
            case REMOVE:
                for (Object entity : getExpr().getArray(event)) {
                    int current = -1;
                    if (entity instanceof Role)
                        current = ((Role) entity).getPosition();
                    if (entity instanceof IPositionableChannel)
                        current = ((IPositionableChannel) entity).getPosition();
                    if (current == -1)
                        continue;

                    current += mode == Changer.ChangeMode.ADD ? amount : -amount;

                    if (entity instanceof Role)
                        action = ((Role) entity).getGuild().modifyRolePositions().selectPosition(((Role) entity)).moveTo(current);
                    if (entity instanceof IPositionableChannel)
                        action = ((IPositionableChannel) entity).getManager().setPosition(current);
                }
                break;
            case SET:
                for (Object entity : getExpr().getArray(event)) {
                    if (entity instanceof Role)
                        action = ((Role) entity).getGuild().modifyRolePositions().selectPosition(((Role) entity)).moveTo(amount);
                    if (entity instanceof IPositionableChannel)
                        action = ((IPositionableChannel) entity).getManager().setPosition(amount);
                }
                break;
        }

        if (action != null)
            action.queue();
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "channel/role position";
    }

    @Override
    public @NotNull Class<? extends Number> getReturnType() {
        return Number.class;
    }
}
