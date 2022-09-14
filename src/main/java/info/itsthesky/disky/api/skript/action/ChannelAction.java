package info.itsthesky.disky.api.skript.action;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.bukkit.event.Event;

public abstract class ChannelAction<T> extends AbstractNewAction<T, TextChannel> {

    protected static void register(
            Class clazz,
            Class entityClazz,
            String actionName
    ) {
        Skript.registerExpression(
                clazz,
                entityClazz,
                ExpressionType.SIMPLE,
                "[a] new "+actionName+" (action|manager) in [the] [channel] %textchannel% [(using|with) [the] [bot] %-bot%]"
        );
    }

    @Override
    public String entityToString(Expression<TextChannel> entity, Event e, boolean debug) {
        return "in channel " + entity.toString(e, debug);
    }

    @Override
    public boolean isSingle() {
        return true;
    }
}
