package info.itsthesky.disky.core;

import org.bukkit.ChatColor;

public final class Utils {

    public static String colored(String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }

}
