package info.itsthesky.disky.managers;

import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.core.Bot;
import info.itsthesky.disky.managers.wrappers.MessageWrapper;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.TreeMap;
import java.util.WeakHashMap;

public class MessageManager extends ListenerAdapter {

	private static final WeakHashMap<String, MessageManager> loadedManagers = new WeakHashMap<>();

	public static @NotNull MessageManager getManager(JDA bot) {
		@Nullable MessageManager manager = loadedManagers.getOrDefault(bot.getSelfUser().getId(), null);
		if (manager == null)
			throw new NullPointerException("The bot " + bot.getSelfUser().getAsTag() + " is not loaded!");
		return manager;
	}

	// ##############################

	private final TreeMap<Long, MessageWrapper> deletedMessageCache = new TreeMap<>();
	private final TreeMap<Long, EditedMessageInfo> editedMessageCache = new TreeMap<>();

	public MessageManager(Bot bot) {
		loadedManagers.put(bot.getInstance().getSelfUser().getId(), this);
	}

	@Override
	public void onMessageReceived(@NotNull MessageReceivedEvent event) {
		insert(event.getMessage());
	}

	@Override
	public void onMessageUpdate(@NotNull MessageUpdateEvent event) {
		update(event.getMessage());
	}

	private void insert(Message message) {
		if (!message.isFromGuild())
			return;

		final long id = message.getIdLong();

		editedMessageCache.put(id, new EditedMessageInfo(message));
		deletedMessageCache.put(id, new MessageWrapper(message));

		DiSky.debug("Caching message with id " + id + ": " + deletedMessageCache.get(id));
	}

	private void update(Message message) {
		final EditedMessageInfo info = editedMessageCache.getOrDefault(message.getIdLong(), null);
		if (info == null)
			return;

		info.setPreviousContent(info.getCurrentContent());
		info.setCurrentContent(message.getContentRaw());

		editedMessageCache.put(message.getIdLong(), info);
		deletedMessageCache.put(message.getIdLong(), new MessageWrapper(message));
	}

	public @Nullable Message getDeletedMessage(long id) {
		if (!deletedMessageCache.containsKey(id))
			DiSky.debug("Message with id " + id + " is not in deleted cache.");
		return deletedMessageCache.getOrDefault(id, null);
	}

	public @Nullable EditedMessageInfo getEditedMessage(long id) {
		return editedMessageCache.getOrDefault(id, null);
	}

	public @Nullable String getEditedMessageOldContent(long id) {
		final @Nullable EditedMessageInfo info = getEditedMessage(id);
		return info == null ? null : info.getPreviousContent();
	}

	public @Nullable String getDeletedMessageContent(long id) {
		final @Nullable Message info = getDeletedMessage(id);
		return info == null ? null : info.getContentRaw();
	}

	public static class EditedMessageInfo {

		private String previousContent;
		private String currentContent;

		public EditedMessageInfo(Message original) {
			this.previousContent = original.getContentRaw();
			this.currentContent = original.getContentRaw();
		}

		public String getPreviousContent() {
			return previousContent;
		}

		public void setPreviousContent(String previousContent) {
			this.previousContent = previousContent;
		}

		public String getCurrentContent() {
			return currentContent;
		}

		public void setCurrentContent(String currentContent) {
			this.currentContent = currentContent;
		}
	}

}
