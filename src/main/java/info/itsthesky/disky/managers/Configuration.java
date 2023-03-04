package info.itsthesky.disky.managers;

import info.itsthesky.disky.DiSky;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.util.logging.Level;

public class Configuration extends YamlConfiguration {

	public Configuration() {
		super();
	}

	public <T> T getOrDefault(String key, T defaultValue) {
		return contains(key) ? (T) get(key) : defaultValue;
	}

	public <T> T getOrSetDefault(String key, T defaultValue) {
		if (!contains(key)) {
			set(key, defaultValue);
			DiSky.updateConfig();
		}
		return (T) get(key);
	}

	@NotNull
	public static Configuration loadConfiguration(@NotNull File file) {
		Validate.notNull(file, "File cannot be null");

		Configuration config = new Configuration();

		try {
			config.load(file);
		} catch (IOException | InvalidConfigurationException ex) {
			Bukkit.getLogger().log(Level.SEVERE, "Cannot load " + file, ex);
		}

		return config;
	}
}
