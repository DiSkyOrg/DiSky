package info.itsthesky.disky.elements.events.interactions;

import info.itsthesky.disky.api.events.DiSkyEvent;
import info.itsthesky.disky.api.events.SimpleDiSkyEvent;
import info.itsthesky.disky.api.events.specific.InteractionEvent;
import info.itsthesky.disky.api.events.specific.ModalEvent;
import info.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.interactions.components.text.Modal;
import net.dv8tion.jda.api.requests.restaction.interactions.ModalCallbackAction;
import org.jetbrains.annotations.NotNull;

public class MessageCommandEvent extends DiSkyEvent<MessageContextInteractionEvent> {

	static {
		register("Message Command", MessageCommandEvent.class, BukkitMessageCommandEvent.class,
				"message command [receive[d]]")
				.description("Fired when someone click on a message application command.",
						"Use 'event-string' to get the command name. Don't forget to either reply to the interaction. Defer doesn't work here.",
						"Modal can be shown in this interaction.");

		SkriptUtils.registerBotValue(BukkitMessageCommandEvent.class);

		SkriptUtils.registerValue(BukkitMessageCommandEvent.class, Guild.class,
				event -> event.getJDAEvent().getGuild());
		SkriptUtils.registerValue(BukkitMessageCommandEvent.class, Member.class,
				event -> event.getJDAEvent().getMember());
		SkriptUtils.registerValue(BukkitMessageCommandEvent.class, User.class,
				event -> event.getJDAEvent().getUser());
		SkriptUtils.registerValue(BukkitMessageCommandEvent.class, String.class,
				event -> event.getJDAEvent().getName());
		SkriptUtils.registerValue(BukkitMessageCommandEvent.class, MessageChannel.class,
				event -> event.getJDAEvent().getMessageChannel());
		SkriptUtils.registerValue(BukkitMessageCommandEvent.class, Message.class,
				event -> event.getJDAEvent().getTarget());

		SkriptUtils.registerValue(BukkitMessageCommandEvent.class, GuildChannel.class,
				event -> event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getGuildChannel() : null);
		SkriptUtils.registerValue(BukkitMessageCommandEvent.class, TextChannel.class,
				event -> event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getTextChannel() : null);
		SkriptUtils.registerValue(BukkitMessageCommandEvent.class, NewsChannel.class,
				event -> event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getNewsChannel() : null);
		SkriptUtils.registerValue(BukkitMessageCommandEvent.class, ThreadChannel.class,
				event -> event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getThreadChannel() : null);

		SkriptUtils.registerValue(BukkitMessageCommandEvent.class, PrivateChannel.class,
				event -> !event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getPrivateChannel() : null);
	}

	public static class BukkitMessageCommandEvent extends SimpleDiSkyEvent<MessageContextInteractionEvent> implements ModalEvent, InteractionEvent {
		public BukkitMessageCommandEvent(MessageCommandEvent event) {}

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