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
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction;
import net.itsthesky.disky.api.events.rework.EventRegistryFactory;

public class InteractionEvents {

    static {

        EventRegistryFactory.builder(ButtonInteractionEvent.class)
                .name("Button Click")
                .description("Fired when any button sent by the button is clicked.",
                        "You can use the `clicked id` to get the clicked button id.",
                        "",
                        "!!! info \"Modal can be shown in this interaction!\"")
                .patterns("button click[ed]")
                .example("on button clicked:\n" +
                        "\treply with hidden \"You clicked the button with id '%clicked id%'!\" # This will defer the interaction!")

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

    }

}
