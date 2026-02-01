package net.itsthesky.disky.elements.effects.retrieve;

import ch.njol.skript.Skript;
import net.itsthesky.disky.api.DiSkyRegistry;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.util.AsyncEffect;
import ch.njol.util.Kleenean;
import net.itsthesky.disky.DiSky;
import net.itsthesky.disky.elements.sections.handler.DiSkyRuntimeHandler;
import net.itsthesky.disky.core.Bot;
import net.dv8tion.jda.api.entities.User;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RetrieveUser extends AsyncEffect {

    static {
        Skript.registerEffect(
                RetrieveUser.class,
                "retrieve user (with|from) id %string% [(with|using) [bot] %-bot%] and store (it|the user) in %~objects%"
        );
    }

    private Expression<String> exprInput;
    private Expression<Bot> exprBot;
    private Expression<Object> exprResult;

    @Override
    public boolean init(Expression<?>[] expressions, int i, @NotNull Kleenean kleenean, SkriptParser.@NotNull ParseResult parseResult) {
        getParser().setHasDelayBefore(Kleenean.TRUE);

        exprInput = (Expression<String>) expressions[0];
        exprBot = (Expression<Bot>) expressions[1];
        exprResult = (Expression<Object>) expressions[2];

        return Changer.ChangerUtils.acceptsChange(exprResult, Changer.ChangeMode.SET, User.class);
    }

    @Override
    protected void execute(@NotNull Event event) {
        final String input = exprInput.getSingle(event);
        final Bot bot = exprBot == null ? DiSky.getManager().findAny() : exprBot.getSingle(event);

        if (input == null || bot == null)
            return;

        final User user;
        try {
            user = bot.getInstance().retrieveUserById(input).complete();
        } catch (Exception ex) {
            DiSkyRuntimeHandler.error((Exception) ex);
            return;
        }

        exprResult.change(event, new User[] {user}, Changer.ChangeMode.SET);
    }

    @Override
    public @NotNull String toString(@Nullable Event event, boolean b) {
        return "retrieve user with id " + exprInput.toString(event, b) + " and store it in " +
                exprResult.toString(event, b);
    }
}
