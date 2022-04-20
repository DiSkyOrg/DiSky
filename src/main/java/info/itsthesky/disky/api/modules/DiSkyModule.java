package info.itsthesky.disky.api.modules;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import ch.njol.util.coll.iterator.EnumerationIterable;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.api.DiSkyType;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.function.Function;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public abstract class DiSkyModule {

    private final String name;
    private final String version;
    private final String author;
    private final File moduleJar;

    public DiSkyModule(String name, String author, String version, File moduleJar) {
        this.name = name;
        this.author = author;
        this.version = version;
        this.moduleJar = moduleJar;
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

    protected <T> void registerType(Class<T> clazz, String codeName, Function<T, String> toString) {
        new DiSkyType<>(clazz, codeName, toString, null).register();
    }

    protected <T extends Enum<T>> void registerType(Class<T> clazz, String codeName) {
        DiSkyType.fromEnum(clazz, codeName, codeName).register();
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
                            Class.forName(c, true, loader);
                        } catch (final ClassNotFoundException ex) {
                            Skript.exception(ex, "Cannot load class " + c + " from " + this);
                        } catch (final ExceptionInInitializerError err) {
                            Skript.exception(err.getCause(), this + "'s class " + c + " generated an exception while loading");
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
