package net.itsthesky.disky.elements.properties.guilds;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import net.itsthesky.disky.api.changers.ChangeablePropertyExpression;
import net.itsthesky.disky.api.skript.EasyElement;
import net.itsthesky.disky.core.Bot;
import net.itsthesky.disky.core.Utils;
import net.dv8tion.jda.api.entities.Guild;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GuildAFKTimeout extends ChangeablePropertyExpression<Guild, Number> {

    static {
        register(
                GuildAFKTimeout.class,
                Number.class,
                "[discord] afk time[( |-)]out [second[s]]",
                "guild"
        );
    }

    @Override
    public Class<?> @NotNull [] acceptChange(@NotNull Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET)
            return CollectionUtils.array(Number.class);
        return CollectionUtils.array();
    }

    @Override
    public void change(Event e, Object[] delta, Bot bot, Changer.ChangeMode mode) {
        if (delta == null || delta.length == 0 || delta[0] == null) return;
        Guild guild = EasyElement.parseSingle(getExpr(), e, null);
        final Number value = (Number) delta[0];
        if (value == null || guild == null) return;
        

        guild = bot.getInstance().getGuildById(guild.getId());

        final Guild.Timeout timeout;
        if (Utils.isBetween(value, 0, 60)) {
            timeout = Guild.Timeout.SECONDS_60;
        } else if (Utils.isBetween(value, 60, 300)) {
            timeout = Guild.Timeout.SECONDS_300;
        } else if (Utils.isBetween(value, 300, 900)) {
            timeout = Guild.Timeout.SECONDS_900;
        } else if (Utils.isBetween(value, 900, 1800)) {
            timeout = Guild.Timeout.SECONDS_1800;
        } else if (Utils.isBetween(value, 1800, 3600)) {
            timeout = Guild.Timeout.SECONDS_3600;
        } else return;

        Utils.catchAction(guild.getManager().setAfkTimeout(timeout), e);
    }

    @Override
    protected Number @NotNull [] get(@NotNull Event e, Guild @NotNull [] source) {
        return new Number[] {source[0].getAfkTimeout().getSeconds()};
    }

    @Override
    public @NotNull Class<? extends Number> getReturnType() {
        return Number.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "afk timeout of " + getExpr().toString(e, debug);
    }

    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull SkriptParser.ParseResult parseResult) {
        setExpr((Expression<? extends Guild>) exprs[0]);
        return true;
    }
}
