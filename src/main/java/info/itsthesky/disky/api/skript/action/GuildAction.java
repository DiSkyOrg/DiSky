package info.itsthesky.disky.api.skript.action;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import net.dv8tion.jda.api.entities.Guild;
import org.bukkit.event.Event;

@SuppressWarnings("unchecked")
public abstract class GuildAction<T> extends AbstractNewAction<T, Guild> {

    protected static void register(
            Class clazz,
            Class entityClazz,
            String actionName
    ) {
        Skript.registerExpression(
                clazz,
                entityClazz,
                ExpressionType.SIMPLE,
                "[a] new "+actionName+" (action|manager) in [the] [guild] %guild% [(using|with) [the] [bot] %-bot%]"
        );
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public String entityToString(Expression<Guild> entity, Event e, boolean debug) {
        return "in guild " + entity.toString(e, debug);
    }
}
