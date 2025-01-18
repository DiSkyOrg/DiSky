package net.itsthesky.disky.elements.properties.bot;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import net.itsthesky.disky.api.skript.EasyElement;
import net.itsthesky.disky.api.skript.NodeInformation;
import net.itsthesky.disky.core.Bot;
import net.dv8tion.jda.api.entities.Activity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BotPresence extends PropertyExpression<Bot, Activity> {

    static {
        register(
                BotPresence.class,
                Activity.class,
                "[discord] presence",
                "bot"
        );
    }

    private NodeInformation info;

    @Override
    public Class<?> @NotNull [] acceptChange(@NotNull Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET)
            return CollectionUtils.array(Activity.class);
        return CollectionUtils.array();
    }

    @Override
    public void change(@NotNull Event e, @NotNull Object[] delta, @NotNull Changer.ChangeMode mode) {
        if (delta == null || delta.length == 0 || delta[0] == null) return;
        Bot bot = EasyElement.parseSingle(getExpr(), e, null);
        final Activity activity = (Activity) delta[0];
        if (activity == null || bot == null) return;

        bot.getInstance().getPresence().setPresence(bot.getInstance().getPresence().getStatus(), activity);
    }

    @Override
    protected Activity @NotNull [] get(@NotNull Event e, Bot @NotNull [] source) {
        return new Activity[] {source[0].getInstance().getPresence().getActivity()};
    }

    @Override
    public @NotNull Class<? extends Activity> getReturnType() {
        return Activity.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "presence of " + getExpr().toString(e, debug);
    }

    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull SkriptParser.ParseResult parseResult) {
        setExpr((Expression<? extends Bot>) exprs[0]);
        info = new NodeInformation();
        return true;
    }
}
