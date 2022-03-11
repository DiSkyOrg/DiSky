package info.itsthesky.disky.api.emojis;

import info.itsthesky.disky.DiSky;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * The class where you retrieve emojis by Unicode / shortcode as well as get the emoji list
 *
 * @see #getEmojis()
 * @see #ofUnicode(String)
 * @see #ofShortcode(String)
 */
public class Emojis {
	private static final EmojiStore store;
	private static final Set<Emoji> emojiView;

	static {
		try {
			store = EmojiStore.loadLocal();
			emojiView = Collections.unmodifiableSet(store.getEmojis());
		} catch (IOException e) {
			throw new RuntimeException("Unable to load emojis", e);
		}
	}

	public static Set<Emoji> getEmojis() {
		return emojiView;
	}

	/**
	 * Retrieves an {@link Emoji} with the matching Unicode such as <code>😂</code>
	 *
	 * @param unicode The Unicode emoji to find
	 * @return An {@link Emoji} with the matching unicode, or <code>null</code> if not found
	 */
	public static Emoji ofUnicode(String unicode) {
		return UnicodeHolder.unicodeMap.get(unicode);
	}

	/**
	 * Retrieves an {@link Emoji} with the matching shortcode such as <code>:joy:</code>
	 *
	 * @param shortcode The shortcode emoji to find
	 * @return An {@link Emoji} with the matching shortcode, or <code>null</code> if not found
	 */
	public static Emoji ofShortcode(String shortcode) {
		if (shortcode.charAt(0) == ':' && shortcode.charAt(shortcode.length() - 1) == ':') {
			return ShortcodeHolder.shortcodeMap.get(shortcode.substring(1, shortcode.length() - 1));
		} else {
			return ShortcodeHolder.shortcodeMap.get(shortcode);
		}
	}

	private static class UnicodeHolder { //Delay initialization, saves memory if not used
		private static final Map<String, Emoji> unicodeMap = new HashMap<>();

		static {
			for (Emoji emoji : store.getEmojis()) {
				final Emoji old = unicodeMap.put(emoji.unicode(), emoji);
				if (old != null) {
					DiSky.debug("Duplicate unicode: "+emoji.unicode()+" in https://emojipedia.org/"+old.subpage()+" and https://emojipedia.org/"+emoji.subpage()+", might not be grave");
				}
			}

			DiSky.debug("Loaded unicode map");
		}
	}

	private static class ShortcodeHolder { //Delay initialization, saves memory if not used
		private static final Map<String, Emoji> shortcodeMap = new HashMap<>();

		static {
			for (Emoji emoji : store.getEmojis()) {
				for (String shortcode : emoji.shortcodes()) {
					final Emoji old = shortcodeMap.put(shortcode, emoji);
					if (old != null) {
						DiSky.debug("Duplicate shortcode: "+shortcode+" in https://emojipedia.org/"+old.subpage()+" and https://emojipedia.org/"+emoji.subpage()+", might not be grave");
					}
				}
			}

			DiSky.debug("Loaded shortcode map");
		}
	}
}