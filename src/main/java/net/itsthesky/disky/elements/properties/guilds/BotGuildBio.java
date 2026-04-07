package net.itsthesky.disky.elements.properties.guilds;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.itsthesky.disky.api.changers.ChangeablePropertyExpression;
import net.itsthesky.disky.api.skript.EasyElement;
import net.itsthesky.disky.core.Bot;
import net.itsthesky.disky.elements.sections.handler.DiSkyRuntimeHandler;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Bot's Guild Bio")
@Description({
    "Get or set the bot's per-guild bio in a specific guild.",
    "Reset with `reset`.",
    "This is a JDA 6.1.0+ feature."
})
@Examples({
    "set bot guild bio of event-guild to \"Hello from this server!\"",
    "reset bot guild bio of event-guild"
})
@Since("4.27.0")
public class BotGuildBio extends ChangeablePropertyExpression<Guild, String> {

    static {
        register(
                BotGuildBio.class,
                String.class,
                "bot guild bio",
                "guild"
        );
    }

    @Override
    public Class<?>[] acceptChange(Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET || mode == Changer.ChangeMode.RESET)
            return CollectionUtils.array(String.class);
        return CollectionUtils.array();
    }

    @Override
    public void change(Event e, Object[] delta, Bot bot, Changer.ChangeMode mode) {
        Guild guild = EasyElement.parseSingle(getExpr(), e, null);
        if (guild == null) return;

        guild = bot.getInstance().getGuildById(guild.getId());
        if (guild == null) return;

        try {
            final var manager = guild.getSelfMember().getManager();
            if (mode == Changer.ChangeMode.RESET) {
                manager.setBio(null).complete();
            } else {
                if (delta == null || delta.length == 0 || delta[0] == null) return;
                final String bio = delta[0].toString();
                manager.setBio(bio).complete();
            }
        } catch (Exception ex) {
            DiSkyRuntimeHandler.error(ex);
        }
    }

    @Override
    protected String @NotNull [] get(@NotNull Event e, Guild @NotNull [] source) {
        return new String[0];
    }

    @Override
    public @NotNull Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "bot guild bio of " + getExpr().toString(e, debug);
    }

    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull SkriptParser.ParseResult parseResult) {
        setExpr((Expression<? extends Guild>) exprs[0]);
        return true;
    }
}
