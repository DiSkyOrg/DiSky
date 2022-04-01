package info.itsthesky.disky.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.api.skript.EasyElement;
import info.itsthesky.disky.api.skript.WaiterEffect;
import info.itsthesky.disky.core.JDAUtils;
import info.itsthesky.disky.elements.components.core.ComponentRow;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EditMessage extends WaiterEffect {

    static {
        Skript.registerEffect(
                EditMessage.class,
                "edit [the] [message] %message% (with|to show) %string/embedbuilder/messagebuilder% [with [the] (component|action)[s] [row] %-rows%] [(1¦[and] keep component[s])]"
        );
    }

    private Expression<Message> exprMessage;
    private Expression<Object> exprNew;
    private Expression<ComponentRow> exprComponents;
    private boolean keepComponents;

    @Override
    public boolean initEffect(Expression[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        exprMessage = (Expression<Message>) expressions[0];
        exprNew = (Expression<Object>) expressions[1];
        exprComponents = (Expression<ComponentRow>) expressions[2];
        keepComponents = parseResult.mark == 1;
        return true;
    }

    @Override
    public void runEffect(Event e) {
        final Message message = parseSingle(exprMessage, e, null);
        final MessageBuilder builder = JDAUtils.constructMessage(parseSingle(exprNew, e, null));

        final List<ComponentRow> rows = Arrays.asList(parseList(exprComponents, e, new ComponentRow[0]));
        final List<ActionRow> formatted = rows
                .stream()
                .map(ComponentRow::asActionRow)
                .collect(Collectors.toList());

        if (anyNull(message, builder)) {
            restart();
            return;
        }
        final MessageAction action;
        if (keepComponents)
            action = message.editMessage(builder.build()).setActionRows(message.getActionRows());
        else
            action = message.editMessage(builder.build()).setActionRows(formatted);

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
