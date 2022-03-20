package info.itsthesky.disky.core;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.requests.RestAction;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public final class Utils {

    public static String colored(String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }

    public static boolean equalsAnyIgnoreCase(String toMatch, String... potentialMatches) {
        return Arrays.asList(potentialMatches).contains(toMatch);
    }

    public static <T> void catchAction(RestAction<T> action,
                                   Consumer<T> success, Consumer<Throwable> error) {
        try {
            action.queue(success, error);
        } catch (Throwable ex) {
            error.accept(ex);
        }
    }

    public static String repeat(String str, int amount) {
        return new String(new char[amount]).replace("\0", str);
    }

	public static <T extends Enum<T>> List<T> parseEnum(Class<T> clazz, List<String> raws) {
        final List<T> values = new ArrayList<>();
        for (String raw : raws) {
            try {
                values.add(Enum.valueOf(clazz, raw.toUpperCase(Locale.ROOT).replace(" ", "_")));
            } catch (Exception ignored) {}
        }
        return values;
	}
}
