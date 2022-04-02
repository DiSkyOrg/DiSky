package info.itsthesky.disky.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.Variable;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.api.skript.WaiterEffect;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unchecked")
public class CreateAction extends WaiterEffect<Object> {

    static {
        Skript.registerEffect(
                CreateAction.class,
                "create [the] [(action|manager)] %roleaction/channelaction% and store (it|the (role|channel)) in %object%"
        );
    }

    private Expression<Object> exprAction;

    @Override
    public boolean initEffect(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        exprAction = (Expression<Object>) expressions[0];
        setChangedVariable((Variable) expressions[1]);
        return true;
    }

    @Override
    public void runEffect(Event e) {
        final AuditableRestAction<Object> action = (AuditableRestAction<Object>) parseSingle(exprAction, e, null);
        if (action == null) {
            restart();
            return;
        }
        action.queue(this::restart);
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "create action " + exprAction.toString(e, debug);
    }
}
