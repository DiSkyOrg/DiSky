package info.itsthesky.disky.core;

import info.itsthesky.disky.BotApplication;
import info.itsthesky.disky.DiSky;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.jetbrains.annotations.Nullable;

/**
 * Class that will handle every information about a bot.
 * @author ItsTheSky
 */
// TODO: 29/12/2021 Maybe use records here, but it's only for Java 14+
public class Bot {

    private final String name;
    private final JDA instance;
    private final @Nullable BotApplication application;

    public Bot(String name, JDA instance, @Nullable BotApplication application) {
        this.name = name;
        this.application = application;
        this.instance = instance;
    }

    public static @Nullable Bot create(BotOptions options) {
        final JDABuilder builder = options.toBuilder();

        final JDA built;
        try {
            built = builder.build();
        } catch (Throwable throwable) {
            DiSky.getErrorHandler().exception(throwable);
            return null;
        }

        return new Bot(options.getName(), built, options.getApplication());
    }

    public String getName() {
        return name;
    }

    public JDA getInstance() {
        return instance;
    }

    public @Nullable BotApplication getApplication() {
        return application;
    }
}
