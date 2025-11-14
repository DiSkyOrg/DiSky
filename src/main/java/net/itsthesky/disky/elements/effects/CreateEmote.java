package net.itsthesky.disky.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.SeeAlso;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.AsyncEffect;
import ch.njol.util.Kleenean;
import net.itsthesky.disky.DiSky;
import net.itsthesky.disky.api.emojis.Emote;
import net.itsthesky.disky.core.JDAUtils;
import net.itsthesky.disky.elements.sections.handler.DiSkyRuntimeHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Icon;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.itsthesky.disky.api.skript.EasyElement.anyNull;

@Name("Create Emote")
@Description({
        "Create a new emote in a specific guild.",
        "The name must be between 2 and 32 chars and the guild should not have an emote with the same name.",
        "The URL will represent the image, and can be either a web URL or a local path."
})
@Examples({"create new emote named \"test\" with url \"https://static.wikia.nocookie.net/leagueoflegends/images/a/ae/This_Changes_Everything_Emote.png/revision/latest/scale-to-width-down/250?cb=20211019231749\" in event-guild and store it in {_emote}",
"make emote with name \"test2\" with path \"plugins/path/image.png\" in event-guild and store it in {_emote}"})
@Since("4.0.0")
@SeeAlso({Guild.class, Icon.class})
public class CreateEmote extends AsyncEffect {

    static {
        Skript.registerEffect(
                CreateEmote.class,
                "(make|create) [the] [new] emote (named|with name) %string% with [the] (url|path) %string% in [the] [guild] %guild% and store (it|the emote) in %~objects%"
        );
    }

    private Expression<String> exprName;
    private Expression<String> exprURL;
    private Expression<Guild> exprGuild;
    private Expression<Object> exprResult;

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "create new emote named " + exprName.toString(e, debug)
                + " with url/path " + exprURL.toString(e, debug)
                + " in guild " + exprGuild.toString(e, debug)
                + " and store it in " + exprResult.toString(e, debug);
    }

    @Override
    public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, ParseResult parseResult) {
        getParser().setHasDelayBefore(Kleenean.TRUE);

        exprName = (Expression<String>) exprs[0];
        exprURL = (Expression<String>) exprs[1];
        exprGuild = (Expression<Guild>) exprs[2];
        exprResult = (Expression<Object>) exprs[3];

        return Changer.ChangerUtils.acceptsChange(exprResult, Changer.ChangeMode.SET, Emote.class);
    }

    @Override
    public void execute(@NotNull Event e) {
        final String name = exprName.getSingle(e);
        final String url = exprURL.getSingle(e);
        final Guild guild = exprGuild.getSingle(e);

        if (anyNull(this, name, guild, url))
            return;

        final Icon icon = JDAUtils.parseIcon(url);
        if (anyNull(this, icon))
            return;


        final Emote emote;
        try {
            emote = Emote.fromJDA(guild.createEmoji(name, icon).complete());
        } catch (Exception ex) {
            DiSkyRuntimeHandler.error((Exception) ex);
            return;
        }

        exprResult.change(e, new Emote[] {emote}, Changer.ChangeMode.SET);
    }
}
