package net.itsthesky.disky.elements.effects.retrieve;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
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
