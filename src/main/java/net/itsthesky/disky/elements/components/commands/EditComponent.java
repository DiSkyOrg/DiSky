package net.itsthesky.disky.elements.components.commands;

import ch.njol.skript.Skript;
import net.itsthesky.disky.api.DiSkyRegistry;
import ch.njol.skript.config.Node;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.util.AsyncEffect;
import ch.njol.util.Kleenean;
import net.itsthesky.disky.elements.sections.handler.DiSkyRuntimeHandler;
import net.itsthesky.disky.api.events.specific.ComponentInteractionEvent;
import net.itsthesky.disky.api.skript.EasyElement;
import net.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.components.Component;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction;
import net.dv8tion.jda.api.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenuInteraction;
import net.dv8tion.jda.api.requests.RestAction;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EditComponent extends AsyncEffect {

    static {
        Skript.registerEffect(
                EditComponent.class,
                "edit [component] (button|dropdown|select[( |-)]menu) [of [the] (interaction|event)] to [show] %button/dropdown%"
        );
    }

    private Node node;
    private Expression<Object> exprComponent;

    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.@NotNull ParseResult parseResult) {
        if (!EasyElement.containsInterfaces(ComponentInteractionEvent.class)) {
            Skript.error("The edit component effect can only be used in a component interaction event!");
            return false;
        }

        node = getParser().getNode();
        exprComponent = (Expression<Object>) expressions[0];

        return true;
    }

    @Override
    protected void execute(@NotNull Event event) {
        final Object component = exprComponent.getSingle(event);
        if (component == null)
            return;

        final ComponentInteractionEvent componentEvent = (ComponentInteractionEvent) event;
        RestAction<?> action;

        if (componentEvent.getComponentInteraction().getComponentType().equals(Component.Type.BUTTON)) {

            // Buttons
            if (!(component instanceof Button)) {
                SkriptUtils.error(node, "You're trying to edit a button interaction, but the provided component is a dropdown!");
                return;
            }

            final Button button = (Button) component;
            final ButtonInteraction buttonInteraction = (ButtonInteraction) componentEvent.getComponentInteraction();
            action = buttonInteraction.editButton(button);

        } else if (componentEvent.getComponentInteraction().getComponentType().name().contains("SELECT")) {

            // Dropdowns
            if (!(component instanceof SelectMenu.Builder)) {
                SkriptUtils.error(node, "You're trying to edit a dropdown interaction, but the provided component is a button!");
                return;
            }

            final SelectMenu.Builder dropdown = (SelectMenu.Builder) component;
            final SelectMenuInteraction dropdownInteraction = (SelectMenuInteraction) componentEvent.getComponentInteraction();
            action = dropdownInteraction.editSelectMenu(dropdown.build());

        } else {
            SkriptUtils.error(node, "The component type of the interaction is not a button or dropdown!");
            return;
        }

        try {
            action.complete();
        } catch (Exception ex) {
            DiSkyRuntimeHandler.error(ex, node);
        }
    }

    @Override
    public @NotNull String toString(@Nullable Event event, boolean debug) {
        return "edit the component " + exprComponent.toString(event, debug);
    }

}
