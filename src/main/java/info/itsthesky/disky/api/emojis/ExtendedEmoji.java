package info.itsthesky.disky.api.emojis;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class which provides optional, extended information about an {@link Emoji}
 */
public final class ExtendedEmoji {
	private static final String BASE_URL = "https://emojipedia.org/";
	private static final Pattern NAME_PATTERN = Pattern.compile("<h1><span class=\"emoji\">.*?</span> (.*)</h1>");

	private final Emoji emoji;
	private final String name;

	private ExtendedEmoji(Emoji emoji, String name) {
		this.emoji = emoji;
		this.name = name;
	}

	/**
	 * Finds extended info about this emoji
	 * <br><b>You should use {@link Emoji#retrieveExtendedInfo()} as to not block the thread for too long</b>s
	 *
	 * @param emoji The {@link Emoji} to get the info from
	 * @return The extended info of this emoji
	 * @throws IOException If the Emojipedia page couldn't be read
	 * @see Emoji#retrieveExtendedInfo()
	 */
	public static ExtendedEmoji of(Emoji emoji) throws IOException {
		final String body = HttpUtils.getPageBody(BASE_URL + emoji.subpage());

		final Matcher nameMatcher = NAME_PATTERN.matcher(body);
		if (!nameMatcher.find()) {
			throw new IllegalArgumentException("Name of the emoji " + emoji.unicode() + " aka " + emoji.shortcodes() + " not found");
		}

		return new ExtendedEmoji(emoji, nameMatcher.group(1));
	}

	/**
	 * Returns the {@link Emoji} this is from
	 *
	 * @return The original {@link Emoji}
	 */
	public Emoji getEmoji() {
		return emoji;
	}

	/**
	 * Returns the Emojipedia name of this emoji
	 *
	 * @return The name of the emoji
	 */
	public String getName() {
		return name;
	}
}