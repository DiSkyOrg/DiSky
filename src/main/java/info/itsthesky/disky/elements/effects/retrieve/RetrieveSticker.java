package info.itsthesky.disky.elements.effects.retrieve;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.util.AsyncEffect;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.core.Bot;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.sticker.Sticker;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Retrieve Sticker")
@Description({"Retrieve a sticker from a guild using its per-guild name.",
        "This will return a sticker from the guild, not a global one."})
public class RetrieveSticker extends AsyncEffect {

    static {
        Skript.registerEffect(
                RetrieveSticker.class,
                "retrieve sticker (with|from) id %string% (from|with|of|in) %guild% [(with|using) [the] [bot] %-bot%] and store (it|the sticker) in %~objects%"
        );
    }

    private Expression<String> exprID;
    private Expression<Guild> exprGuild;
    private Expression<Bot> exprBot;
    private Expression<Object> exprResult;

    @Override
    public boolean init(Expression<?>[] expressions, int i, @NotNull Kleenean kleenean, SkriptParser.@NotNull ParseResult parseResult) {
        getParser().setHasDelayBefore(Kleenean.TRUE);

        exprID = (Expression<String>) expressions[0];
        exprGuild = (Expression<Guild>) expressions[1];
        exprBot = (Expression<Bot>) expressions[2];
        exprResult = (Expression<Object>) expressions[3];
        return Changer.ChangerUtils.acceptsChange(exprResult, Changer.ChangeMode.SET, Sticker.class);
    }

    @Override
    protected void execute(@NotNull Event event) {
        String id = exprID.getSingle(event);
        Guild guild = exprGuild.getSingle(event);
        Bot bot = Bot.fromContext(exprBot, event);
        if (id == null || guild == null || bot == null)
            return;

        guild = bot.getInstance().getGuildById(guild.getId());
        if (guild == null)
            return;

        final Sticker sticker;
        try {
            sticker = guild.getStickerById(id);
        } catch (Exception ex) {
            DiSky.getErrorHandler().exception(event, ex);
            return;
        }

        exprResult.change(event, new Sticker[] {sticker}, Changer.ChangeMode.SET);
    }

    @Override
    public @NotNull String toString(@Nullable Event event, boolean b) {
        return "retrieve sticker with id " + exprID.toString(event, b)
                + " from guild " + exprGuild.toString(event, b)
                + (exprBot == null ? "" : " using bot " + exprBot.toString(event, b))
                + " and store it in " + exprResult.toString(event, b);
    }
}
