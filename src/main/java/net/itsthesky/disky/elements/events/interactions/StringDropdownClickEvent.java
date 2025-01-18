package net.itsthesky.disky.elements.events.interactions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.ExpressionType;
import net.itsthesky.disky.api.events.DiSkyEvent;
import net.itsthesky.disky.api.events.SimpleDiSkyEvent;
import net.itsthesky.disky.api.events.specific.ComponentInteractionEvent;
import net.itsthesky.disky.api.events.specific.ModalEvent;
import net.itsthesky.disky.api.skript.MultipleGetterExpression;
import net.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.NewsChannel;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ComponentInteraction;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.requests.restaction.interactions.ModalCallbackAction;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

public class StringDropdownClickEvent extends DiSkyEvent<StringSelectInteractionEvent> {

	static {
		register("String Dropdown Click", StringDropdownClickEvent.class, BukkitDropdownClickEvent.class,
				"drop[( |-)]down click[ed]")
				.description("Fired when an user select one or more choice in a string dropdown.",
						"Use 'event-dropdown' to get the dropdown id. Don't forget to either reply or defer the interaction.",
						"Use 'selected values' to get the selected string values.",
						"Modal can be shown in this interaction.");

		SkriptUtils.registerBotValue(BukkitDropdownClickEvent.class);

		SkriptUtils.registerValue(BukkitDropdownClickEvent.class, Message.class,
				event -> event.getJDAEvent().getMessage());
		SkriptUtils.registerValue(BukkitDropdownClickEvent.class, Guild.class,
				event -> event.getJDAEvent().getGuild());
		SkriptUtils.registerValue(BukkitDropdownClickEvent.class, Member.class,
				event -> event.getJDAEvent().getMember());
		SkriptUtils.registerValue(BukkitDropdownClickEvent.class, User.class,
				event -> event.getJDAEvent().getUser());
		SkriptUtils.registerValue(BukkitDropdownClickEvent.class, SelectMenu.Builder.class,
				event -> event.getJDAEvent().getComponent().createCopy());
		SkriptUtils.registerValue(BukkitDropdownClickEvent.class, String.class,
				event -> event.getJDAEvent().getComponent().getId());
		SkriptUtils.registerValue(BukkitDropdownClickEvent.class, MessageChannel.class,
				event -> event.getJDAEvent().getChannel());
		SkriptUtils.registerValue(BukkitDropdownClickEvent.class, ComponentInteraction.class,
				event -> event.getJDAEvent().getInteraction());

		SkriptUtils.registerValue(BukkitDropdownClickEvent.class, GuildChannel.class,
				event -> event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getGuildChannel() : null);
		SkriptUtils.registerValue(BukkitDropdownClickEvent.class, TextChannel.class,
				event -> event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getChannel().asTextChannel() : null);
		SkriptUtils.registerValue(BukkitDropdownClickEvent.class, NewsChannel.class,
				event -> event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getChannel().asNewsChannel() : null);
		SkriptUtils.registerValue(BukkitDropdownClickEvent.class, ThreadChannel.class,
				event -> event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getChannel().asThreadChannel() : null);

		SkriptUtils.registerValue(BukkitDropdownClickEvent.class, PrivateChannel.class,
				event -> !event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getChannel().asPrivateChannel() : null);
	}

	@Name("Selected Values")
	@Description("The list of the selected values' IDs, in the current dropdown update event.")
	@Examples("selected values")
	public static class SelectedValues extends MultipleGetterExpression<String, BukkitDropdownClickEvent> {

		static {
			Skript.registerExpression(
					SelectedValues.class,
					String.class,
					ExpressionType.SIMPLE,
					"select[ed] value[s]"
			);
		}

		@Override
		protected String getValue() {
			return "selected values";
		}

		@Override
		protected Class<? extends Event> getEvent() {
			return BukkitDropdownClickEvent.class;
		}

		@Override
		protected String[] gets(BukkitDropdownClickEvent event) {
			return event.getJDAEvent().getValues().toArray(new String[0]);
		}

		@Override
		public @NotNull Class<? extends String> getReturnType() {
			return String.class;
		}
	}

	public static class BukkitDropdownClickEvent extends SimpleDiSkyEvent<StringSelectInteractionEvent> implements ModalEvent, ComponentInteractionEvent {
		public BukkitDropdownClickEvent(StringDropdownClickEvent event) {}

		@Override
		public GenericInteractionCreateEvent getInteractionEvent() {
			return getJDAEvent();
		}

		@Override
		public ModalCallbackAction replyModal(@NotNull Modal modal) {
			return getJDAEvent().replyModal(modal);
		}
	}
}