package info.itsthesky.disky.api.emojis;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class to convert unicode emojis to shortcodes and vice versa
 */
public class EmojiParser {
	private static final Pattern SHORTCODE_PATTERN = Pattern.compile(":(.+?|[+-]1):");

	/**
	 * Replaces shortcode emojis to their unicode equivalent
	 * <br>Example:
	 * <br><code>foo :joy: bar</code>
	 * <br>V
	 * <br><code>foo 😂 bar</code>
	 *
	 * @param str The string with shortcode emojis to replace
	 * @return A new string with the emojis shortcode replaced with their unicode
	 */
	public static String toUnicode(String str) {
		final StringBuilder sb = new StringBuilder(str.length());
		final Matcher matcher = SHORTCODE_PATTERN.matcher(str);

		int start = 0;
		while (matcher.find()) {
			final String shortcode = matcher.group(1);

			sb.append(str, start, matcher.start());

			final Emoji emoji = Emojis.ofShortcode(shortcode);
			if (emoji != null) {
				sb.append(emoji.unicode());
			} else {
				sb.append(matcher.group());
			}

			start = matcher.end();
		}

		sb.append(str, start, str.length());

		return sb.toString();
	}
}