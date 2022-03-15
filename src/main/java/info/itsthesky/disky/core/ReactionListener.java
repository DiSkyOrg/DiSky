package info.itsthesky.disky.core;

import ch.njol.skript.lang.Trigger;
import info.itsthesky.disky.api.emojis.Emote;
import info.itsthesky.disky.elements.events.react.ReactionAddEvent;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class ReactionListener extends ListenerAdapter {

	public static final HashMap<Long, ReactionInfo> waiters = new HashMap<>();

	@Override
	public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
		final ReactionInfo info = waiters.getOrDefault(event.getMessageIdLong(), null);
		if (info == null)
			return;

		final Emote reactedEmote = Emote.fromReaction(event.getReactionEmote());
		if (!reactedEmote.isSimilar(info.getEmote()))
			return;
		if (!info.canUserExecute(event.getUserIdLong()))
			return;
		if (info.getSelfID().equals(event.getUserIdLong())) // It's the actual bot
			return;

		final ReactionAddEvent.BukkitReactionAddEvent e = new ReactionAddEvent.BukkitReactionAddEvent(new ReactionAddEvent());
		e.setJDAEvent(event);
		if (info.isAlreadyFired())
			waiters.remove(event.getMessageIdLong());
		else
			info.execute(e);
	}

	public static class ReactionInfo {

		private boolean alreadyFired;
		private final boolean oneTime;
		private final Emote emote;
		private final Long messageId;
		private final @NotNull Long selfID;
		private final @Nullable Long userId;
		private final @Nullable Trigger trigger;

		public ReactionInfo(boolean oneTime, Emote emote, Long messageId, @NotNull Long selfID, @Nullable User user, @Nullable Trigger trigger) {
			this.alreadyFired = false;
			this.oneTime = oneTime;
			this.emote = emote;
			this.messageId = messageId;
			this.selfID = selfID;
			this.userId = user == null ? null : user.getIdLong();
			this.trigger = trigger;
		}

		public Emote getEmote() {
			return emote;
		}

		public Long getMessageId() {
			return messageId;
		}

		public void setAlreadyFired(boolean alreadyFired) {
			this.alreadyFired = alreadyFired;
		}

		public boolean isAlreadyFired() {
			return alreadyFired;
		}

		public boolean isOneTime() {
			return oneTime;
		}

		public @Nullable Long getUserId() {
			return userId;
		}

		public @NotNull Long getSelfID() {
			return selfID;
		}

		public void execute(Event event) {
			if (isOneTime())
				setAlreadyFired(true);
			if (getTrigger() != null)
				getTrigger().execute(event);
		}

		public @Nullable Trigger getTrigger() {
			return trigger;
		}

		public boolean canUserExecute(long userId) {
			if (getUserId() == null)
				return true;
			return getUserId() == userId;
		}
	}
}
