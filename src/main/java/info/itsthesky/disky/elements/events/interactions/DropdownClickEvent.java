package info.itsthesky.disky.elements.events.interactions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.ExpressionType;
import info.itsthesky.disky.api.events.DiSkyEvent;
import info.itsthesky.disky.api.events.SimpleDiSkyEvent;
import info.itsthesky.disky.api.events.specific.InteractionEvent;
import info.itsthesky.disky.api.events.specific.ModalEvent;
import info.itsthesky.disky.api.skript.MultipleGetterExpression;
import info.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.components.Modal;
import net.dv8tion.jda.api.requests.restaction.interactions.ModalCallbackAction;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

public class DropdownClickEvent extends DiSkyEvent<SelectMenuInteractionEvent> {

	static {
		register("Dropdown Click", DropdownClickEvent.class, BukkitDropdownClickEvent.class,
				"drop[( |-)]down click[ed]")
				.description("Fired when an user select one or more choice in a dropdown.",
						"Use 'event-dropdown' to get the dropdown id. Don't forget to either reply or defer the interaction.",
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

		SkriptUtils.registerValue(BukkitDropdownClickEvent.class, GuildChannel.class,
				event -> event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getGuildChannel() : null);
		SkriptUtils.registerValue(BukkitDropdownClickEvent.class, TextChannel.class,
				event -> event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getTextChannel() : null);
		SkriptUtils.registerValue(BukkitDropdownClickEvent.class, NewsChannel.class,
				event -> event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getNewsChannel() : null);
		SkriptUtils.registerValue(BukkitDropdownClickEvent.class, ThreadChannel.class,
				event -> event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getThreadChannel() : null);

		SkriptUtils.registerValue(BukkitDropdownClickEvent.class, PrivateChannel.class,
				event -> !event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getPrivateChannel() : null);
	}

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

	public static class BukkitDropdownClickEvent extends SimpleDiSkyEvent<SelectMenuInteractionEvent> implements ModalEvent, InteractionEvent {
		public BukkitDropdownClickEvent(DropdownClickEvent event) {}

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