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

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.lang.TriggerSection;
import ch.njol.skript.sections.SecLoop;
import ch.njol.skript.sections.SecWhile;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.components.ComponentInteraction;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.itsthesky.disky.api.events.rework.BuiltEvent;
import net.itsthesky.disky.api.events.rework.EventRegistryFactory;
import net.itsthesky.disky.api.skript.EasyElement;
import net.itsthesky.disky.api.skript.MultipleGetterExpression;
import net.itsthesky.disky.api.skript.SimpleGetterExpression;
import net.itsthesky.disky.core.JDAUtils;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DropdownEvents {


    static {
        // String Dropdown Click Event
        // Fired when a user interacts with a string dropdown menu
        EventRegistryFactory.builder(StringSelectInteractionEvent.class)
                .name("String Dropdown Click Event")
                .patterns("drop[( |-)]down click[ed]")
                .description("Fired when a user selects one or more choices in a string dropdown menu.",
                        "This event provides access to the selected string values and dropdown details.",
                        "Don't forget to either reply to or defer the interaction to acknowledge it.",
                        "You can show a modal in response to this interaction.")
                .example("on dropdown clicked:\n\treply with \"You selected: %selected values%\"")
                .implementComponentInteraction(evt -> evt)
                .implementModal(StringSelectInteractionEvent::replyModal)
                
                .channelValues(StringSelectInteractionEvent::getChannel)
                .value(Message.class, StringSelectInteractionEvent::getMessage)
                .value(Guild.class, StringSelectInteractionEvent::getGuild)
                .value(Member.class, StringSelectInteractionEvent::getMember)
                .value(User.class, StringSelectInteractionEvent::getUser)
                .value(SelectMenu.Builder.class, evt -> evt.getComponent().createCopy())
                .value(String.class, evt -> evt.getComponent().getId())
                .value(ComponentInteraction.class, StringSelectInteractionEvent::getInteraction)

                .listExpression("select[ed] value[s]", String.class,
                        evt -> evt.getValues().toArray(new String[0]))

                .register();

        // Entity Dropdown Click Event
        // Fired when a user interacts with an entity dropdown menu
        EventRegistryFactory.builder(EntitySelectInteractionEvent.class)
                .name("Entity Dropdown Click Event")
                .patterns("entit(y|ies) drop[( |-)]down click[ed]")
                .description("Fired when a user selects one or more entities in an entity dropdown menu.",
                        "This event provides access to the selected entities (users, roles, channels, etc.).",
                        "Don't forget to either reply to or defer the interaction to acknowledge it.",
                        "You can show a modal in response to this interaction.")
                .example("on entity dropdown clicked:\n\tbroadcast \"User %event-user% selected entities: %selected entities%\"")
                .implementComponentInteraction(evt -> evt)
                .implementModal(EntitySelectInteractionEvent::replyModal)
                
                .channelValues(EntitySelectInteractionEvent::getChannel)
                .value(Message.class, EntitySelectInteractionEvent::getMessage)
                .value(Guild.class, EntitySelectInteractionEvent::getGuild)
                .value(Member.class, EntitySelectInteractionEvent::getMember)
                .value(User.class, EntitySelectInteractionEvent::getUser)
                .value(SelectMenu.Builder.class, evt -> evt.getComponent().createCopy())
                .value(String.class, evt -> evt.getComponent().getId())
                .value(ComponentInteraction.class, EntitySelectInteractionEvent::getInteraction)

                .listExpression("select[ed] entit(y|ies)", Object.class,
                        evt -> evt.getValues().toArray(new IMentionable[0]))

                .register();
    }

}