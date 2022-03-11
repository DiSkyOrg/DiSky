package info.itsthesky.disky;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import info.itsthesky.disky.api.skript.ErrorHandler;
import info.itsthesky.disky.elements.BaseBotEffect;
import info.itsthesky.disky.managers.BotManager;
import info.itsthesky.disky.managers.ConfigManager;
import net.dv8tion.jda.api.requests.RestAction;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public final class DiSky extends JavaPlugin {

    private static DiSky instance;
    private static SkriptAddon addonInstance;
    private static ErrorHandler errorHandler;
    private static BotManager botManager;
    private static ConfigManager configManager;

    @Override
    public void onEnable() {

        /*
        We set up the base things here
         */
        instance = this;
        botManager = new BotManager(this);
        configManager = new ConfigManager(this);
        errorHandler = botManager.errorHandler();

        /*
        Check for Skript & start registration
         */
        if (!getServer().getPluginManager().isPluginEnabled("Skript")) {
            errorHandler.exception(new RuntimeException("Skript is not found, cannot start DiSky."));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        if (!Skript.isAcceptRegistrations()) {
            errorHandler.exception(new RuntimeException("Skript found, but it doesn't accept registration. Cannot start DiSky."));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        addonInstance = Skript.registerAddon(this);
        try {
            addonInstance.loadClasses("info.itsthesky.disky.elements");
        } catch (IOException e) {
            errorHandler.exception(e);
            return;
        }

        /*
        Default JDA's error handler
         */
        RestAction.setDefaultFailure(throwable -> DiSky.getErrorHandler().exception(throwable));

    }

    @Override
    public void onDisable() {
        botManager.shutdown();
    }

    public static DiSky getInstance() {
        return instance;
    }

    public static ErrorHandler getErrorHandler() {
        return errorHandler;
    }

    public static SkriptAddon getAddonInstance() {
        return addonInstance;
    }

    public static ConfigManager getConfigManager() {
        return configManager;
    }

    public static BotManager getManager() {
        return botManager;
    }
}
