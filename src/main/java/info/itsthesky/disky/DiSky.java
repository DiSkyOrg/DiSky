package info.itsthesky.disky;

import info.itsthesky.disky.core.ErrorHandler;
import net.dv8tion.jda.api.requests.RestAction;
import org.bukkit.plugin.java.JavaPlugin;

public final class DiSky extends JavaPlugin {

    private static DiSky instance;
    private static ErrorHandler errorHandler;
    private static BotManager manager;

    @Override
    public void onEnable() {

        /*
        We set up the base things here
         */
        instance = this;
        manager = new BotManager(this);
        errorHandler = manager.errorHandler();

        /*
        Default JDA's error handler
         */
        RestAction.setDefaultFailure(throwable -> DiSky.getErrorHandler().exception(throwable));

    }

    @Override
    public void onDisable() {
        manager.shutdown();
    }

    public static DiSky getInstance() {
        return instance;
    }

    public static ErrorHandler getErrorHandler() {
        return errorHandler;
    }

    public static BotManager getManager() {
        return manager;
    }
}
