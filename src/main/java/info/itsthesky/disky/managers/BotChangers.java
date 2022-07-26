package info.itsthesky.disky.managers;

import info.itsthesky.disky.core.Bot;
import net.dv8tion.jda.api.entities.*;

import java.util.HashMap;
import java.util.function.BiFunction;

public class BotChangers {

    public static final HashMap<Class<?>, BiFunction<?, Bot, ?>> CONVERTERS = new HashMap<>();

    private static <T> void register(BiFunction<T, Bot, T> converter, Class<T> clazz) {
        CONVERTERS.put(clazz, converter);
    }

    @SuppressWarnings("unchecked")
    public static <T> T convert(T entity, Bot bot) {
        final BiFunction<T, Bot, T> converter = (BiFunction<T, Bot, T>) CONVERTERS.getOrDefault(entity.getClass().getInterfaces()[0], null);
        if (converter == null)
            return null;
        return converter.apply(entity, bot);
    }

    private static <T> void register(Class<T> clazz, BiFunction<T, Bot, T> converter) {
        CONVERTERS.put(clazz, converter);
    }

    public static void init() {
        register(Guild.class, (entity, bot) -> bot.getInstance().getGuildById(entity.getId()));
        register(User.class, (entity, bot) -> bot.getInstance().getUserById(entity.getId()));
        register(Member.class, (entity, bot) -> bot.getInstance().getGuildById(entity.getGuild().getId()).getMemberById(entity.getId()));

        register(GuildChannel.class, (entity, bot) -> bot.getInstance().getGuildChannelById(entity.getId()));
        register(TextChannel.class, (entity, bot) -> bot.getInstance().getTextChannelById(entity.getId()));
        register(NewsChannel.class, (entity, bot) -> bot.getInstance().getNewsChannelById(entity.getId()));
        register(StageChannel.class, (entity, bot) -> bot.getInstance().getStageChannelById(entity.getId()));
        register(ThreadChannel.class, (entity, bot) -> bot.getInstance().getThreadChannelById(entity.getId()));
        register(VoiceChannel.class, (entity, bot) -> bot.getInstance().getVoiceChannelById(entity.getId()));

        register(Message.class, (entity, bot) ->
                bot.getInstance()
                        .getGuildById(entity.getGuild().getId())
                        .getTextChannelById(entity.getChannel().asGuildMessageChannel().getId())
                        .getHistory()
                        .getMessageById(entity.getId())
        );
    }
}
