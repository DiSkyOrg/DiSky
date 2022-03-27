package info.itsthesky.disky.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.api.skript.EasyElement;
import info.itsthesky.disky.api.skript.WaiterEffect;
import info.itsthesky.disky.core.JDAUtils;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EditMessage extends WaiterEffect {

    static {
        Skript.registerEffect(
                EditMessage.class,
                "edit [the] [message] %message% (with|to show) %string/embedbuilder/messagebuilder% [(1¦[and] keep component[s])]"
        );
    }

    private Expression<Message> exprMessage;
    private Expression<Object> exprNew;
    private boolean keepComponents;

    @Override
    public boolean initEffect(Expression[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        exprMessage = (Expression<Message>) expressions[0];
        exprNew = (Expression<Object>) expressions[1];
        keepComponents = parseResult.mark == 1;
        return true;
    }

    @Override
    public void runEffect(Event e) {
        final Message message = parseSingle(exprMessage, e, null);
        final MessageBuilder builder = JDAUtils.constructMessage(parseSingle(exprNew, e, null));
        if (anyNull(message, builder)) {
            restart();
            return;
        }
        final MessageAction action;
        if (keepComponents)
            action = message.editMessage(builder.build()).setActionRows(message.getActionRows());
        else
            action = message.editMessage(builder.build());

        action.queue(this::restart, ex -> {
            DiSky.getErrorHandler().exception(e, ex);
            restart();
        });
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "edit " + exprMessage.toString(e, debug) + " with " + exprNew.toString(e, debug) + (keepComponents ? " and keep components" : "");
    }
}
