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
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.itsthesky.disky.api.events.rework.BuiltEvent;
import net.itsthesky.disky.api.events.rework.EventRegistryFactory;
import net.itsthesky.disky.api.skript.EasyElement;
import net.itsthesky.disky.api.skript.SimpleGetterExpression;
import net.itsthesky.disky.core.JDAUtils;
import net.itsthesky.disky.elements.events.rework.custom.SlashCooldownEvent;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class CommandEvents {

    public static final BuiltEvent<SlashCooldownEvent> SLASH_COOLDOWN_EVENT;
    public static final BuiltEvent<SlashCommandInteractionEvent> SLASH_COMMAND_EVENT;
    public static final BuiltEvent<MessageContextInteractionEvent> MESSAGE_COMMAND_EVENT;
    public static final BuiltEvent<UserContextInteractionEvent> USER_COMMAND_EVENT;

    static {

        SLASH_COMMAND_EVENT = EventRegistryFactory.builder(SlashCommandInteractionEvent.class)
                .name("Slash Command")
                .patterns("slash command [receive[d]]")
                .description("Fired when a user execute a specific slash command.",
                        "Use 'used command' to get the command name. Don't forget to either reply or defer the interaction, You can only defer using the wait pattern  e.g: 'defer the interaction and wait [silently].",
                        "You can get value of arguments using 'argument \"name\" as string' for example.")

                .implementModal(SlashCommandInteractionEvent::replyModal)
                .implementInteraction(evt -> evt)

                .channelValues(SlashCommandInteractionEvent::getChannel)
                .value(String.class, SlashCommandInteractionEvent::getFullCommandName)

                .value(User.class, SlashCommandInteractionEvent::getUser)
                .value(Guild.class, SlashCommandInteractionEvent::getGuild)
                .value(Member.class, SlashCommandInteractionEvent::getMember)

                .singleExpression("(execute|use)[d] [slash] command", String.class,
                        SlashCommandInteractionEvent::getFullCommandName)

                .register();

        SLASH_COOLDOWN_EVENT = EventRegistryFactory.builder(SlashCooldownEvent.class)
                .noRegistration()

                .implementModal(SlashCommandInteractionEvent::replyModal)
                .implementInteraction(evt -> evt)

                .channelValues(SlashCommandInteractionEvent::getChannel)
                .value(String.class, SlashCommandInteractionEvent::getFullCommandName)

                .value(User.class, SlashCommandInteractionEvent::getUser)
                .value(Guild.class, SlashCommandInteractionEvent::getGuild)
                .value(Member.class, SlashCommandInteractionEvent::getMember)

                .cancellable(SlashCooldownEvent::isCancelled,
                        SlashCooldownEvent::setCancelled)

                .register();

        MESSAGE_COMMAND_EVENT = EventRegistryFactory.builder(MessageContextInteractionEvent.class)
                .name("Message Command")
                .patterns("message command [receive[d]]")
                .description("Fired when someone click on a message application command.",
                        "Use `used command` to get the command name and `target message` for the targeted message. Don't forget to either reply to the interaction. Defer doesn't work here.")

                .implementModal(MessageContextInteractionEvent::replyModal)
                .implementInteraction(evt -> evt)

                .channelValues(MessageContextInteractionEvent::getChannel)
                .value(String.class, MessageContextInteractionEvent::getName)

                .value(User.class, MessageContextInteractionEvent::getUser)
                .value(Guild.class, MessageContextInteractionEvent::getGuild)
                .value(Member.class, MessageContextInteractionEvent::getMember)
                .value(Message.class, MessageContextInteractionEvent::getTarget)

                .singleExpression("(execute|use)[d] [message] command", String.class,
                        MessageContextInteractionEvent::getFullCommandName)
                .singleExpression("[the] target message", Message.class,
                        MessageContextInteractionEvent::getTarget)

                .register();

        USER_COMMAND_EVENT = EventRegistryFactory.builder(UserContextInteractionEvent.class)
                .name("User Command")
                .patterns("user command [receive[d]]")
                .description("Fired when someone click on a user application command.",
                        "Use `used command` to get the command name and `target user` for the targeted user. Don't forget to either reply to the interaction. Defer doesn't work here.")

                .implementModal(UserContextInteractionEvent::replyModal)
                .implementInteraction(evt -> evt)

                .channelValues(UserContextInteractionEvent::getChannel)
                .value(String.class, UserContextInteractionEvent::getName)

                .value(User.class, UserContextInteractionEvent::getUser)
                .value(Guild.class, UserContextInteractionEvent::getGuild)
                .value(Member.class, UserContextInteractionEvent::getMember)

                .singleExpression("(execute|use)[d] [user] command", String.class,
                        UserContextInteractionEvent::getFullCommandName)
                .singleExpression("[the] target user", User.class,
                        UserContextInteractionEvent::getTarget)

                .register();

    }

    @Name("Slash Command Argument")
    @Description({"Represents a slash command argument.",
            "The name is the ID used when defining the slash command.",
            "Specify the type, so that Skript can parse it correctly. (if it's a number, operation wil be allowed for example)",
            "The type should be the same used when defining the argument in the command."})
    @Examples({"# I'm doing /ban time:30 user:*user id*, so:",
            "set {_time} to argument \"time\" as integer",
            "set {_user} to argument \"user\" as user"})
    public static class ArgValue extends SimpleGetterExpression<Object, Event> {

        static {
            Skript.registerExpression(
                    ArgValue.class,
                    Object.class,
                    ExpressionType.COMBINED,
                    "[the] arg[ument] [(named|with name)] %string% as (%-optiontype%|:member)"
            );
        }

        private Expression<String> exprName;
        private OptionType type;
        private boolean isMember;

        @Override
        @SuppressWarnings("ALL")
        public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull SkriptParser.ParseResult parseResult) {
            if (!super.init(exprs, matchedPattern, isDelayed, parseResult))
                return false;

            exprName = (Expression<String>) exprs[0];
            isMember = parseResult.hasTag("member");

            if (!isMember) {
                type = ((Expression<OptionType>) exprs[1]).getSingle(null);
                if (type == null) {
                    Skript.error("You must provide a literal (= constant) value for the option type.");
                    return false;
                }
            } else {
                type = OptionType.USER;
            }
            return true;
        }

        @Override
        protected String getValue() {
            return "argument " + exprName.toString(null, false);
        }

        @Override
        protected Class<Event> getEvent() {
            return (Class<Event>) SLASH_COOLDOWN_EVENT.getBukkitEventClass();
        }

        @Override
        protected Object convert(Event e) {
            final String name = EasyElement.parseSingle(exprName, e, null);
            if (name == null)
                return null;

            final var evt = SLASH_COOLDOWN_EVENT.getJDAEvent(e);
            if (evt == null) {
                Skript.error("You cannot use this expression outside of a slash command event.");
                return null;
            }

            final OptionMapping option = evt.getOption(name);
            if (option == null)
                return null;

            if (isMember) {
                final User user = option.getAsUser();
                if (!evt.isFromGuild()) {
                    Skript.error("You cannot get a member from a private channel slash command.");
                    return null;
                }

                return Objects.requireNonNull(evt.getGuild()).getMember(user);
            } else {
                return JDAUtils.parseOptionValue(option);
            }
        }

        @Override
        public @NotNull Class<?> getReturnType() {
            return JDAUtils.getOptionClass(type);
        }
    }

}
