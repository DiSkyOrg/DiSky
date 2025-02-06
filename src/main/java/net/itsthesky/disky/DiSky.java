package net.itsthesky.disky;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import ch.njol.skript.util.Version;
import net.itsthesky.disky.api.emojis.EmojiStore;
import net.itsthesky.disky.api.generator.DocBuilder;
import net.itsthesky.disky.api.modules.DiSkyModule;
import net.itsthesky.disky.api.modules.ModuleManager;
import net.itsthesky.disky.api.skript.ErrorHandler;
import net.itsthesky.disky.core.DiSkyCommand;
import net.itsthesky.disky.core.DiSkyMetrics;
import net.itsthesky.disky.core.Utils;
import net.itsthesky.disky.elements.properties.DynamicElements;
import net.itsthesky.disky.elements.structures.context.ContextCommandManager;
import net.itsthesky.disky.elements.structures.slash.SlashManager;
import net.itsthesky.disky.managers.BotManager;
import net.itsthesky.disky.managers.ConfigManager;
import net.itsthesky.disky.managers.WebhooksManager;
import net.dv8tion.jda.api.requests.RestAction;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxRegistry;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;

public final class DiSky extends JavaPlugin {

    private static DiSky instance;
    private static SkriptAddon addonInstance;
    private static ErrorHandler errorHandler;
    private static BotManager botManager;
    private static boolean skImageInstalled;
    private static ModuleManager moduleManager;
    private static DocBuilder builder;
    private static WebhooksManager webhooksManager;

    public static DiSkyModule getModule(String moduleName) {
        return getModuleManager()
                .getModules()
                .stream()
                .filter(module -> module.getModuleInfo().name.equalsIgnoreCase(moduleName))
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
        webhooksManager = new WebhooksManager(this);
        errorHandler = botManager.errorHandler();
        skImageInstalled = getServer().getPluginManager().isPluginEnabled("SkImage2");

        getCommand("disky").setExecutor(new DiSkyCommand());
        if (!getDataFolder().exists())
            getDataFolder().mkdirs();

        /*
         * Check for Skript
         */
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

        /*
         * Loading the configuration & the metrics
         */
        ConfigManager.loadConfig(this);
        DiSkyMetrics.init(this);

        /*
        Saving & loading emojis
         */

        final File emojisFile = new File(getDataFolder(), "emojis.json");
        if (!emojisFile.exists()) {
            getLogger().info("Saving emoji's file ...");
            try {
                InputStream stream = getResource("emojis.json");
                if (stream == null) {
                    getLogger().severe("Could not find emoji's file of the JAR, this should never happens :c");
                    getServer().getPluginManager().disablePlugin(this);
                    return;
                }
                Files.write(emojisFile.toPath(), Utils.readBytesFromStream(stream));
            } catch (RuntimeException | IOException e) {
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
        addonInstance = Skript.registerAddon(this);
        moduleManager = new ModuleManager(new File(getDataFolder(), "modules"), this, addonInstance);
        try {
            DynamicElements.registerLogs();
            DynamicElements.registerThreadProperties();

            addonInstance.loadClasses("net.itsthesky.disky.elements");
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
        if (ConfigManager.get("debug", false))
            Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_PURPLE + "DEBUG: " + ChatColor.LIGHT_PURPLE + s);
    }

    @Override
    public void onDisable() {
        SlashManager.shutdownAll();
        ContextCommandManager.shutdownAll();
        moduleManager.getModules().forEach(DiSkyModule::shutdown);

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

    public static BotManager getManager() {
        return botManager;
    }

    public static WebhooksManager getWebhooksManager() {
        return webhooksManager;
    }

    public static void runtimeError(String description, @Nullable Object... data) {
        getInstance().getLogger().severe(ChatColor.DARK_RED + "DiSky Runtime Warning: " + ChatColor.RED + description);
        if (data != null) {
            getInstance().getLogger().severe(ChatColor.RED + "Provided data context:");
            for (int i = 0; i < data.length - 1; i += 2)
                getInstance().getLogger().severe(ChatColor.GOLD + "  - " + ChatColor.YELLOW + data[i] + ChatColor.GRAY + ": " + ChatColor.WHITE + data[i + 1]);
        }
    }

    public static SyntaxRegistry syntaxRegistry() {
        return getAddonInstance().syntaxRegistry();
    }

    public static Version getVersion() {
        return new Version(getInstance().getDescription().getVersion());
    }
}
