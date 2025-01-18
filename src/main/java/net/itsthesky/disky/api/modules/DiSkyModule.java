package net.itsthesky.disky.api.modules;

import ch.njol.skript.SkriptAddon;
import net.itsthesky.disky.DiSky;
import org.skriptlang.skript.util.ClassLoader;

import java.io.File;
import java.net.URLClassLoader;

public abstract class DiSkyModule {

    private final DiSkyModuleInfo info;
    private final File moduleJar;
    private final ModuleOrigin origin;

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
     * @return The module jar file
     */
    public File getModuleJar() {
        return moduleJar;
    }

    /**
     * Get the origin for syntax registration, representing this module.
     * @return The origin
     */
    public ModuleOrigin getOrigin() {
        return origin;
    }

    /**
     * @return The URL class loader for this module
     */
    public URLClassLoader getLoader() {
        return loader;
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
