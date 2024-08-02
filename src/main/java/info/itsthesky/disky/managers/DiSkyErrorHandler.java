package info.itsthesky.disky.managers;

import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.api.skript.ErrorHandler;
import info.itsthesky.disky.core.SkriptUtils;
import info.itsthesky.disky.core.Utils;
import info.itsthesky.disky.elements.events.DiSkyErrorEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.requests.ErrorResponse;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

public class DiSkyErrorHandler implements ErrorHandler {
	
	private final HashMap<ErrorResponse, Function<Throwable, String[]>> errors = new HashMap<>();
	private final HashMap<Event, Throwable> errorsValue = new HashMap<>();
	private final Function<Throwable, String[]> def;
	private final HashMap<ErrorResponse, String> friendlyMessages = new HashMap<>();

	public DiSkyErrorHandler() {
		def = ex -> {
			final List<String> msg = new ArrayList<>(Arrays.asList(ex.getMessage().split("\n")));
			msg.add("");
			msg.add("&4Full Stacktrace:");
			msg.add("");
			for (StackTraceElement line : ex.getStackTrace())
				msg.add("" + line.toString());
			msg.add("");
			msg.add("&6&lREAD THE ERROR CAREFULLY&r&e, and if you are sure about it,");
			msg.add("&eyou can report the error with the whole stacktrace on the GitHub repository:");
			msg.add("&ehttps://github.com/DiSkyOrg/DiSky/issues/new");
			msg.add("");
			return msg.toArray(new String[0]);
		};
		errors.put(ErrorResponse.INVALID_FORM_BODY, ex -> {
			final List<String> msg = new ArrayList<>();
			final List<String> oldMsg = new ArrayList<>(Arrays.asList(ex.getMessage().split("\n")));
			oldMsg.remove(0);

			msg.add("Invalid component configuration: ");
			msg.add("");

			String previous = "";
			for (String m : oldMsg) {
				if (previous.contains("-") && !m.contains("-"))
					msg.add("");
				msg.add(m);
				previous = m;
			}

			return msg.toArray(new String[0]);
		});

		friendlyMessages.put(ErrorResponse.UNKNOWN_USER, "The specified user does not exist or is not cached.");
		friendlyMessages.put(ErrorResponse.UNKNOWN_CHANNEL, "The specified channel does not exist or is not cached.");
		friendlyMessages.put(ErrorResponse.UNKNOWN_GUILD, "The specified guild does not exist or is not cached.");
		friendlyMessages.put(ErrorResponse.UNKNOWN_MEMBER, "The specified member does not exist or is not cached.");
		friendlyMessages.put(ErrorResponse.UNKNOWN_ROLE, "The specified role does not exist or is not cached.");
		friendlyMessages.put(ErrorResponse.UNKNOWN_EMOJI, "The specified emoji does not exist or is not cached.");
		friendlyMessages.put(ErrorResponse.UNKNOWN_WEBHOOK, "The specified webhook does not exist or is not cached.");
		friendlyMessages.put(ErrorResponse.UNKNOWN_BAN, "The specified ban does not exist or is not cached.");
		friendlyMessages.put(ErrorResponse.UNKNOWN_INTEGRATION, "The specified integration does not exist or is not cached.");
		friendlyMessages.put(ErrorResponse.UNKNOWN_INVITE, "The specified invite does not exist or is not cached.");
		friendlyMessages.put(ErrorResponse.UNKNOWN_MESSAGE, "The specified message does not exist or is not cached.");
	}
	
	@Override
	public void exception(@Nullable Event event, @Nullable Throwable ex) {
		insertErrorValue(event, ex);
		send("&4[&c!&4] &c");
		send("&4[&c!&4] &4DiSky Internal Error (version: "+ DiSky.getInstance().getDescription().getVersion()+")");
		if (ex != null)
			send("&4[&c!&4] &4Error type: &c" + ex.getClass().getSimpleName());
		send("&4[&c!&4] &c");
		final String[] lines;
		if (ex == null)
			ex = new RuntimeException("Unknown exception (nullable message): " + ex);
		if (ex instanceof ErrorResponseException)
			lines = formatResponseError((ErrorResponseException) ex);
		else
			lines = def.apply(ex);

		if (event != null) {
			@Nullable Throwable finalEx = ex;
			SkriptUtils.sync(() -> Bukkit.getPluginManager().callEvent(new DiSkyErrorEvent.BukkitDiSkyErrorEvent(finalEx, event.getEventName())));
		}

		if (lines == null) {
			QUEUED_MESSAGES.clear();
			return;
		}

		for (String line : lines)
			send("&4[&c!&4] &c" + line);

		send("&4[&c!&4] &c");
		sendAll();
	}

	@Override
	public void insertErrorValue(@Nullable Event event, @Nullable Throwable error) {
		if (event != null)
			errorsValue.put(event, error);
	}

	@Override
	public @Nullable Throwable getErrorValue(@NotNull Event event) {
		final Throwable value = errorsValue.getOrDefault(event, null);
		errorsValue.remove(event);
		return value;
	}

	public String[] formatResponseError(ErrorResponseException ex) {
		if (ConfigManager.getIgnoredCodes().contains(ex.getErrorCode()))
			return null;

		if (!errors.containsKey(ex.getErrorResponse()))
			return def.apply(ex);

		return errors.get(ex.getErrorResponse()).apply(ex);
	}

	private final List<String> QUEUED_MESSAGES = new ArrayList<>();
	private void send(String message) {
		QUEUED_MESSAGES.add(Utils.colored(message));
	}

	private void sendAll() {
		Bukkit.getConsoleSender().sendMessage(QUEUED_MESSAGES.toArray(new String[0]));
		QUEUED_MESSAGES.clear();
	}
}
