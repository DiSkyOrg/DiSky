package info.itsthesky.disky.elements.properties.guilds;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.api.changers.ChangeablePropertyExpression;
import info.itsthesky.disky.api.skript.EasyElement;
import info.itsthesky.disky.core.Bot;
import info.itsthesky.disky.core.Utils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Icon;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.URL;

public class GuildBanner extends ChangeablePropertyExpression<Guild, String> {

    static {
        register(
                GuildBanner.class,
                String.class,
                "[discord] banner",
                "guild"
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
        Guild guild = EasyElement.parseSingle(getExpr(), e, null);
        final String value = delta[0].toString();
        if (value == null || guild == null) return;

        guild = bot.getInstance().getGuildById(guild.getId());

        final InputStream iconStream;
        if (Utils.isURL(value)) {
            try {
                iconStream = new URL(value).openStream();
            } catch (IOException ex) {
                DiSky.getErrorHandler().exception(e, ex);
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
        } catch (IOException ex) {
            DiSky.getErrorHandler().exception(e, ex);
            return;
        }

        guild.getManager().setBanner(icon).queue();
    }

    @Override
    protected String @NotNull [] get(@NotNull Event e, Guild @NotNull [] source) {
        return new String[] {source[0].getBannerUrl()};
    }

    @Override
    public @NotNull Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "banner of " + getExpr().toString(e, debug);
    }

    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull SkriptParser.ParseResult parseResult) {
        setExpr((Expression<? extends Guild>) exprs[0]);
        return true;
    }
}
