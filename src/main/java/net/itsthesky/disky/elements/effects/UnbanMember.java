package net.itsthesky.disky.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.config.Node;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.AsyncEffect;
import ch.njol.util.Kleenean;
import net.itsthesky.disky.api.skript.EasyElement;
import net.itsthesky.disky.elements.sections.handler.DiSkyRuntimeHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Unban User")
@Description({"Unbans a user from a guild."})
@Examples({"unban event-user in guild with id \"818182471140114432\""})

public class UnbanMember extends AsyncEffect {

    static {
        Skript.registerEffect(
                UnbanMember.class,
                "[discord] un[-| ]ban [the] [discord] [user] %user% (from|in) [guild] %guild%"
        );
    }

    private Expression<User> exprUser;
    private Expression<Guild> exprGuild;
    private Node node;

    @Override
    public boolean init(Expression[] expr, int i, Kleenean kleenean, ParseResult parseResult) {
        exprUser = (Expression<User>) expr[0];
        exprGuild = (Expression<Guild>) expr[1];
        node = getParser().getNode();
        return true;
    }

    @Override
    public void execute(@NotNull Event e) {
        final User user = EasyElement.parseSingle(exprUser, e, null);
        final Guild guild = EasyElement.parseSingle(exprGuild, e, null);

        if (user == null || guild == null) {
            DiSkyRuntimeHandler.error(new NullPointerException("The user or the guild cannot be null!"), node);
            return;
        }

        try {
            guild.unban(user).complete();
        } catch (Exception ex) {
            DiSkyRuntimeHandler.error(ex, node);
        }
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "unbanned " + exprUser.toString(e, debug) + " from guild " + exprGuild.toString(e, debug);
    }
}