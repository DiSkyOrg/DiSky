package net.itsthesky.disky.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.config.Node;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.AsyncEffect;
import ch.njol.util.Kleenean;
import net.itsthesky.disky.api.skript.INodeHolder;
import net.itsthesky.disky.elements.sections.handler.DiSkyRuntimeHandler;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.itsthesky.disky.api.skript.EasyElement.anyNull;
import static net.itsthesky.disky.api.skript.EasyElement.parseSingle;

@SuppressWarnings("unchecked")
public class CreateAction extends AsyncEffect implements INodeHolder {

    static {
        Skript.registerEffect(
                CreateAction.class,
                "create [the] [(action|manager)] %roleaction/channelaction% and store (it|the (role|channel)) in %~objects%"
        );
    }

    private Expression<Object> exprAction;
    private Expression<Object> exprResult;
    private Node node;

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, ParseResult parseResult) {
        getParser().setHasDelayBefore(Kleenean.TRUE);
        node = getParser().getNode();

        exprAction = (Expression<Object>) expressions[0];
        exprResult = (Expression<Object>) expressions[1];

        return Changer.ChangerUtils.acceptsChange(exprResult, Changer.ChangeMode.SET, Object.class);
    }

    @Override
    public void execute(Event e) {
        final AuditableRestAction<Object> action = (AuditableRestAction<Object>) parseSingle(exprAction, e, null);
        if (anyNull(this, action))
            return;

        final Object result;
        try {
            result = action.complete();
        } catch (Exception ex) {
            DiSkyRuntimeHandler.error(ex, node);
            return;
        }

        exprResult.change(e, new Object[] {result}, Changer.ChangeMode.SET);
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "create action " + exprAction.toString(e, debug)
                + " and store it in " + exprResult.toString(e, debug);
    }

    @Override
    @NotNull
    public Node getNode() {
        return node;
    }
}
