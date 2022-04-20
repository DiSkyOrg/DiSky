package info.itsthesky.disky.api.modules;

import ch.njol.skript.SkriptAddon;
import info.itsthesky.disky.DiSky;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ModuleManager {

    private final HashMap<String, DiSkyModule> modules;
    private final File moduleFolder;
    private final DiSky instance;
    private final SkriptAddon addon;

    public ModuleManager(final File moduleFolder, DiSky instance, SkriptAddon addon) {
        this.instance = instance;
        this.addon = addon;
        this.modules = new HashMap<>();
        if (!moduleFolder.exists())
            moduleFolder.mkdirs();
        this.moduleFolder = moduleFolder;
    }

    private DiSkyModule loadModule(final File file) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, InvalidConfigurationException {

        final JarFile jf = new JarFile(file);
        final JarEntry moduleFile = jf.getJarEntry("module.yml");
        final String moduleYml = new BufferedReader(new InputStreamReader(jf.getInputStream(moduleFile))).lines().collect(Collectors.joining("\n"));

        final YamlConfiguration config = new YamlConfiguration();
        config.loadFromString(moduleYml);

        final String classPath = config.getString("MainClass");

        final String name = config.getString("Name");
        final String author = config.getString("Author");
        final String version = config.getString("Version");

        final URL[] urls = {new URL("jar:file:"+file.getAbsolutePath()+"!/")};
        final URLClassLoader loader = new URLClassLoader(urls, this.getClass().getClassLoader());
        final Class<DiSkyModule> clazz = (Class<DiSkyModule>) loader.loadClass(classPath);

        final Constructor<DiSkyModule> constructor = clazz.getDeclaredConstructor(String.class, String.class, String.class, File.class);
        return constructor.newInstance(name, author, version, file);
    }

    public void loadModules() throws IOException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvalidConfigurationException {
        final File[] modulesFile = this.moduleFolder.listFiles();
        for (final File moduleFile : modulesFile) {
            getLogger().warning("Loading module from file '"+moduleFile.getPath()+"'...");
            final DiSkyModule module = loadModule(moduleFile);
            getLogger().info("Successfully loaded module '"+module.getName()+"' v"+module.getVersion()+" by '"+module.getAuthor()+"'! Enabling ...");
            try {
                module.init(this.instance, this.addon);
            } catch (Exception ex) {
                getLogger().severe("Failed to enable module '"+module.getName()+"' v"+module.getVersion()+" by '"+module.getAuthor()+"':");
                ex.printStackTrace();
                continue;
            }
            modules.put(module.getName(), module);
            getLogger().info("Successfully enabled module '"+module.getName()+"'!");
        }
    }

    private Logger getLogger() {
        return this.instance.getLogger();
    }

}
