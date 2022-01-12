package info.itsthesky.disky;

import info.itsthesky.disky.core.ErrorHandler;
import info.itsthesky.disky.managers.BotManager;
import info.itsthesky.disky.managers.ConfigManager;
import net.dv8tion.jda.api.requests.RestAction;
import org.bukkit.plugin.java.JavaPlugin;

public final class DiSky extends JavaPlugin {

    private static DiSky instance;
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

    public static ConfigManager getConfigManager() {
        return configManager;
    }

    public static BotManager getManager() {
        return botManager;
    }
}
