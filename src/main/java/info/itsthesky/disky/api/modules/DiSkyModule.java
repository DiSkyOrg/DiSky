package info.itsthesky.disky.api.modules;

import ch.njol.skript.SkriptAddon;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.api.DiSkyType;
import info.itsthesky.disky.api.events.SimpleDiSkyEvent;
import info.itsthesky.disky.core.Bot;
import info.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.JDA;
import org.bukkit.event.Event;
import org.skriptlang.skript.util.ClassLoader;

import java.io.File;
import java.net.URLClassLoader;
import java.util.function.Function;

public abstract class DiSkyModule {

    private final String name;
    private final String version;
    private final String author;
    private final File moduleJar;
    private final ModuleManager manager;
    private final ModuleOrigin origin;

    private URLClassLoader loader;

    public DiSkyModule(String name, String author, String version, File moduleJar) {
        this.name = name;
        this.author = author;
        this.version = version;
        this.moduleJar = moduleJar;

        this.manager = DiSky.getModuleManager();
        this.origin = new ModuleOrigin(this);
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

    public <B extends Event, T> void registerValue(Class<B> bukkitClass,
                                                          Class<T> entityClass,
                                                          Function<B, T> function,
                                                          int time) {
        SkriptUtils.registerValue(bukkitClass, entityClass, function, time);
    }

    public <B extends Event, T> void registerValue(Class<B> bukkitClass, Class<T> entityClass, Function<B, T> function) {
        registerValue(bukkitClass, entityClass, function, 0);
    }

    public <E extends net.dv8tion.jda.api.events.Event, B extends SimpleDiSkyEvent<E>> void registerBotValue(Class<B> bukkitClass) {
        registerValue(bukkitClass, Bot.class, e -> {
            final JDA jda = e.getJDAEvent().getJDA();
            return DiSky.getManager().fromJDA(jda);
        });
    }

    protected <T> void registerType(Class<T> clazz, String codeName, Function<T, String> toString) {
        final DiSkyType<T> type = new DiSkyType<>(clazz, codeName, toString, null);
        type.register();
    }

    protected <T extends Enum<T>> void registerType(Class<T> clazz, String codeName) {
        final DiSkyType<T> type = DiSkyType.fromEnum(clazz, codeName, codeName);
        type.register();
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
