package info.itsthesky.disky;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import de.leonhard.storage.util.FileUtils;
import info.itsthesky.disky.api.emojis.EmojiStore;
import info.itsthesky.disky.api.generator.DocBuilder;
import info.itsthesky.disky.api.modules.DiSkyModule;
import info.itsthesky.disky.api.modules.ModuleManager;
import info.itsthesky.disky.api.skript.ErrorHandler;
import info.itsthesky.disky.core.DiSkyCommand;
import info.itsthesky.disky.elements.properties.ConstLogs;
import info.itsthesky.disky.managers.BotManager;
import info.itsthesky.disky.managers.ConfigManager;
import net.dv8tion.jda.api.requests.RestAction;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;

public final class DiSky extends JavaPlugin {

    private static DiSky instance;
    private static SkriptAddon addonInstance;
    private static ErrorHandler errorHandler;
    private static BotManager botManager;
    private static ConfigManager configManager;
    private static boolean skImageInstalled;
    private static ModuleManager moduleManager;
    private static DocBuilder builder;

    public static DiSkyModule getModule(String moduleName) {
        return getModuleManager()
                .getModules()
                .stream()
                .filter(module -> module.getName().equalsIgnoreCase(moduleName))
                .findAny()
                .orElse(null);
    }

    @Override
    public void onEnable() {

        /*
        We set up the base things here
         */
        instance = this;
        botManager = new BotManager(this);
        builder = new DocBuilder(this);
        configManager = new ConfigManager(this);
        errorHandler = botManager.errorHandler();
        skImageInstalled = getServer().getPluginManager().isPluginEnabled("SkImage");

        getCommand("disky").setExecutor(new DiSkyCommand());

        /*
        Saving & loading emojis
         */

        final File emojisFile = new File(getDataFolder(), "emojis.json");
        if (!emojisFile.exists()) {
            getLogger().info("Saving emoji's file ...");
            try {
                InputStream stream = getResource("emojis.json");
                FileUtils.writeToFile(new File(getDataFolder(), "emojis.json"), stream);
            } catch (RuntimeException e) {
                e.printStackTrace();
                getLogger().severe("An error occurred while saving emojis file! Emojis will not be available.");
            }
            getLogger().info("Success!");
        }
        getLogger().info("Loading emoji library ...");
        try {
            EmojiStore.loadLocal();
        } catch (IOException e) {
            e.printStackTrace();
            getLogger().severe("An error occurred while loading emojis! They will not be available.");
        }
        getLogger().info("Success!");

        /*
        Check for Skript & start registration
         */

        if (skImageInstalled)
            getLogger().info("SkImage has been found! Enabling images syntax.");
        if (!getServer().getPluginManager().isPluginEnabled("Skript")) {
            errorHandler.exception(null, new RuntimeException("Skript is not found, cannot start DiSky."));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        if (!Skript.isAcceptRegistrations()) {
            errorHandler.exception(null, new RuntimeException("Skript found, but it doesn't accept registration. Cannot start DiSky."));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        addonInstance = Skript.registerAddon(this);
        moduleManager = new ModuleManager(new File(getDataFolder(), "modules"), this, addonInstance);
        try {
            ConstLogs.register();
            addonInstance.loadClasses("info.itsthesky.disky.elements");
            moduleManager.loadModules();
        } catch (IOException e) {
            errorHandler.exception(null, e);
            return;
        } catch (ClassNotFoundException | InvalidConfigurationException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }

        /*
        Default JDA's error handler
         */
        RestAction.setDefaultFailure(throwable -> DiSky.getErrorHandler().exception(null, throwable));

    }

    public static void debug(String s) {
        getInstance().debugMessage(s);
    }

    private void debugMessage(String s) {
        getLogger().log(Level.INFO, s);
    }

    @Override
    public void onDisable() {
        botManager.shutdown();
    }

    public static boolean isSkImageInstalled() {
        return skImageInstalled;
    }

    public static DiSky getInstance() {
        return instance;
    }

    public static DocBuilder getDocBuilder() {
        return builder;
    }

    public static ErrorHandler getErrorHandler() {
        return errorHandler;
    }

    public static SkriptAddon getAddonInstance() {
        return addonInstance;
    }

    public static ModuleManager getModuleManager() {
        return moduleManager;
    }

    public static ConfigManager getConfigManager() {
        return configManager;
    }

    public static BotManager getManager() {
        return botManager;
    }
}
