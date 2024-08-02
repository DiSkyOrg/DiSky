package info.itsthesky.disky.elements.properties.bot;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.api.changers.ChangeablePropertyExpression;
import info.itsthesky.disky.api.skript.EasyElement;
import info.itsthesky.disky.core.Bot;
import info.itsthesky.disky.managers.ConfigManager;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BotToken extends ChangeablePropertyExpression<Bot, String> {

    static {
        if (ConfigManager.get("token-of-bot-expression", true))
            register(
                    BotToken.class,
                    String.class,
                    "[discord] bot token",
                    "bot"
            );
    }

    @Override
    public Class<?>[] acceptChange(Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET)
            return CollectionUtils.array(String.class);
        return CollectionUtils.array();
    }

    @Override
    public void change(Event e, Object[] delta, Bot bot, Changer.ChangeMode mode) {
        if (delta == null || delta.length == 0 || delta[0] == null) return;
        bot = EasyElement.parseSingle(getExpr(), e, null);
        final String value = delta[0].toString();
        if (value == null || bot == null) return;

        bot.getInstance().getSelfUser().getManager().setName(value).queue(null, ex -> DiSky.getErrorHandler().exception(e, ex));
    }

    @Override
    protected String @NotNull [] get(@NotNull Event e, Bot @NotNull [] source) {
        return new String[] {source[0].getInstance().getSelfUser().getName()};
    }

    @Override
    public @NotNull Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "discord name of " + getExpr().toString(e, debug);
    }

    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull SkriptParser.ParseResult parseResult) {
        setExpr((Expression<? extends Bot>) exprs[0]);
        return true;
    }
}
