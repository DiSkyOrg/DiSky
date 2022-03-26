package info.itsthesky.disky.elements.properties.bot;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.api.changers.ChangeablePropertyExpression;
import info.itsthesky.disky.api.skript.EasyElement;
import info.itsthesky.disky.api.skript.NodeInformation;
import info.itsthesky.disky.core.Bot;
import info.itsthesky.disky.core.Utils;
import net.dv8tion.jda.api.entities.Icon;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.URL;

public class BotAvatar extends ChangeablePropertyExpression<Bot, String> {

    static {
        register(
                BotAvatar.class,
                String.class,
                "[discord] [bot] avatar",
                "bot"
        );
    }

    private NodeInformation info;

    @Override
    public Class<?> @NotNull [] acceptChange(Changer.@NotNull ChangeMode mode) {
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

        final InputStream iconStream;
        if (Utils.isURL(value)) {
            try {
                iconStream = new URL(value).openStream();
            } catch (IOException ioException) {
                DiSky.getErrorHandler().exception(e, ioException);
                return;
            }
        } else {
            final File iconFile = new File(value);
            if (iconFile == null || !iconFile.exists())
                return;
            try {
                iconStream = new FileInputStream(iconFile);
            } catch (FileNotFoundException ex) {
                DiSky.getErrorHandler().exception(e, ex);
                return;
            }
        }

        final Icon icon;
        try {
            icon = Icon.from(iconStream);
        } catch (IOException ioException) {
            DiSky.getErrorHandler().exception(e, ioException);
            return;
        }

        bot.getInstance().getSelfUser().getManager().setAvatar(icon).queue(null, ex -> DiSky.getErrorHandler().exception(e, ex));
    }

    @Override
    protected String @NotNull [] get(@NotNull Event e, Bot @NotNull [] source) {
        return new String[] {source[0].getInstance().getSelfUser().getAvatarUrl()};
    }

    @Override
    public @NotNull Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "avatar of " + getExpr().toString(e, debug);
    }

    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.@NotNull ParseResult parseResult) {
        setExpr((Expression<? extends Bot>) exprs[0]);
        info = new NodeInformation();
        return true;
    }
}
