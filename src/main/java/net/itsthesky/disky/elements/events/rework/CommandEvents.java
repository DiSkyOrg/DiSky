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
import ch.njol.skript.lang.*;
import ch.njol.skript.sections.SecLoop;
import ch.njol.skript.sections.SecWhile;
import ch.njol.skript.util.AsyncEffect;
import ch.njol.util.Kleenean;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.itsthesky.disky.api.events.rework.BuiltEvent;
import net.itsthesky.disky.api.events.rework.EventCategory;
import net.itsthesky.disky.api.events.rework.EventRegistryFactory;
import net.itsthesky.disky.api.skript.EasyElement;
import net.itsthesky.disky.api.skript.SimpleGetterExpression;
import net.itsthesky.disky.core.JDAUtils;
import net.itsthesky.disky.elements.events.rework.custom.SlashCooldownEvent;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@EventCategory(name = "Interaction Command Events", description = {
        "These events are fired when a user interacts with a command:", "",
        "- Slash Command: Fired when a user executes a slash command. (+ includes an auto-complete event)",
        "- Message Command: Fired when a user interacts with a message command (right click on a message).",
        "- User Command: Fired when a user interacts with a user command (right click on a user).", "",
        "Refer to individual event documentation for more details.",
})
public class CommandEvents {

    public static final BuiltEvent<SlashCooldownEvent> SLASH_COOLDOWN_EVENT;
    public static final BuiltEvent<SlashCommandInteractionEvent> SLASH_COMMAND_EVENT;
    public static final BuiltEvent<MessageContextInteractionEvent> MESSAGE_COMMAND_EVENT;
    public static final BuiltEvent<UserContextInteractionEvent> USER_COMMAND_EVENT;
    public static final BuiltEvent<CommandAutoCompleteInteractionEvent> SLASH_COMPLETION_EVENT;

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



        // Slash Command Completion Event
        // Fired when a user triggers autocompletion in a slash command
        SLASH_COMPLETION_EVENT = EventRegistryFactory.builder(CommandAutoCompleteInteractionEvent.class)
                .name("Slash Command Completion Event")
                .patterns("slash completion [receive[d]]")
                .description("Fired when Discord requests argument autocompletion for a slash command.",
                        "Use 'event-string' to get the command name.",
                        "Use the 'return' effect to provide completion choices to the user.",
                        "You can access the focused argument with 'current argument' and other argument values with 'argument \"name\" as type'.")
                .example("on slash completion:\n\tif event-string is \"mycommand\":\n\t\tif current argument is \"option\":\n\t\t\treturn choice \"Option 1\" with value \"option1\", choice \"Option 2\" with value \"option2\"")
                .implementInteraction(evt -> evt)

                .channelValues(CommandAutoCompleteInteractionEvent::getChannel)
                .value(Guild.class, CommandAutoCompleteInteractionEvent::getGuild)
                .value(Member.class, CommandAutoCompleteInteractionEvent::getMember)
                .value(User.class, CommandAutoCompleteInteractionEvent::getUser)
                .value(String.class, CommandAutoCompleteInteractionEvent::getFullCommandName)

                .singleExpression("current( |-)arg[ument] [name]", String.class,
                        evt -> evt.getInteraction().getFocusedOption().getName())

                .register();

        // Register the Return effect for slash command completion
        Skript.registerEffect(
                ReturnCompletions.class,
                "return %slashchoices%"
        );
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
            throw new UnsupportedOperationException("This method should not be called.");
        }

        @Override
        public Class<Event>[] getCompatibleEvents() {
            return new Class[]{
                    SLASH_COMMAND_EVENT.getBukkitEventClass(),
                    SLASH_COOLDOWN_EVENT.getBukkitEventClass(),
                    SLASH_COMPLETION_EVENT.getBukkitEventClass()
            };
        }

        @Override
        protected Object convert(Event e) {
            final String name = EasyElement.parseSingle(exprName, e, null);
            if (name == null)
                return null;

            final OptionMapping option;
            final boolean isFromGuild;
            final @Nullable Guild guild;
            if (SLASH_COMMAND_EVENT.getBukkitEventClass().isAssignableFrom(e.getClass())) {
                option = SLASH_COMMAND_EVENT.getJDAEvent(e).getOption(name);
                isFromGuild = SLASH_COMMAND_EVENT.getJDAEvent(e).isFromGuild();
                guild = SLASH_COMMAND_EVENT.getJDAEvent(e).getGuild();
            } else if (SLASH_COMPLETION_EVENT.getBukkitEventClass().isAssignableFrom(e.getClass())) {
                option = SLASH_COMPLETION_EVENT.getJDAEvent(e).getOption(name);
                isFromGuild = SLASH_COMPLETION_EVENT.getJDAEvent(e).isFromGuild();
                guild = SLASH_COMPLETION_EVENT.getJDAEvent(e).getGuild();
            } else {
                option = null;
                isFromGuild = false;
                guild = null;
            }

            if (option == null)
                return null;

            if (isMember) {
                final User user = option.getAsUser();
                if (!isFromGuild) {
                    Skript.error("You cannot get a member from a private channel slash command.");
                    return null;
                }

                return Objects.requireNonNull(guild).getMember(user);
            } else {
                return JDAUtils.parseOptionValue(option);
            }
        }

        @Override
        public @NotNull Class<?> getReturnType() {
            return JDAUtils.getOptionClass(type);
        }
    }


    /**
     * Effect for returning autocompletion choices in slash command completion events
     */
    public static class ReturnCompletions extends Effect {
        private Expression<Command.Choice> exprChoices;

        @Override
        protected void execute(@NotNull Event e) {
            throw new UnsupportedOperationException();
        }

        @Override
        protected @Nullable TriggerItem walk(@NotNull Event e) {
            debug(e, false);
            final Command.Choice[] choices = EasyElement.parseList(exprChoices, e, new Command.Choice[0]);
            if (choices.length == 0)
                return null;

            CommandAutoCompleteInteractionEvent event = SLASH_COMPLETION_EVENT.getJDAEvent(e);
            if (event != null)
                event.replyChoices(choices).queue();

            // Exit from any loops or while sections
            TriggerSection parent = getParent();
            while (parent != null) {
                if (parent instanceof SecLoop) {
                    ((SecLoop) parent).exit(e);
                } else if (parent instanceof SecWhile) {
                    ((SecWhile) parent).exit(e);
                }
                parent = parent.getParent();
            }

            return null;
        }

        @Override
        public @NotNull String toString(@Nullable Event e, boolean debug) {
            return "return choices " + exprChoices.toString(e, debug);
        }

        @Override
        public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull SkriptParser.ParseResult parseResult) {
            if (!EasyElement.containsEvent(SLASH_COMPLETION_EVENT.getBukkitEventClass()))
                return false;
            exprChoices = (Expression<Command.Choice>) exprs[0];
            return true;
        }
    }


}
