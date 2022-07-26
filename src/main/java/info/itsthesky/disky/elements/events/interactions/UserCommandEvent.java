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
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.interactions.components.Modal;
import net.dv8tion.jda.api.requests.restaction.interactions.ModalCallbackAction;
import org.jetbrains.annotations.NotNull;

public class UserCommandEvent extends DiSkyEvent<UserContextInteractionEvent> {

	static {
		register("User Command", UserCommandEvent.class, BukkitUserCommandEvent.class,
				"user command [receive[d]]")
				.description("Fired when someone click on an user application command.",
						"Use 'event-string' to get the command name. Don't forget to either reply to the interaction. Defer doesn't work here.",
						"Modal can be shown in this interaction.");

		SkriptUtils.registerBotValue(BukkitUserCommandEvent.class);

		SkriptUtils.registerValue(BukkitUserCommandEvent.class, Guild.class,
				event -> event.getJDAEvent().getGuild());
		SkriptUtils.registerValue(BukkitUserCommandEvent.class, Member.class,
				event -> event.getJDAEvent().getMember());
		SkriptUtils.registerValue(BukkitUserCommandEvent.class, User.class,
				event -> event.getJDAEvent().getUser());
		SkriptUtils.registerValue(BukkitUserCommandEvent.class, String.class,
				event -> event.getJDAEvent().getName());
		SkriptUtils.registerValue(BukkitUserCommandEvent.class, MessageChannel.class,
				event -> event.getJDAEvent().getMessageChannel());

		SkriptUtils.registerValue(BukkitUserCommandEvent.class, GuildChannel.class,
				event -> event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getGuildChannel() : null);
		SkriptUtils.registerValue(BukkitUserCommandEvent.class, TextChannel.class,
				event -> event.getJDAEvent().isFromGuild() ? ((MessageChannelUnion) event.getJDAEvent().getChannel()).asTextChannel() : null);
		SkriptUtils.registerValue(BukkitUserCommandEvent.class, NewsChannel.class,
				event -> event.getJDAEvent().isFromGuild() ? ((MessageChannelUnion) event.getJDAEvent().getChannel()).asNewsChannel() : null);
		SkriptUtils.registerValue(BukkitUserCommandEvent.class, ThreadChannel.class,
				event -> event.getJDAEvent().isFromGuild() ? ((MessageChannelUnion) event.getJDAEvent().getChannel()).asThreadChannel() : null);

		SkriptUtils.registerValue(BukkitUserCommandEvent.class, PrivateChannel.class,
				event -> !event.getJDAEvent().isFromGuild() ? ((MessageChannelUnion) event.getJDAEvent().getChannel()).asPrivateChannel() : null);
	}

	@Name("Target User")
	@Description({"Represent the target user in a user command event.",
			"It basically represent the user that was clicked on."})
	@Examples({"target user"})
	public static class TargetUser extends SimpleGetterExpression<User, BukkitUserCommandEvent> {

		static {
			Skript.registerExpression(
					TargetUser.class,
					User.class,
					ExpressionType.COMBINED,
					"[the] target user"
			);
		}

		@Override
		protected String getValue() {
			return "target user";
		}

		@Override
		protected Class<BukkitUserCommandEvent> getEvent() {
			return BukkitUserCommandEvent.class;
		}

		@Override
		protected User convert(BukkitUserCommandEvent event) {
			return event.getJDAEvent().getTarget();
		}

		@Override
		public @NotNull Class<? extends User> getReturnType() {
			return User.class;
		}
	}


	public static class BukkitUserCommandEvent extends SimpleDiSkyEvent<UserContextInteractionEvent> implements ModalEvent, InteractionEvent {
		public BukkitUserCommandEvent(UserCommandEvent event) {}

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