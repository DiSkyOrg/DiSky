package info.itsthesky.disky.core;

import ch.njol.util.NonNullPair;
import com.google.common.collect.Lists;
import info.itsthesky.disky.DiSky;
import net.dv8tion.jda.api.requests.RestAction;
import org.bukkit.ChatColor;
import org.bukkit.event.Event;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.function.Consumer;

public final class Utils {

    public static String colored(String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }

    public static String formatBytes(long input) {
        if (input < 1024) {
            return input + " B";
        } else if (input < 1024 * 1024) {
            return (input / 1024) + " KB";
        } else if (input < 1024 * 1024 * 1024) {
            return (input / 1024 / 1024) + " MB";
        } else {
            return (input / 1024 / 1024 / 1024) + " GB";
        }
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

    public static <T> void catchAction(RestAction<T> action, Event event) {
        catchAction(action, (v) -> {}, ex -> DiSky.getErrorHandler().exception(event, ex));
    }

    public static boolean isURL(String url) {
        try {
            final URL url1 = new URL(url);
            return true;
        } catch (Exception ex) {
            return false;
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

    public static boolean isBetween(Number value, Number min, Number max) {
        return value.doubleValue() >= min.doubleValue() && value.doubleValue() <= max.doubleValue();
    }

    public static InputStream convert(BufferedImage image) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", os);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ByteArrayInputStream(os.toByteArray());
    }

    public static List<NonNullPair<InputStream, String>> parseFiles(Object[] raws) throws FileNotFoundException {
        final List<NonNullPair<InputStream, String>> streams = new ArrayList<>();
        int imageCount = 1;
        for (Object raw : raws) {
            if (raw instanceof BufferedImage)
                streams.add(new NonNullPair<>(convert((BufferedImage) raw),
                        "image-" + imageCount++ + ".png"));
            if (raw instanceof String)
                streams.add(new NonNullPair<>(new FileInputStream((String) raw), new File((String) raw).getName()));
        }
        return streams;
    }
}
