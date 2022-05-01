package info.itsthesky.disky.api.modules;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.util.coll.iterator.EnumerationIterable;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.api.DiSkyType;
import org.bukkit.configuration.InvalidConfigurationException;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public abstract class DiSkyModule {

    private final String name;
    private final String version;
    private final String author;
    private final File moduleJar;
    private final ModuleManager manager;
    private final List<ClassInfo<?>> registeredClasses = new ArrayList<>();

    private URLClassLoader loader;

    public DiSkyModule(String name, String author, String version, File moduleJar) {
        this.name = name;
        this.author = author;
        this.version = version;
        this.moduleJar = moduleJar;
        this.manager = DiSky.getModuleManager();
    }

    public abstract void init(final DiSky instance, final SkriptAddon addon);

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public String getVersion() {
        return version;
    }

    public File getModuleJar() {
        return moduleJar;
    }

    protected <T> void registerType(Class<T> clazz, String codeName, Function<T, String> toString) {
        final DiSkyType<T> type = new DiSkyType<>(clazz, codeName, toString, null);
        type.register();
        registeredClasses.add(type.getClassInfo());
    }

    protected <T extends Enum<T>> void registerType(Class<T> clazz, String codeName) {
        final DiSkyType<T> type = DiSkyType.fromEnum(clazz, codeName, codeName);
        type.register();
        registeredClasses.add(type.getClassInfo());
    }

    public List<ClassInfo<?>> getRegisteredClasses() {
        return registeredClasses;
    }

    public void reload() throws IOException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvalidConfigurationException {
        manager.reload(this);
    }

    public URLClassLoader getLoader() {
        return loader;
    }

    public DiSkyModule setLoader(URLClassLoader loader) {
        this.loader = loader;
        return this;
    }

    public void disable() {
        manager.disable(this);
    }

    protected void loadClasses(String basePackage, String... subPackages) throws IOException {
        assert subPackages != null;
        final JarFile jar = new JarFile(moduleJar);
        for (int i = 0; i < subPackages.length; i++)
            subPackages[i] = subPackages[i].replace('.', '/') + "/";
        basePackage = basePackage.replace('.', '/') + "/";
        try {
            for (final JarEntry e : new EnumerationIterable<>(jar.entries())) {
                if (e.getName().startsWith(basePackage) && e.getName().endsWith(".class")) {
                    boolean load = subPackages.length == 0;
                    for (final String sub : subPackages) {
                        if (e.getName().startsWith(sub, basePackage.length())) {
                            load = true;
                            break;
                        }
                    }
                    if (load) {
                        final String c = e.getName().replace('/', '.').substring(0, e.getName().length() - ".class".length());
                        try {
                            final URL[] urls = {new URL("jar:file:"+moduleJar.getAbsolutePath()+"!/")};
                            final URLClassLoader loader = new URLClassLoader(urls, this.getClass().getClassLoader());
                            final Class<?> clazz = Class.forName(c, true, loader);
                            clazz.getDeclaredMethod("load").invoke(null);
                        } catch (ClassNotFoundException ex) {
                            Skript.exception(ex, "Cannot load class " + c + " from " + this);
                        } catch (final ExceptionInInitializerError err) {
                            Skript.exception(err.getCause(), this + "'s class " + c + " generated an exception while loading");
                        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException ignored) {

                        }
                    }
                }
            }
        } finally {
            try {
                jar.close();
            } catch (final IOException ignored) {}
        }
    }

}
