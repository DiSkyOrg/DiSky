package info.itsthesky.disky.managers;

import de.leonhard.storage.Config;
import info.itsthesky.disky.DiSky;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class ConfigManager {
    private final DiSky instance;
    private final Config config;

    public ConfigManager(final DiSky instance) {
        this.instance = instance;

        /*
         * Configuration Loading
         */
        this.config = init("config.yml");
    }

    public Config getConfig() {
        return config;
    }

    private Config init(@NotNull final String fileName) {
        return new Config(new File(instance.getDataFolder(), fileName));
    }

}
