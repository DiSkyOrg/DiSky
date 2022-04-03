package info.itsthesky.disky.elements.properties.guilds;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import info.itsthesky.disky.api.changers.ChangeablePropertyExpression;
import info.itsthesky.disky.api.skript.EasyElement;
import info.itsthesky.disky.core.Bot;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.VoiceChannel;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GuildAFKChannel extends ChangeablePropertyExpression<Guild, VoiceChannel> {

    static {
        register(
                GuildAFKChannel.class,
                VoiceChannel.class,
                "[discord] afk [voice( |-)] channel",
                "guild"
        );
    }

    @Override
    public Class<?>[] acceptChange(Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET)
            return CollectionUtils.array(VoiceChannel.class);
        return CollectionUtils.array();
    }

    @Override
    public void change(Event e, Object[] delta, Bot bot, Changer.ChangeMode mode) {
        if (delta == null || delta.length == 0 || delta[0] == null) return;
        Guild guild = EasyElement.parseSingle(getExpr(), e, null);
        final VoiceChannel value = (VoiceChannel) delta[0];
        if (value == null || guild == null) return;
        

        guild = bot.getInstance().getGuildById(guild.getId());
        guild.getManager().setAfkChannel(value).queue();
    }

    @Override
    protected VoiceChannel @NotNull [] get(@NotNull Event e, Guild @NotNull [] source) {
        return new VoiceChannel[] {source[0].getAfkChannel()};
    }

    @Override
    public @NotNull Class<? extends VoiceChannel> getReturnType() {
        return VoiceChannel.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "afk channel of " + getExpr().toString(e, debug);
    }

    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.@NotNull ParseResult parseResult) {
        setExpr((Expression<? extends Guild>) exprs[0]);
        return true;
    }
}
