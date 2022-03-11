package info.itsthesky.disky.managers;

import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.api.skript.ErrorHandler;
import info.itsthesky.disky.core.Utils;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.requests.ErrorResponse;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

public class DiSkyErrorHandler implements ErrorHandler {
	
	private final HashMap<ErrorResponse, Function<Throwable, String[]>> errors = new HashMap<>();
	private final Function<Throwable, String[]> def;

	public DiSkyErrorHandler() {
		def = ex -> {
			final List<String> msg = new ArrayList<>(Arrays.asList(ex.getMessage().split("\n")));
			msg.add("");
			msg.add("&4Full Stacktrace:");
			msg.add("");
			for (StackTraceElement line : ex.getStackTrace())
				msg.add("" + line.toString());
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
	}
	
	@Override
	public void exception(Throwable ex) {
		send("&4[&c!&4] &c");
		send("&4[&c!&4] &4DiSky Internal Error (version: "+ DiSky.getInstance().getDescription().getVersion()+")");
		send("&4[&c!&4] &c");
		final String[] lines;
		if (ex instanceof ErrorResponseException)
			lines = errors.getOrDefault(((ErrorResponseException) ex).getErrorResponse(), def).apply(ex);
		else
			lines = def.apply(ex);

		for (String line : lines)
			send("&4[&c!&4] &c" + line);

		send("&4[&c!&4] &c");
	}
	
	private void send(String message) {
		Bukkit.getServer().getConsoleSender().sendMessage(Utils.colored(message));
	}
}
