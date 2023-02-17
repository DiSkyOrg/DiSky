package info.itsthesky.disky.managers.config;

import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.managers.Configuration;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Holds all the configuration of the bot from the <code>config.yml</code> file.
 */
public final class Config {

	public static List<Integer> getIgnoredCodes() {
		final Configuration configuration = DiSky.getConfiguration();
		if (configuration.contains("ignored-core")) {
			DiSky.getInstance().getLogger().warning("The 'ignored-core' option is deprecated and will be removed in the next version. Please use 'ignored-codes' instead.");
			return configuration.getStringList("ignored-core").stream()
					.map(Integer::parseInt)
					.collect(Collectors.toList());
		}
		return configuration.getStringList("ignored-codes").stream()
				.map(Integer::parseInt)
				.collect(Collectors.toList());
	}

	public static boolean isDebug() {
		return DiSky.getConfiguration().getBoolean("debug");
	}

	public static boolean shouldFixSkriptOnlineStatus() {
		return DiSky.getConfiguration().getBoolean("fix-skript-online-status");
	}

	public static boolean shouldRegisterTokenExpressionOfBot() {
		return DiSky.getConfiguration().getBoolean("token-of-bot-expression");
	}

}
