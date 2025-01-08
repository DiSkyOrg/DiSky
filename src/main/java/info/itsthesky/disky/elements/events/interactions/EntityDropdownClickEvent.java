package info.itsthesky.disky.elements.events.interactions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.ExpressionType;
import info.itsthesky.disky.api.events.DiSkyEvent;
import info.itsthesky.disky.api.events.SimpleDiSkyEvent;
import info.itsthesky.disky.api.events.specific.ComponentInteractionEvent;
import info.itsthesky.disky.api.events.specific.ModalEvent;
import info.itsthesky.disky.api.skript.MultipleGetterExpression;
import info.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.NewsChannel;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ComponentInteraction;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.requests.restaction.interactions.ModalCallbackAction;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

public class EntityDropdownClickEvent extends DiSkyEvent<EntitySelectInteractionEvent> {

	static {
		register("Entity Dropdown Click", EntityDropdownClickEvent.class, BukkitDropdownClickEvent.class,
				"entit(y|ies) drop[( |-)]down click[ed]")
				.description("Fired when an user select one or more choice in an entity dropdown.",
						"Use 'event-dropdown' to get the dropdown id. Don't forget to either reply or defer the interaction.",
						"Use 'selected entities' to get the selected entities.",
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

	@Name("Selected Entities")
	@Description("The list of the selected entities, in the current entity dropdown update event.")
	@Examples("selected entities")
	public static class SelectedValues extends MultipleGetterExpression<Object, BukkitDropdownClickEvent> {

		static {
			Skript.registerExpression(
					SelectedValues.class,
					Object.class,
					ExpressionType.SIMPLE,
					"select[ed] entit(y|ies)"
			);
		}

		@Override
		protected String getValue() {
			return "selected entities";
		}

		@Override
		protected Class<? extends Event> getEvent() {
			return BukkitDropdownClickEvent.class;
		}

		@Override
		protected Object[] gets(BukkitDropdownClickEvent event) {
			return event.getJDAEvent().getValues().toArray(new IMentionable[0]);
		}

		@Override
		public @NotNull Class<?> getReturnType() {
			return Object.class;
		}
	}

	public static class BukkitDropdownClickEvent extends SimpleDiSkyEvent<EntitySelectInteractionEvent> implements ModalEvent, ComponentInteractionEvent {
		public BukkitDropdownClickEvent(EntityDropdownClickEvent event) {}

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