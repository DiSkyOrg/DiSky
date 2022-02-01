package info.itsthesky.disky.core;

import net.dv8tion.jda.api.requests.RestAction;
import org.bukkit.ChatColor;

import java.util.function.Consumer;

public final class Utils {

    public static String colored(String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }

    public static <T> void catchAction(RestAction<T> action,
                                   Consumer<T> success, Consumer<Throwable> error) {
        try {
            action.queue(success, error);
        } catch (Throwable ex) {
            error.accept(ex);
        }
    }

}
