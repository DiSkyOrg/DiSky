package info.itsthesky.disky.elements.events.interactions;

import info.itsthesky.disky.api.events.DiSkyEvent;
import info.itsthesky.disky.api.events.SimpleDiSkyEvent;
import info.itsthesky.disky.api.events.specific.InteractionEvent;
import info.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;

public class ModalSendEvent extends DiSkyEvent<ModalInteractionEvent> {

	static {
		register("Modal Receive", ModalSendEvent.class, BukkitModalSendEvent.class,
				"modal (click[ed]|receive[d])")
				.description("Fired when a modal has been sent to the bot from any user.",
						"Use 'event-string' to get the modal id. Don't forget to either reply or defer the interaction.",
						"Modal can NOT be shown in this interaction.");

		SkriptUtils.registerBotValue(BukkitModalSendEvent.class);

		SkriptUtils.registerValue(BukkitModalSendEvent.class, Guild.class,
				event -> event.getJDAEvent().getGuild());
		SkriptUtils.registerValue(BukkitModalSendEvent.class, Member.class,
				event -> event.getJDAEvent().getMember());
		SkriptUtils.registerValue(BukkitModalSendEvent.class, User.class,
				event -> event.getJDAEvent().getUser());
		SkriptUtils.registerValue(BukkitModalSendEvent.class, String.class,
				event -> event.getJDAEvent().getModalId());
		SkriptUtils.registerValue(BukkitModalSendEvent.class, MessageChannel.class,
				event -> event.getJDAEvent().getMessageChannel());

		SkriptUtils.registerValue(BukkitModalSendEvent.class, GuildChannel.class,
				event -> event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getGuildChannel() : null);
		SkriptUtils.registerValue(BukkitModalSendEvent.class, TextChannel.class,
				event -> event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getTextChannel() : null);
		SkriptUtils.registerValue(BukkitModalSendEvent.class, NewsChannel.class,
				event -> event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getNewsChannel() : null);
		SkriptUtils.registerValue(BukkitModalSendEvent.class, ThreadChannel.class,
				event -> event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getThreadChannel() : null);

		SkriptUtils.registerValue(BukkitModalSendEvent.class, PrivateChannel.class,
				event -> !event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getPrivateChannel() : null);
	}

	public static class BukkitModalSendEvent extends SimpleDiSkyEvent<ModalInteractionEvent> implements InteractionEvent {
		public BukkitModalSendEvent(ModalSendEvent event) {}

		@Override
		public GenericInteractionCreateEvent getInteractionEvent() {
			return getJDAEvent();
		}

	}
}