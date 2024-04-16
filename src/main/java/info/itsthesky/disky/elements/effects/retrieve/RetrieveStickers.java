package info.itsthesky.disky.elements.effects.retrieve;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.util.AsyncEffect;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.core.Bot;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.sticker.GuildSticker;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Name("Retrieve Stickers")
@Description({"Retrieve every stickers (and cache them) from a specific guild."})
public class RetrieveStickers extends AsyncEffect {

    static {
        Skript.registerEffect(
                RetrieveStickers.class,
                "retrieve [(all|every)] stickers (from|with|of|in) %guild% [(with|using) [the] [bot] %-bot%] and store (them|the stickers) in %~objects%"
        );
    }

    private Expression<Guild> exprGuild;
    private Expression<Bot> exprBot;
    private Expression<Object> exprResult;

    @Override
    public boolean init(Expression<?>[] expressions, int i, @NotNull Kleenean kleenean, SkriptParser.@NotNull ParseResult parseResult) {
        getParser().setHasDelayBefore(Kleenean.TRUE);

        exprGuild = (Expression<Guild>) expressions[0];
        exprBot = (Expression<Bot>) expressions[1];
        exprResult = (Expression<Object>) expressions[2];
        return Changer.ChangerUtils.acceptsChange(exprResult, Changer.ChangeMode.SET, GuildSticker[].class);
    }

    @Override
    protected void execute(@NotNull Event event) {
        Guild guild = exprGuild.getSingle(event);
        Bot bot = exprBot == null ? Bot.any() : exprBot.getSingle(event);
        if (guild == null || bot == null)
            return;

        guild = bot.getInstance().getGuildById(guild.getId());
        if (guild == null)
            return;

        final List<GuildSticker> stickers;
        try {
            stickers = guild.retrieveStickers().complete();
        } catch (Exception ex) {
            Skript.error("Cannot retrieve stickers from the guild " + guild.getName() + " (" + guild.getId() + ")");
            return;
        }

        exprResult.change(event, stickers.toArray(new GuildSticker[0]), Changer.ChangeMode.SET);
    }

    @Override
    public @NotNull String toString(@Nullable Event event, boolean b) {
        return "retrieve every stickers from guild " + exprGuild.toString(event, b)
                + (exprBot == null ? "" : " using bot " + exprBot.toString(event, b))
                + " and store them in " + exprResult.toString(event, b);
    }
}
