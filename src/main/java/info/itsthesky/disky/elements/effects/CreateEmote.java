package info.itsthesky.disky.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.Variable;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.api.emojis.Emote;
import info.itsthesky.disky.api.skript.SpecificBotEffect;
import info.itsthesky.disky.core.Bot;
import info.itsthesky.disky.core.JDAUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Icon;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Create Emote")
@Description({
        "Create a new emote in a specific guild.",
        "The name must be between 2 and 32 chars and the guild should not have an emote with the same name.",
        "The URL will represent the image, and can be either a web URL or a local path."
})
@Examples({"create new emote named \"test\" with url \"https://static.wikia.nocookie.net/leagueoflegends/images/a/ae/This_Changes_Everything_Emote.png/revision/latest/scale-to-width-down/250?cb=20211019231749\" in event-guild and store it in {_emote}",
"make emote with name \"test2\" with path \"plugins/path/image.png\" in event-guild and store it in {_emote}"})
public class CreateEmote extends SpecificBotEffect<Emote> {

    static {
        Skript.registerEffect(
                CreateEmote.class,
                "(make|create) [the] [new] emote (named|with name) %string% with [the] (url|path) %string% in [the] [guild] %guild% and store (it|the emote) in %object%"
        );
    }

    private Expression<String> exprName;
    private Expression<String> exprURL;
    private Expression<Guild> exprGuild;
    private Expression<Bot> exprBot;

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "create new emote named " + exprName.toString(e, debug) + " using bot " + exprBot.toString(e, debug);
    }

    @Override
    public boolean initEffect(Expression<?>[] exprs, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        exprName = (Expression<String>) exprs[0];
        exprURL = (Expression<String>) exprs[1];
        exprGuild = (Expression<Guild>) exprs[2];
        setChangedVariable((Variable<Emote>) exprs[3]);
        return true;
    }

    @Override
    public void runEffect(@NotNull Event e, Bot bot) {
        final String name = exprName.getSingle(e);
        final String url = exprURL.getSingle(e);
        Guild guild = exprGuild.getSingle(e);

        if (anyNull(this, name, guild, url)) {
            restart();
            return;
        }
        if (bot != null)
            guild = bot.getInstance().getGuildById(guild.getId());

        final Icon icon = JDAUtils.parseIcon(url);
        if (anyNull(this, icon)) {
            restart();
            return;
        }
        guild.createEmoji(name, icon).queue(emote -> restart(Emote.fromJDA(emote)), ex -> {
            DiSky.getErrorHandler().exception(e, ex);
            restart();
        });
    }
}
