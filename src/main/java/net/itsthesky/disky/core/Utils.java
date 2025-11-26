package net.itsthesky.disky.core;

import ch.njol.util.NonNullPair;
import com.google.common.collect.Lists;
import net.itsthesky.disky.elements.sections.handler.DiSkyRuntimeHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.utils.data.DataObject;
import org.bukkit.ChatColor;
import org.bukkit.event.Event;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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

    public static byte[] readBytesFromStream(InputStream is) throws IOException {
        final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        final byte[] data = new byte[16384];
        int len;
        while ((len = is.read(data, 0, data.length)) != -1)
            buffer.write(data, 0, len);
        return buffer.toByteArray();
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
        catchAction(action, (v) -> {}, ex -> DiSkyRuntimeHandler.error((Exception) ex));
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

    public static EmbedBuilder convertJSONToEmbed(String json) {
        return EmbedBuilder.fromData(DataObject.fromJson(json));
    }

    public static String convertEmbedToJSON(EmbedBuilder builder) {
        if (builder.isEmpty())
            return "{}"; // TODO: 26/08/2023 Maybe handle that a bit better?

        final MessageEmbed embed = builder.build();
        return new String(embed.toData().toJson(), StandardCharsets.UTF_8);
    }

    public static String getClosest(String input, Set<String> others) {
        final List<String> list = Lists.newArrayList(others);
        return list.stream().min(Comparator.comparingInt(s -> LevenshteinDistance(input, s))).orElse(null);
    }

    public static int LevenshteinDistance(String s, String t) {
        int m = s.length();
        int n = t.length();
        int[][] d = new int[m + 1][n + 1];
        for (int i = 0; i <= m; i++) {
            d[i][0] = i;
        }
        for (int j = 0; j <= n; j++) {
            d[0][j] = j;
        }
        for (int j = 1; j <= n; j++) {
            for (int i = 1; i <= m; i++) {
                if (s.charAt(i - 1) == t.charAt(j - 1)) {
                    d[i][j] = d[i - 1][j - 1];
                } else {
                    d[i][j] = Math.min(d[i - 1][j] + 1, Math.min(d[i][j - 1] + 1, d[i - 1][j - 1] + 1));
                }
            }
        }
        return d[m][n];
    }
}
