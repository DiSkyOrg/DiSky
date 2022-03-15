package info.itsthesky.disky.managers;

import ch.njol.skript.registrations.EventValues;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.core.Bot;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.WeakHashMap;

public class MessageManager extends ListenerAdapter {

	private static final WeakHashMap<String, MessageManager> loadedManagers = new WeakHashMap<>();
	private final WeakHashMap<Long, MessageInfo> deletedMessageCache = new WeakHashMap<>();
	private final WeakHashMap<Long, EditedMessageInfo> editedMessageCache = new WeakHashMap<>();

	public static @NotNull MessageManager getManager(JDA jda) {
		return getManager(DiSky.getManager().fromJDA(jda));
	}

	public static @NotNull MessageManager getManager(Bot bot) {
		@Nullable MessageManager manager = loadedManagers.getOrDefault(bot.getName(), null);
		if (manager == null) {
			manager = new MessageManager();
			bot.getInstance().addEventListener(manager);
			loadedManagers.put(bot.getName(), manager);
		}
		return manager;
	}

	@Override
	public void onMessageReceived(@NotNull MessageReceivedEvent event) {
		insert(event.getMessage(), false);
		insert(event.getMessage(), true);
	}

	@Override
	public void onMessageUpdate(@NotNull MessageUpdateEvent event) {
		update(event.getMessage());
	}

	private void insert(Message message, boolean edited) {
		final long id = message.getIdLong();
		if (edited) {
			editedMessageCache.put(id, new EditedMessageInfo(message));
		} else {
			deletedMessageCache.put(id, new MessageInfo(message));
		}
		// "Bonjour" -> "Bye"
	}

	private void update(Message message) {
		final EditedMessageInfo info = editedMessageCache.getOrDefault(message.getIdLong(), null);
		if (info == null)
			return;
		info.setPreviousContent(info.getCurrentContent());
		info.setCurrentContent(message.getContentRaw());
		editedMessageCache.put(message.getIdLong(), info);
	}

	public @Nullable MessageInfo getDeletedMessage(long id) {
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
		final @Nullable MessageInfo info = getDeletedMessage(id);
		return info == null ? null : info.getCurrentContent();
	}

	public static class MessageInfo {

		private final long messageId;
		private final long authorId;
		private String currentContent;

		public MessageInfo(Message original) {
			this(original.getIdLong(), original.getAuthor().getIdLong(), original.getContentRaw());
		}

		public MessageInfo(long messageId, long authorId, String currentContent) {
			this.messageId = messageId;
			this.authorId = authorId;
			this.currentContent = currentContent;
		}

		public long getMessageId() {
			return messageId;
		}

		public long getAuthorId() {
			return authorId;
		}

		public String getCurrentContent() {
			return currentContent;
		}

		public void setCurrentContent(String currentContent) {
			this.currentContent = currentContent;
		}
	}

	public static class EditedMessageInfo extends MessageInfo {

		private String previousContent;

		public EditedMessageInfo(Message original) {
			super(original);
			this.previousContent = original.getContentRaw();
		}

		public EditedMessageInfo(long messageId, long authorId, String currentContent) {
			super(messageId, authorId, currentContent);
			this.previousContent = currentContent;
		}

		public String getPreviousContent() {
			return previousContent;
		}

		public void setPreviousContent(String previousContent) {
			this.previousContent = previousContent;
		}
	}

}
