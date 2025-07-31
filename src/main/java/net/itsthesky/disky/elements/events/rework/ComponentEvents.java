package net.itsthesky.disky.elements.events.rework;

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

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction;
import net.itsthesky.disky.api.events.rework.BuiltEvent;
import net.itsthesky.disky.api.events.rework.EventCategory;
import net.itsthesky.disky.api.events.rework.EventRegistryFactory;

import java.util.Objects;

@EventCategory(name = "Component Interaction Events", description = {
        "Events that are fired when a component is interacted with.",
        "This includes buttons, select menus, and modals.",
        "",
        "!!! info \"Check individual details to see if you are able to show a modal!\""
})
public class ComponentEvents {

    public static final BuiltEvent<ModalInteractionEvent> MODAL_INTERACTION_EVENT;

    static {

        EventRegistryFactory.builder(ButtonInteractionEvent.class)
                .name("Button Click")
                .description("Fired when any button sent by the button is clicked.",
                        "You can use the `clicked id` to get the clicked button id.",
                        "",
                        "!!! info \"Modal can be shown in this interaction!\"")
                .patterns("button click[ed]")
                .example("on button clicked:\n" +
                        "    reply with hidden \"You clicked the button with id '%clicked id%'!\" # This will defer the interaction!")

                .implementComponentInteraction(evt -> evt)
                .implementModal(ButtonInteraction::replyModal)

                .channelValues(ButtonInteractionEvent::getChannel)

                .value(String.class, ButtonInteractionEvent::getComponentId)
                .value(Button.class, ButtonInteractionEvent::getButton)
                .value(Guild.class, ButtonInteractionEvent::getGuild)
                .value(User.class, ButtonInteractionEvent::getUser)
                .value(Member.class, ButtonInteractionEvent::getMember)
                .value(Number.class, ButtonInteractionEvent::getMessageIdLong)
                .value(Message.class, ButtonInteractionEvent::getMessage)

                .singleExpression("click[ed] (id|button)", String.class,
                        ButtonInteraction::getComponentId)
                .register();

        MODAL_INTERACTION_EVENT = EventRegistryFactory.builder(ModalInteractionEvent.class)
                .name("Modal Received")
                .description("Fired when a modal has been sent to the bot from any user.",
                        "Use 'received modal' to get the modal id. Don't forget to either reply or defer the interaction.",
                        "",
                        "!!! warning \"Modal can NOT be shown in this interaction!\"")
                .patterns("modal (click[ed]|receive[d])")
                .example("on modal received:\n" +
                        "    reply with hidden \"You clicked the button with id '%received modal%'!\" # This will defer the interaction!")

                .implementInteraction(evt -> evt)

                .channelValues(ModalInteractionEvent::getChannel)

                .value(String.class, ModalInteractionEvent::getModalId)
                .value(Guild.class, ModalInteractionEvent::getGuild)
                .value(User.class, ModalInteractionEvent::getUser)
                .value(Member.class, ModalInteractionEvent::getMember)
                .value(Number.class, evt -> Objects.requireNonNull(evt.getMessage()).getIdLong())
                .value(Message.class, ModalInteractionEvent::getMessage)

                .singleExpression("receive[d] (id|modal)", String.class,
                        ModalInteractionEvent::getModalId)
                .register();
    }

}
