package info.itsthesky.disky.managers;

import info.itsthesky.disky.DiSky;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Manager class for the configuration file of DiSky.
 * @author Sky
 */
public final class ConfigManager {

    private static final String CONFIG_FILE = "config.yml";
    private static YamlConfiguration CONFIG;

    public static void loadConfig(DiSky instance) {
        final File localConfig = new File(instance.getDataFolder(), CONFIG_FILE);
        if (!localConfig.exists())
            instance.saveResource(CONFIG_FILE, false);

        final YamlConfiguration localYaml = YamlConfiguration.loadConfiguration(localConfig);
        final YamlConfiguration defaultYaml = YamlConfiguration.loadConfiguration(new InputStreamReader(instance.getResource(CONFIG_FILE)));

        final int localVersion = localYaml.getInt("version", 1);
        final int currentVersion = defaultYaml.getInt("version", 1);

        if (localVersion < currentVersion) {
            instance.getLogger().info("Your configuration file is outdated! (v" + localVersion + " -> v" + currentVersion + ")");
            updateConfig(instance, localYaml);
            instance.getLogger().info("Configuration file updated!");
        }

        CONFIG = localYaml;
    }

    private static void updateConfig(DiSky instance, YamlConfiguration local) {
        // 1. We copy the new config to override the old one in the files.
        instance.saveResource(CONFIG_FILE, true);
        final YamlConfiguration newConfig = YamlConfiguration.loadConfiguration(new File(instance.getDataFolder(), CONFIG_FILE));

        // 2. We reload the local config to get the new one.
        for (String key : newConfig.getKeys(true)) {
            if (!local.contains(key)) {
                local.set(key, newConfig.get(key));
            }
        }

        // 3. We save the local config to save the new one.
        try {
            local.save(new File(instance.getDataFolder(), CONFIG_FILE));
            instance.getLogger().info("Configuration file updated successfully!");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static <T> T get(String path, T def) {
        return (T) CONFIG.get(path, def);
    }

    public static List<Integer> getIgnoredCodes() {
        return CONFIG.getIntegerList("ignored-codes");
    }

}
