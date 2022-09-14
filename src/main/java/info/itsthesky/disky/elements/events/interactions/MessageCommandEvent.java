package info.itsthesky.disky.elements.events.interactions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.ExpressionType;
import info.itsthesky.disky.api.events.DiSkyEvent;
import info.itsthesky.disky.api.events.SimpleDiSkyEvent;
import info.itsthesky.disky.api.events.specific.InteractionEvent;
import info.itsthesky.disky.api.events.specific.ModalEvent;
import info.itsthesky.disky.api.skript.SimpleGetterExpression;
import info.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.*;
import net.dv8tion.jda.api.entities.channel.attribute.*;
import net.dv8tion.jda.api.entities.channel.middleman.*;
import net.dv8tion.jda.api.entities.channel.concrete.*;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.interactions.components.Modal;
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
				event -> event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getChannel().asTextChannel() : null);
		SkriptUtils.registerValue(BukkitMessageCommandEvent.class, NewsChannel.class,
				event -> event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getChannel().asNewsChannel() : null);
		SkriptUtils.registerValue(BukkitMessageCommandEvent.class, ThreadChannel.class,
				event -> event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getChannel().asThreadChannel() : null);

		SkriptUtils.registerValue(BukkitMessageCommandEvent.class, PrivateChannel.class,
				event -> !event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getChannel().asPrivateChannel() : null);
	}

	@Name("Target Message")
	@Description({"Represent the target message in a message command event.",
			"It basically represent the message that was clicked on."})
	@Examples({"target message"})
	public static class TargetMessage extends SimpleGetterExpression<Message, BukkitMessageCommandEvent> {

		static {
			Skript.registerExpression(
					TargetMessage.class,
					Message.class,
					ExpressionType.COMBINED,
					"[the] target message"
			);
		}

		@Override
		protected String getValue() {
			return "target message";
		}

		@Override
		protected Class<BukkitMessageCommandEvent> getEvent() {
			return BukkitMessageCommandEvent.class;
		}

		@Override
		protected Message convert(BukkitMessageCommandEvent event) {
			return event.getJDAEvent().getTarget();
		}

		@Override
		public @NotNull Class<? extends Message> getReturnType() {
			return Message.class;
		}
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