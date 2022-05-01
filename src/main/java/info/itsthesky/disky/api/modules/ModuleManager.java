package info.itsthesky.disky.api.modules;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.classes.Serializer;
import ch.njol.skript.classes.data.JavaClasses;
import ch.njol.skript.lang.ExpressionInfo;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.lang.SyntaxElementInfo;
import ch.njol.skript.lang.VariableString;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.registrations.Converters;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Utils;
import ch.njol.yggdrasil.Fields;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.api.ReflectionUtils;
import info.itsthesky.disky.api.generator.DocBuilder;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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
        final URLClassLoader loader = new URLClassLoader(urls, getClass().getClassLoader());
        final Class<DiSkyModule> clazz = (Class<DiSkyModule>) loader.loadClass(classPath);

        final Constructor<DiSkyModule> constructor = clazz.getDeclaredConstructor(String.class, String.class, String.class, File.class);
        return constructor.newInstance(name, author, version, file)
                .setLoader(loader);
    }

    public void reload(DiSkyModule module) throws IOException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvalidConfigurationException {
        final File cachedFile = module.getModuleJar();

        getLogger().info("Reloading module " + module.getName() + "...");
        disable(module);
        module = loadModule(cachedFile);

        getLogger().info("Enabling module " + module.getName() + "...");
        ReflectionUtils.setField(Skript.class, null, "acceptRegistrations", true);
        module.init(instance, addon);
        ReflectionUtils.setField(Skript.class, null, "acceptRegistrations", false);
        Classes.onRegistrationsStop();
        Converters.createMissingConverters();

        modules.put(module.getName(), module);
        getLogger().info("Module " + module.getName() + " reloaded (version "+module.getVersion()+").");
    }

    public void disable(DiSkyModule module) {
        getLogger().info("Disabling module " + module.getName() + "...");
        // Removing every syntax registered by the module
        final List<SyntaxElementInfo<?>> infos = new ArrayList<>();

        infos.addAll(Skript.getEffects());
        infos.addAll(Skript.getSections());
        infos.addAll(Skript.getEvents());
        infos.addAll(Skript.getConditions());
        final List<SyntaxElementInfo<?>> expressions = new ArrayList<>();
        Skript.getExpressions().forEachRemaining(expressions::add);
        infos.addAll(expressions);

        for (SyntaxElementInfo<?> info : infos)
            if (DocBuilder.isFromModule(info, module))
                unregisterSyntax(info);

        getLogger().info("Unregistering all classes from module " + module.getName() + "...");
        final List<ClassInfo<?>> removedClasses = unregisterClasses(module);
        getLogger().info("Success, removed "+removedClasses.size()+" classes. ("+removedClasses+")");
        Classes.onRegistrationsStop();

        modules.remove(module.getName());

        try {
            module.getLoader().close();
        } catch (IOException e) {
            e.printStackTrace();
            getLogger().severe("Error while closing classloader!");
            return;
        }
        getLogger().info("Module " + module.getName() + " disabled.");
    }

    @SuppressWarnings("unchecked")
    private List<ClassInfo<?>> unregisterClasses(DiSkyModule module) {
        final List<ClassInfo<?>> removedClasses = new ArrayList<>();
        for (ClassInfo<?> info : new ArrayList<>(module.getRegisteredClasses()))
            try {
                final Field tempClassInfos = Classes.class.getDeclaredField("tempClassInfos");
                tempClassInfos.setAccessible(true);
                ((List<ClassInfo<?>>) tempClassInfos.get(null)).remove(info);

                final Field classInfos = Classes.class.getDeclaredField("classInfos");
                classInfos.setAccessible(true);
                final ClassInfo<?>[] current = (ClassInfo<?>[]) classInfos.get(null);
                final List<ClassInfo<?>> changedArray = new ArrayList<>(Arrays.asList(current));
                changedArray.removeIf(c -> c.getCodeName().equals(info.getCodeName()));
                classInfos.set(null, changedArray.toArray(new ClassInfo[0]));

                final Field superClassInfos = Classes.class.getDeclaredField("superClassInfos");
                superClassInfos.setAccessible(true);
                ((HashMap<Class<?>, ClassInfo<?>>) superClassInfos.get(null)).remove(info.getC());

                final Field classInfosByCodeName = Classes.class.getDeclaredField("classInfosByCodeName");
                classInfosByCodeName.setAccessible(true);
                ((HashMap<String, ClassInfo<?>>) classInfosByCodeName.get(null)).remove(info.getCodeName());

                final Field exactClassInfos = Classes.class.getDeclaredField("exactClassInfos");
                exactClassInfos.setAccessible(true);
                ((HashMap<Class<?>, ClassInfo<?>>) exactClassInfos.get(null)).remove(info.getC());

                removedClasses.add(info);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        return removedClasses;
    }

    private void unregisterSyntax(SyntaxElementInfo<?> info) {
        final Collection<SyntaxElementInfo<?>> effects = Skript
                .getEffects().stream().filter(i -> i != info).collect(Collectors.toList());
        final Collection<SyntaxElementInfo<?>> conditions = Skript.getConditions().stream()
                .filter(i -> i != info).collect(Collectors.toList());
        final Collection<SyntaxElementInfo<?>> sections = Skript.getSections().stream()
                .filter(i -> i != info).collect(Collectors.toList());
        final Collection<SyntaxElementInfo<?>> events = Skript.getEvents().stream()
                .filter(i -> i != info).collect(Collectors.toList());
        final Collection<SyntaxElementInfo<?>> expressions = StreamSupport.stream(((Iterable<ExpressionInfo<?, ?>>) Skript::getExpressions).spliterator(), false)
                .filter(i -> i != info).collect(Collectors.toList());

        ReflectionUtils.setFinalCollection(Skript.class, "effects", effects);
        ReflectionUtils.setFinalCollection(Skript.class, "conditions", conditions);
        ReflectionUtils.setFinalCollection(Skript.class, "sections", sections);
        ReflectionUtils.setFinalCollection(Skript.class, "events", events);
        ReflectionUtils.setFinalCollection(Skript.class, "expressions", new ArrayList<>(expressions));
    }

    public void loadModules() throws IOException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvalidConfigurationException {
        final File[] modulesFile = this.moduleFolder.listFiles();
        assert modulesFile != null;
        for (final File moduleFile : modulesFile) {
            getLogger().warning("Loading module from file '"+moduleFile.getPath()+"'...");
            final DiSkyModule module;
            try {
                module = loadModule(moduleFile);
            } catch (Exception ex) {
                ex.printStackTrace();
                getLogger().severe("Unable to initialize module '"+moduleFile.getPath()+"'! Maybe a wrong Java version?");
                return;
            }
            getLogger().info("Successfully loaded module '"+module.getName()+"' v"+module.getVersion()+" by '"+module.getAuthor()+"'! Enabling ...");
            try {
                module.init(this.instance, this.addon);
            } catch (Exception ex) {
                ex.printStackTrace();
                getLogger().severe("Failed to enable module '"+module.getName()+"' v"+module.getVersion()+" by '"+module.getAuthor()+"':");
                continue;
            }
            modules.put(module.getName(), module);
            getLogger().info("Successfully enabled module '"+module.getName()+"'!");
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
