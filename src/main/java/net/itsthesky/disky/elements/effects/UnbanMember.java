package net.itsthesky.disky.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.config.Node;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import net.itsthesky.disky.api.generator.SeeAlso;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.AsyncEffect;
import ch.njol.util.Kleenean;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.itsthesky.disky.api.skript.EasyElement;
import net.itsthesky.disky.elements.sections.handler.DiSkyRuntimeHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Unban User")
@Description("Unbans a user from a guild.")
@Examples("unban event-user in guild with id \"818182471140114432\"")
@Since("4.0.0")
@SeeAlso({Guild.class, User.class})
public class UnbanMember extends AsyncEffect {

    static {
        Skript.registerEffect(
                UnbanMember.class,
                "[discord] un[-| ]ban [the] [discord] [user] %user/string% (from|in) [guild] %guild%"
        );
    }

    private Expression<Object> exprTarget;
    private Expression<Guild> exprGuild;
    private Node node;

    @Override
    public boolean init(Expression[] expr, int i, Kleenean kleenean, ParseResult parseResult) {
        exprTarget = (Expression<Object>) expr[0];
        exprGuild = (Expression<Guild>) expr[1];
        node = getParser().getNode();
        return true;
    }

    @Override
    public void execute(@NotNull Event e) {
        final Object target = EasyElement.parseSingle(exprTarget, e, null);
        final Guild guild = EasyElement.parseSingle(exprGuild, e, null);

        if (!DiSkyRuntimeHandler.checkSet(node, target, exprTarget, guild, exprGuild))
            return;

        if (!(target instanceof UserSnowflake) && !(target instanceof String)) {
            DiSkyRuntimeHandler.error(new IllegalArgumentException("The given object is not a discord user or a string (user id)!"), node);
            return;
        }

        final UserSnowflake user = target instanceof UserSnowflake
                ? (UserSnowflake) target
                : UserSnowflake.fromId((String) target);

        try {
            guild.unban(user).complete();
        } catch (Exception ex) {
            DiSkyRuntimeHandler.error(ex, node);
        }
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "unban " + exprTarget.toString(e, debug) + " from guild " + exprGuild.toString(e, debug);
    }
}