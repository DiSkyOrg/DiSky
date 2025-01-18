package net.itsthesky.disky.elements.effects.retrieve;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.util.AsyncEffect;
import ch.njol.util.Kleenean;
import net.itsthesky.disky.DiSky;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RetrieveOwner extends AsyncEffect {

    static {
        Skript.registerEffect(
                RetrieveOwner.class,
                "retrieve [the] owner (of|from) %guild% and store (it|the member) in %~object%"
        );
    }

    private Expression<Guild> exprGuild;
    private Expression<Object> exprResult;

    @Override
    public boolean init(Expression<?>[] expressions, int i, @NotNull Kleenean kleenean, SkriptParser.@NotNull ParseResult parseResult) {
        getParser().setHasDelayBefore(Kleenean.TRUE);

        exprGuild = (Expression<Guild>) expressions[0];
        exprResult = (Expression<Object>) expressions[1];
        return Changer.ChangerUtils.acceptsChange(exprResult, Changer.ChangeMode.SET, Member.class);
    }

    @Override
    protected void execute(@NotNull Event event) {
        Guild guild = exprGuild.getSingle(event);
        if (guild == null)
            return;

        final Member owner;
        try {
            owner = guild.retrieveOwner().complete();
        } catch (Exception ex) {
            DiSky.getErrorHandler().exception(event, ex);
            return;
        }

        exprResult.change(event, new Member[] {owner}, Changer.ChangeMode.SET);
    }

    @Override
    public @NotNull String toString(@Nullable Event event, boolean b) {
        return "retrieve owner of guild " + exprGuild.toString(event, b) + " and store it in " + exprResult.toString(event, b);
    }
}
