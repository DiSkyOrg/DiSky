package net.itsthesky.disky.api.modules;

import ch.njol.skript.util.Version;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents information about a <code>module.yml</code> file of a
 * DiSky module. This is usually deserialized from the module's JAR file
 * and shouldn't be manipulated/instantiated in any other way.
 *
 * @since 4.21.0
 * @see DiSkyModule
 */
public class DiSkyModuleInfo {

    /**
     * The main class of the module.
     */
    public final @NotNull String mainClass;

    /**
     * The name of the module.
     */
    public final @NotNull String name;

    /**
     * The author of this module.
     */
    public final @NotNull String author;

    /**
     * The version of this module.
     */
    public final @NotNull Version version;

    /**
     * The minimal DiSky version required to run this module.
     * If no version is specified in the module.yml, it will default
     * to 4.21.0 as it's the first version to support the new module system.
     */
    public final @NotNull Version requiredMinVersion;

    private DiSkyModuleInfo(@NotNull String mainClass,
                            @NotNull String name,
                            @NotNull String author,
                            @NotNull Version version,
                            @Nullable Version requiredMinVersion) {
        this.mainClass = mainClass;
        this.name = name;
        this.author = author;
        this.version = version;

        // We default to the oldest DiSky version that
        // supports the new module system, which is 4.21.0
        this.requiredMinVersion = requiredMinVersion == null
                ? new Version(4, 21, 0) : requiredMinVersion;
    }

    /**
     * Create a new instance of {@link DiSkyModuleInfo} from a given
     * {@link YamlConfiguration} object.
     * @param configuration The configuration to deserialize
     * @return The deserialized module info
     */
    public static DiSkyModuleInfo fromYaml(YamlConfiguration configuration) {
        if (!configuration.contains("main"))
            return null; // old version, unsupported

        return new DiSkyModuleInfo(
                configuration.getString("main"),
                configuration.getString("name"),
                configuration.getString("author"),
                new Version(configuration.getString("version")),
                configuration.contains("required-version") ? new Version(configuration.getString("required-version")) : null
        );
    }
}
