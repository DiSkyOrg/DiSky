package net.itsthesky.disky.elements.componentsv2.skript.replacement;

/*
 * DiSky
 * Copyright (C) 2025 ItsTheSky
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

import ch.njol.skript.Skript;
import net.itsthesky.disky.api.DiSkyRegistry;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.*;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import lombok.Setter;
import net.dv8tion.jda.api.components.Component;
import net.dv8tion.jda.api.components.MessageTopLevelComponent;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.interactions.callbacks.IMessageEditCallback;
import net.dv8tion.jda.api.requests.ErrorResponse;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.itsthesky.disky.DiSky;
import net.itsthesky.disky.api.events.specific.ComponentInteractionEvent;
import net.itsthesky.disky.core.SkriptUtils;
import net.itsthesky.disky.elements.components.core.ComponentRow;
import net.itsthesky.disky.elements.componentsv2.ComponentV2Factory;
import net.itsthesky.disky.elements.componentsv2.base.*;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class SecApplyReplacement extends Section {

    static {
        Skript.registerSection(
                SecApplyReplacement.class,
                "apply component[s] [the] replacement[s] (to|for) [the] [message] %message%"
        );
    }

    private Expression<Message> exprMessage;
    private Trigger trigger;

    @Setter
    private INewComponentBuilder<?> nextNewComponent;
    @Setter
    private boolean hasChanged = false;

    public static Component doReplacement(@NotNull Component component,
                                          @NotNull Function<INewComponentBuilder<?>, ComponentReplacerResult> replacer) {

        if (component instanceof Container container) {
            final var containerBuilder = (ContainerBuilder) ComponentV2Factory.recreateComponent(container);
            final var result = replacer.apply(containerBuilder);
            if (!result.hasChanged())
                return containerBuilder.buildWithId();

            final var replacedBuilder = (ContainerBuilder) result.newComponent();

            if (containerBuilder != replacedBuilder)
                return replacedBuilder.buildWithId();

            replacedBuilder.getComponents().clear();

            for (final Component child : container.getComponents()) {
                final var replacedChild = doReplacement(child, replacer);
                if (replacedChild != null) {
                    final var childBuilder = ComponentV2Factory.recreateComponent(replacedChild);
                    replacedBuilder.addContent(childBuilder == null
                            ? null
                            : (IContainerComponentBuilder<?>) childBuilder);
                }
            }

            return replacedBuilder.buildWithId();
        }

        if (component instanceof net.dv8tion.jda.api.components.section.Section section) {
            final var sectionBuilder = (SectionBuilder) ComponentV2Factory.recreateComponent(section);
            final var result = replacer.apply(sectionBuilder);
            if (!result.hasChanged())
                return sectionBuilder.buildWithId();

            final var replacedBuilder = (SectionBuilder) result.newComponent();

            if (sectionBuilder != replacedBuilder)
                return replacedBuilder.buildWithId();

            replacedBuilder.getComponents().clear();

            for (final Component child : section.getContentComponents()) {
                final var replacedChild = doReplacement(child, replacer);
                if (replacedChild != null) {
                    final var childBuilder = ComponentV2Factory.recreateComponent(replacedChild);
                    replacedBuilder.addContent(childBuilder == null
                            ? null
                            : (ISectionComponentBuilder<?>) childBuilder);
                }
            }

            final var accessoryBuilder = (ISectionAccessoryBuilder<?>) ComponentV2Factory.recreateComponent(section.getAccessory());
            if (accessoryBuilder != null) {
                final var accessoryResult = replacer.apply(accessoryBuilder);
                if (!accessoryResult.hasChanged())
                    return replacedBuilder.buildWithId();

                final var replacedAccessory = accessoryResult.newComponent();
                if (replacedAccessory == null)
                    return null;

                if (replacedAccessory instanceof final ComponentRow componentRow)
                    replacedBuilder.setAccessoryComponent((ISectionAccessoryBuilder<?>) INewComponentBuilder.of(componentRow.getSingleButton()));
                else
                    replacedBuilder.setAccessoryComponent((ISectionAccessoryBuilder<?>) replacedAccessory);
            }

            if (replacedBuilder.getAccessoryComponent() == null)
                return null;

            return replacedBuilder.buildWithId();
        }

        final var componentBuilder = ComponentV2Factory.recreateComponent(component);
        final var result = replacer.apply(componentBuilder);
        if (!result.hasChanged())
            return componentBuilder.buildWithId();

        final var replacedBuilder = (INewComponentBuilder<?>) result.newComponent();
        if (componentBuilder != replacedBuilder)
            return replacedBuilder.buildWithId();

        return null;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult, SectionNode sectionNode, List<TriggerItem> triggerItems) {
        exprMessage = (Expression<Message>) expressions[0];
        trigger = SkriptUtils.loadCode(sectionNode, this, "apply replacement",
                parser -> parser.setCurrentSections(List.of(this)), null,
                EvtMsgReplacement.class);
        return true;
    }

    @Override
    protected @Nullable TriggerItem walk(Event event) {
        final var message = exprMessage.getSingle(event);
        if (message == null)
            return getNext();

        final Function<INewComponentBuilder<?>, ComponentReplacerResult> replacer = builder -> {

            final var evt = new EvtMsgReplacement(builder.getUniqueId(), builder, null);
            final var vars = Variables.copyLocalVariables(event);
            if (vars != null)
                Variables.setLocalVariables(evt, vars);
            nextNewComponent = builder; // if new is the same, we don't change; If it's null or
            hasChanged = false; // we don't change the next component
            TriggerItem.walk(trigger, evt);

            return new ComponentReplacerResult(nextNewComponent, hasChanged);
        };

        final var replacedTree = new ArrayList<MessageTopLevelComponent>();

        for (final var topLevelComp : message.getComponents()) {
            final var replaced = doReplacement(topLevelComp, replacer);
            // we give a top level comp, so we can safely cast it
            if (replaced == null) {
                DiSky.debug("Component " + topLevelComp.getClass().getSimpleName() + " was replaced to null, skipping.");
                continue;
            }

            replacedTree.add((MessageTopLevelComponent) replaced);
        }

        SkriptUtils.async(() -> {
            RestAction<?> action = message.editMessageComponents(replacedTree).useComponentsV2();
            DiSky.debug("Got event " + event.getClass().getSimpleName() + " with interfaces:");
            for (final var iface : event.getClass().getInterfaces()) {
                DiSky.debug(" - " + iface.getSimpleName());
            }

            if (event instanceof final ComponentInteractionEvent interactionEvent) {
                final var interaction = interactionEvent.getInteractionEvent().getInteraction();
                DiSky.debug("Editing via interaction, components for interaction: " + interaction.getClass().getSimpleName());
                if (interaction instanceof final IMessageEditCallback messageEditCallback) {
                    DiSky.debug("Editing via interaction, components for interaction: " + interaction.getId());
                    action = messageEditCallback.editComponents(replacedTree)
                            .useComponentsV2();
                } else {
                    DiSky.debug("Editing component via normal mesages :)");
                }
            }

            try {
                action.complete();
            } catch (ErrorResponseException e) {
                if (e.getErrorResponse() == ErrorResponse.INVALID_FORM_BODY) {
                    // this mean something was not built correctly; we can trace back the path to
                    // debug the right component!
                    final var responseDataObject = e.getResponse().getObject();
                    final var data = responseDataObject.getObject("errors").getObject("data");

                    for (final var key : data.keys())
                        DiSky.debug("Error in component: " + key + " - " + data.getObject(key));
//
//                    DiSky.debug("Sent component structure: " + "["
//                            + replacedTree.stream()
//                            .map(Component::toData)
//                            .map(DataObject::toString)
//                            .reduce((a, b) -> a + ", " + b)
//                            .orElse("[]") + "]");
                }

                throw new RuntimeException("Failed to apply replacement to message: " + message.getId(), e);
            }
            SkriptUtils.sync(() -> TriggerItem.walk(getNext(), event));
        });
        return null;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "apply the replacement for the message " + exprMessage.toString(event, debug);
    }

    public record ComponentReplacerResult(
            @NotNull INewComponentBuilder<?> newComponent,
            boolean hasChanged
    ) {
    }
}
