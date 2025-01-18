package net.itsthesky.disky.api.modules;

import ch.njol.skript.SkriptAddon;
import net.itsthesky.disky.DiSky;
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
import java.util.*;
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
        final DiSkyModuleInfo info = DiSkyModuleInfo.fromYaml(config);
        if (info == null) {
            getLogger().severe("The module '"+file.getName()+"' was made for an older (4.20 or below) version of DiSky and cannot be loaded.");
            getLogger().severe("Please update the module at https://patreon.disky.me/ or on the wiki page (https://disky.me/docs)!");
            return null;
        }
        if (DiSky.getVersion().isSmallerThan(info.requiredMinVersion)) {
            getLogger().severe("The module '"+info.name+"' v"+info.version+" by '"+info.author+"' requires at least DiSky v"+info.requiredMinVersion+" to work! (You're using v"+DiSky.getVersion()+")");
            return null;
        }

        final URL[] urls = {new URL("jar:file:"+file.getAbsolutePath()+"!/")};
        final URLClassLoader loader = new URLClassLoader(urls, getClass().getClassLoader());
        final Class<DiSkyModule> clazz = (Class<DiSkyModule>) loader.loadClass(info.mainClass);

        final Constructor<DiSkyModule> constructor = clazz.getDeclaredConstructor(DiSkyModuleInfo.class, File.class);
        return constructor.newInstance(info, file)
                .setLoader(loader);
    }

    public void loadModules() throws IOException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvalidConfigurationException {
        final File[] modulesFile = this.moduleFolder.listFiles();
        assert modulesFile != null;
        for (final File moduleFile : modulesFile) {
            if (moduleFile.isDirectory())
                continue; // Skip directories

            if (!moduleFile.getName().endsWith(".jar")) {
                getLogger().warning("Skipping file '"+moduleFile.getPath()+"' as it's not a valid module file.");
                continue;
            }

            getLogger().warning("Loading module from file '"+moduleFile.getPath()+"'...");
            final DiSkyModule module;
            try {
                module = loadModule(moduleFile);
            } catch (Exception ex) {
                ex.printStackTrace();
                getLogger().severe("Unable to initialize module '"+moduleFile.getPath()+"'! Maybe a wrong Java version?");
                return;
            }
            if (module == null) // If the module is null, it means the version is not compatible
                continue;


            getLogger().info("Successfully loaded module '"+module.getModuleInfo().name+"' v"+module.getModuleInfo().version+" by '"+module.getModuleInfo().author+"'! Enabling ...");
            try {
                module.init(this.instance, this.addon);
            } catch (Exception ex) {
                ex.printStackTrace();
                getLogger().severe("Failed to enable module '"+module.getModuleInfo().name+"' v"+module.getModuleInfo().version+" by '"+module.getModuleInfo().author+"':");
                continue;
            }
            modules.put(module.getModuleInfo().name, module);
            getLogger().info("Successfully enabled module '"+module.getModuleInfo().name+"'!");
        }
    }

    public List<DiSkyModule> getModules() {
        return new ArrayList<>(modules.values());
    }

    public HashMap<String, DiSkyModule> getModulesMap() {
        return modules;
    }

    private Logger getLogger() {
        return this.instance.getLogger();
    }

}
