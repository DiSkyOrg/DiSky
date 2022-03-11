package info.itsthesky.disky.api.emojis;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import info.itsthesky.disky.DiSky;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class EmojiStore {
	private static final Gson GSON = new GsonBuilder()
			.setPrettyPrinting()
			.create();

	private final Set<Emoji> emojis = new HashSet<>();

	public static EmojiStore loadLocal() throws IOException {
		DiSky.debug("Loading local emojis");

		final EmojiStore store;
		final File file = new File(DiSky.getInstance().getDataFolder(), "emojis.json");
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
			store = GSON.fromJson(reader, EmojiStore.class);
		}

		DiSky.debug("Loaded "+store.getEmojis().size()+" local emojis");

		return store;
	}

	public Set<Emoji> getEmojis() {
		return emojis;
	}
}
