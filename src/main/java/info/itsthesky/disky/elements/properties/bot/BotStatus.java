package info.itsthesky.disky.elements.properties.bot;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import info.itsthesky.disky.api.skript.EasyElement;
import info.itsthesky.disky.core.Bot;
import info.itsthesky.disky.api.skript.NodeInformation;
import net.dv8tion.jda.api.OnlineStatus;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BotStatus extends PropertyExpression<Bot, OnlineStatus> {

    static {
        register(
                BotStatus.class,
                OnlineStatus.class,
                "[discord] [online] status",
                "bot"
        );
    }

    private NodeInformation info;

    @Override
    public Class<?> @NotNull [] acceptChange(Changer.@NotNull ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET)
            return CollectionUtils.array(OnlineStatus.class);
        return CollectionUtils.array();
    }

    @Override
    public void change(@NotNull Event e, Object @NotNull [] delta, Changer.@NotNull ChangeMode mode) {
        if (delta == null || delta.length == 0 || delta[0] == null) return;
        Bot bot = EasyElement.parseSingle(getExpr(), e, null);
        final OnlineStatus status = (OnlineStatus) delta[0];
        if (status == null || bot == null) return;

        bot.getInstance().getPresence().setPresence(status, bot.getInstance().getPresence().getActivity());
    }

    @Override
    protected OnlineStatus @NotNull [] get(@NotNull Event e, Bot @NotNull [] source) {
        if (source.length == 0)
            return new OnlineStatus[0];
        return new OnlineStatus[] {source[0].getInstance().getPresence().getStatus()};
    }

    @Override
    public @NotNull Class<? extends OnlineStatus> getReturnType() {
        return OnlineStatus.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "online status of " + getExpr().toString(e, debug);
    }

    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.@NotNull ParseResult parseResult) {
        setExpr((Expression<? extends Bot>) exprs[0]);
        info = new NodeInformation();
        return true;
    }
}
