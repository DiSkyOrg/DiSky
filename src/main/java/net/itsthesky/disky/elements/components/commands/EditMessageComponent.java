package net.itsthesky.disky.elements.components.commands;

import ch.njol.skript.Skript;
import ch.njol.skript.config.Node;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.AsyncEffect;
import ch.njol.util.Kleenean;
import net.dv8tion.jda.api.components.ActionComponent;
import net.dv8tion.jda.api.components.Component;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.actionrow.ActionRowChildComponent;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.selections.SelectMenu;
import net.dv8tion.jda.api.entities.Message;
import net.itsthesky.disky.DiSky;
import net.itsthesky.disky.core.SkriptUtils;
import net.itsthesky.disky.elements.sections.handler.DiSkyRuntimeHandler;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class EditMessageComponent extends AsyncEffect {

    static {
        Skript.registerEffect(
                EditMessageComponent.class,
                "edit [message] (component|button|dropdown|select[( |-)]menu) with [the] id %string% (of|from|in) [the] [message] %message% (to [show]|with) %button/dropdown%"
        );
    }

    private Node node;
    private Expression<String> exprID;
    private Expression<Message> exprMessage;
    private Expression<Object> exprComponent;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        node = getParser().getNode();
        exprID = (Expression<String>) expressions[0];
        exprMessage = (Expression<Message>) expressions[1];
        exprComponent = (Expression<Object>) expressions[2];
        return true;
    }

    @Override
    protected void execute(Event event) {
        final String id = exprID.getSingle(event);
        final Message message = exprMessage.getSingle(event);
        final Object component = exprComponent.getSingle(event);
        if (!DiSkyRuntimeHandler.checkSet(node, exprID, id, exprMessage, message, exprComponent, component))
            return;

        // First gather components rows
        final List<ActionRow> rows = message.getActionRows();
        final List<ActionRow> newRows = new ArrayList<>();

        // Then iterate over each row and edit the comp if found
        for (final ActionRow current : rows) {
            final List<ActionRowChildComponent> components = new ArrayList<>();
            for (ActionRowChildComponent actionComponent : current.getComponents()) {
                ActionRowChildComponent componentToAdd = actionComponent;
                final var componentId = actionComponent instanceof final ActionComponent comp ? comp.getCustomId() : null;

                if (id.equalsIgnoreCase(componentId)) {
                    if (actionComponent.getType().equals(Component.Type.BUTTON) && component instanceof Button)
                        componentToAdd = (Button) component;
                    else if (actionComponent.getType().name().contains("SELECT") && component instanceof SelectMenu.Builder)
                        componentToAdd = ((SelectMenu.Builder) component).build();
                    else {
                        SkriptUtils.error(node, "The provided component type doesn't match with the current component type! (ID: " + id + ")");
                        return;
                    }
                }
                components.add(componentToAdd);
            }

            newRows.add(ActionRow.of(components));
        }

        //action = componentEvent.getComponentInteraction().editComponents(newRows);
        try {
            message.editMessageComponents(newRows).complete();
        } catch (Exception ex) {
            DiSky.getErrorHandler().exception(event, ex);
        }
    }

    @Override
    public @NotNull String toString(@Nullable Event event, boolean debug) {
        return "edit message components with id " + exprID.toString(event, debug) + " of message " + exprMessage.toString(event, debug) + " to show " + exprComponent.toString(event, debug);
    }
}
