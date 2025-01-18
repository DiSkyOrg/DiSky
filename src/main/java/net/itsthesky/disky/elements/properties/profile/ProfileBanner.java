package net.itsthesky.disky.elements.properties.profile;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.config.Node;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import net.itsthesky.disky.core.Bot;
import net.itsthesky.disky.core.SkriptUtils;
import net.itsthesky.disky.elements.changers.IAsyncChangeableExpression;
import net.itsthesky.disky.elements.sections.handler.DiSkyRuntimeHandler;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.managers.AccountManager;
import net.dv8tion.jda.api.requests.RestAction;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Profile Banner")
@Description({"Get the profile banner URL. If the user doesn't have a custom banner, this will return none.",
"Use the 'profile color' expression to get the color instead of the banner URL in that case!"})
public class ProfileBanner extends SimplePropertyExpression<Object, String>
        implements IAsyncChangeableExpression {

    static {
        register(
                ProfileBanner.class,
                String.class,
                "profile banner [ur(l|i)]",
                "userprofile/bot"
        );
    }

    private Node node;

    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.@NotNull ParseResult parseResult) {
        node = getParser().getNode();
        return super.init(expressions, matchedPattern, isDelayed, parseResult);
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "profile banner";
    }

    @Override
    public @Nullable String convert(Object profile) {
        if (profile instanceof User.Profile)
            return ((User.Profile) profile).getBannerUrl();
        if (profile instanceof Bot)
            DiSkyRuntimeHandler.error(new Exception("Cannot retrieve profile banner from a bot, retrieve its profile first."), node);
        return null;
    }

    @Override
    public @NotNull Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public Class<?> @NotNull [] acceptChange(Changer.@NotNull ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET)
            return new Class[] {String.class};

        return new Class[0];
    }

    @Override
    public void change(@NotNull Event event, Object @NotNull [] delta, Changer.@NotNull ChangeMode mode) {
        change(event, delta, mode, false);
    }

    @Override
    public void changeAsync(Event e, Object[] delta, Changer.ChangeMode mode) {
        change(e, delta, mode, true);
    }

    public void change(Event event, Object[] delta, Changer.ChangeMode mode, boolean async) {
        if (delta == null || delta.length == 0 || delta[0] == null)
            return;
        if (mode != Changer.ChangeMode.SET)
            return;

        final String bannerInput = delta[0].toString();
        final Icon banner = SkriptUtils.parseIcon(bannerInput);
        if (banner == null) {
            DiSkyRuntimeHandler.error(new Exception("Cannot parse the given banner input! Must be valid URL or file path."), node);
            return;
        }

        for (Object entity : getExpr().getArray(event)) {
            if (entity instanceof Bot) {
                final AccountManager manager = ((Bot) entity).getInstance().getSelfUser().getManager();
                final RestAction<Void> action = manager.setBanner(banner);
                try {
                    if (async) action.complete();
                    else action.queue();
                } catch (Exception ex) {
                    DiSkyRuntimeHandler.error(ex, node);
                }
            } else if (entity instanceof User) {
                DiSkyRuntimeHandler.error(new Exception("Cannot change the profile banner of a user."), node);
            }
        }
    }
}
