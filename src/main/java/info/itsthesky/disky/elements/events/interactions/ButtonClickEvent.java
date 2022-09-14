package info.itsthesky.disky.elements.events.interactions;

import info.itsthesky.disky.api.events.DiSkyEvent;
import info.itsthesky.disky.api.events.SimpleDiSkyEvent;
import info.itsthesky.disky.api.events.specific.InteractionEvent;
import info.itsthesky.disky.api.events.specific.ModalEvent;
import info.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.*;
import net.dv8tion.jda.api.entities.channel.attribute.*;
import net.dv8tion.jda.api.entities.channel.middleman.*;
import net.dv8tion.jda.api.entities.channel.concrete.*;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.GenericMessageEvent;
import net.dv8tion.jda.api.interactions.components.Component;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.Modal;
import net.dv8tion.jda.api.requests.restaction.interactions.ModalCallbackAction;
import org.jetbrains.annotations.NotNull;

public class ButtonClickEvent extends DiSkyEvent<ButtonInteractionEvent> {

	static {
		register("Button Click", ButtonClickEvent.class, BukkitButtonClickEvent.class,
				"button click[ed]")
				.description("Fired when any button sent by the button is clicked.",
						"Use 'event-button' to get the button id. Don't forget to either reply or defer the interaction.",
						"Modal can be shown in this interaction.");

		SkriptUtils.registerBotValue(BukkitButtonClickEvent.class);

		SkriptUtils.registerValue(BukkitButtonClickEvent.class, Message.class,
				event -> event.getJDAEvent().getMessage());
		SkriptUtils.registerValue(BukkitButtonClickEvent.class, Guild.class,
				event -> event.getJDAEvent().getGuild());
		SkriptUtils.registerValue(BukkitButtonClickEvent.class, Member.class,
				event -> event.getJDAEvent().getMember());
		SkriptUtils.registerValue(BukkitButtonClickEvent.class, User.class,
				event -> event.getJDAEvent().getUser());
		SkriptUtils.registerValue(BukkitButtonClickEvent.class, String.class,
				event -> event.getJDAEvent().getButton().getId());
		SkriptUtils.registerValue(BukkitButtonClickEvent.class, Button.class,
				event -> event.getJDAEvent().getButton());
		SkriptUtils.registerValue(BukkitButtonClickEvent.class, MessageChannel.class,
				event -> event.getJDAEvent().getChannel());

		SkriptUtils.registerValue(BukkitButtonClickEvent.class, GuildChannel.class,
				event -> event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getGuildChannel() : null);
		SkriptUtils.registerValue(BukkitButtonClickEvent.class, TextChannel.class,
				event -> event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getChannel().asTextChannel() : null);
		SkriptUtils.registerValue(BukkitButtonClickEvent.class, NewsChannel.class,
				event -> event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getChannel().asNewsChannel() : null);
		SkriptUtils.registerValue(BukkitButtonClickEvent.class, ThreadChannel.class,
				event -> event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getChannel().asThreadChannel() : null);

		SkriptUtils.registerValue(BukkitButtonClickEvent.class, PrivateChannel.class,
				event -> !event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getChannel().asPrivateChannel() : null);
	}

	public static class BukkitButtonClickEvent extends SimpleDiSkyEvent<ButtonInteractionEvent> implements ModalEvent, InteractionEvent {
		public BukkitButtonClickEvent(ButtonClickEvent event) {}

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