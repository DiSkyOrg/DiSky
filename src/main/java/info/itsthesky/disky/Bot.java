package info.itsthesky.disky;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.Nullable;

/**
 * Class that will handle every information about a bot.
 */
// TODO: 29/12/2021 Maybe use records here, but it's only for Java 14+
public class Bot {

    private final String name;
    private final JDA instance;

    public Bot(String name, JDA instance) {
        this.name = name;
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

        return new Bot(options.getName(), built);
    }

    public String getName() {
        return name;
    }

    public JDA getInstance() {
        return instance;
    }
}
