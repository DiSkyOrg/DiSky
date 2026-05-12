package net.itsthesky.disky.elements.effects.retrieve;

import ch.njol.skript.Skript;
import net.itsthesky.disky.api.DiSkyRegistry;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.util.AsyncEffect;
import ch.njol.util.Kleenean;
import net.itsthesky.disky.DiSky;
import net.itsthesky.disky.elements.sections.handler.DiSkyRuntimeHandler;
import net.itsthesky.disky.core.Bot;
import net.dv8tion.jda.api.entities.ApplicationInfo;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Retrieve Application Info")
@Description({"Retrieve the application info/meta of a specific bot and store it in a variable."})
@Examples({"retrieve the application info of bot \"MyBot\" and store it in {_info}"})
@Since("4.0.0")
public class RetrieveApplicationInfo extends AsyncEffect {

    static {
        Skript.registerEffect(RetrieveApplicationInfo.class,
                "retrieve [the] application (info|meta) of [the] [bot] %bot% and store (it|the (application|info|result)) in %~objects%");
    }

    private Expression<Bot> exprBot;
    private Expression<Object> exprResult;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.@NotNull ParseResult parseResult) {
        getParser().setHasDelayBefore(Kleenean.TRUE);

        exprBot = (Expression<Bot>) expressions[0];
        exprResult = (Expression<Object>) expressions[1];

        return Changer.ChangerUtils.acceptsChange(exprResult, Changer.ChangeMode.SET, ApplicationInfo.class);
    }

    @Override
    protected void execute(@NotNull Event event) {
        final Bot bot = exprBot.getSingle(event);
        if (bot == null)
            return;

        final ApplicationInfo applicationInfo;
        try {
            applicationInfo = bot.getInstance().retrieveApplicationInfo().complete();
        } catch (Exception ex ) {
            DiSkyRuntimeHandler.error((Exception) ex);
            return;
        }

        exprResult.change(event, new ApplicationInfo[] {applicationInfo}, Changer.ChangeMode.SET);
    }

    @Override
    public @NotNull String toString(@Nullable Event event, boolean debug) {
        return "retrieve application info of bot " + exprBot.toString(event, debug) + " and store it in " + exprResult.toString(event, debug);
    }

}
