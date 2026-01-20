package net.itsthesky.disky.api.modules;

import lombok.Getter;
import net.itsthesky.disky.DiSky;
import org.skriptlang.skript.addon.SkriptAddon;
import org.skriptlang.skript.util.ClassLoader;

import java.io.File;
import java.net.URLClassLoader;

public abstract class DiSkyModule {

    private final DiSkyModuleInfo info;
    /**
     * -- GETTER --
     *
     * @return The module jar file
     */
    @Getter
    private final File moduleJar;
    /**
     * -- GETTER --
     *  Get the origin for syntax registration, representing this module.
     *
     * @return The origin
     */
    @Getter
    private final ModuleOrigin origin;

    /**
     * -- GETTER --
     *
     * @return The URL class loader for this module
     */
    @Getter
    private URLClassLoader loader;

    public DiSkyModule(DiSkyModuleInfo info, File moduleJar) {
        this.info = info;
        this.moduleJar = moduleJar;

        this.origin = new ModuleOrigin(this);
    }

    public abstract void init(final DiSky instance, final SkriptAddon addon);

    /**
     * Get the version of the module.
     * @return The version
     * @deprecated Use {@link DiSkyModuleInfo#version} instead
     */
    @Deprecated(forRemoval = true)
    public String getVersion() {
        return info.version.toString();
    }

    /**
     * Get the required minimum version of DiSky for this module.
     * @return The required minimum version
     * @deprecated Use {@link DiSkyModuleInfo#name} ()} instead
     */
    @Deprecated(forRemoval = true)
    public String getName() {
        return info.name;
    }

    /**
     * Get the author of the module.
     * @return The author
     * @deprecated Use {@link DiSkyModuleInfo#author} instead
     */
    @Deprecated(forRemoval = true)
    public String getAuthor() {
        return info.author;
    }

    /**
     * Get further information about the module.
     * @return The module info
     */
    public DiSkyModuleInfo getModuleInfo() {
        return info;
    }

    /**
     * Shutdown the module. This should be used to clean up any resources
     * that the module may have created.
     */
    public void shutdown() {

    }

    /**
     * Change the loader of this module. This should only be called when
     * the module is being instantiated for the first time; otherwise, it
     * may cause issues with class loading.
     * @param loader The new loader
     * @return The module, for chaining
     */
    public DiSkyModule setLoader(URLClassLoader loader) {
        this.loader = loader;
        return this;
    }

    /**
     * Load classes from the given base package and sub-packages.
     * @param basePackage The base package to load from
     * @param subPackages The sub-packages to load from
     */
    protected void loadClasses(String basePackage, String... subPackages) {
        ClassLoader.builder()
                .basePackage(basePackage)
                .addSubPackages(subPackages)
                .initialize(true)
                .deep(true)
                .build()
                .loadClasses(getClass(), getModuleJar());
    }

}
