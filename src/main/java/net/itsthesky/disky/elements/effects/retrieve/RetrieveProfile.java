package net.itsthesky.disky.elements.effects.retrieve;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.util.AsyncEffect;
import ch.njol.util.Kleenean;
import net.itsthesky.disky.DiSky;
import net.dv8tion.jda.api.entities.User;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Retrieve Profile")
@Description({"Retrieve the profile of the specified user.",
        "Profile represent mainly the banner of the user, could return the accent color if non set."})
public class RetrieveProfile extends AsyncEffect {

    static {
        Skript.registerEffect(
                RetrieveProfile.class,
                "retrieve profile (with|from) id %string% (from|with|of|in) %user% and store (it|the profile) in %~object%"
        );
    }

    private Expression<String> exprID;
    private Expression<User> exprUser;
    private Expression<Object> exprResult;

    @Override
    public boolean init(Expression<?>[] expressions, int i, @NotNull Kleenean kleenean, SkriptParser.@NotNull ParseResult parseResult) {
        getParser().setHasDelayBefore(Kleenean.TRUE);

        exprID = (Expression<String>) expressions[0];
        exprUser = (Expression<User>) expressions[1];
        exprResult = (Expression<Object>) expressions[2];
        return Changer.ChangerUtils.acceptsChange(exprResult, Changer.ChangeMode.SET, User.Profile.class);
    }

    @Override
    protected void execute(@NotNull Event event) {
        String id = exprID.getSingle(event);
        User user = exprUser.getSingle(event);
        if (id == null || user == null)
            return;

        final User.Profile profile;
        try {
            profile = user.retrieveProfile().complete();
        } catch (Exception ex) {
            DiSky.getErrorHandler().exception(event, ex);
            return;
        }

        exprResult.change(event, new User.Profile[] {profile}, Changer.ChangeMode.SET);
    }

    @Override
    public @NotNull String toString(@Nullable Event event, boolean b) {
        return "retrieve profile (with|from) id " + exprID.toString(event, b) + " (from|with|of|in) " + exprUser.toString(event, b) + " and store (it|the profile) in " + exprResult.toString(event, b);
    }
}
