package net.itsthesky.disky.elements.effects.find;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.config.Node;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.util.AsyncEffect;
import ch.njol.util.Kleenean;
import net.itsthesky.disky.DiSky;
import net.itsthesky.disky.core.SkriptUtils;
import net.itsthesky.disky.elements.sections.handler.DiSkyRuntimeHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FindMembersWithRoles extends AsyncEffect {

    static {
        Skript.registerEffect(
            FindMembersWithRoles.class,
            "find [the] [discord] member[s] with [the] role[s] %roles% and store (them|the member[s]) in %~objects%"
        );
    }

    private Expression<Role> exprRoles;
    private Expression<Object> exprResult;
    private Node node;

    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.@NotNull ParseResult parseResult) {
        getParser().setHasDelayBefore(Kleenean.TRUE);

        exprRoles = (Expression<Role>) expressions[0];
        exprResult = (Expression<Object>) expressions[1];

        node = getParser().getNode();

        return Changer.ChangerUtils.acceptsChange(exprResult, Changer.ChangeMode.SET, Member[].class);
    }

    @Override
    protected void execute(@NotNull Event event) {
        final Object[] rolesObjs = exprRoles.getArray(event);
        if (rolesObjs.length == 0)
            return;

        // First, parse all roles
        final Role[] roles = new Role[rolesObjs.length];
        for (int i = 0; i < rolesObjs.length; i++) {
            final Object obj = rolesObjs[i];
            if (obj == null) {
                SkriptUtils.error(node, "All provided roles must be a role! (one of them is null)");
                return;
            }

            if (!(obj instanceof Role)) {
                SkriptUtils.error(node, "All provided roles must be a role! (one of them is not a role)");
                return;
            }

            roles[i] = (Role) obj;
        }

        // Be sure all roles are in the same guild
        @Nullable Guild guild = null;
        for (Role role : roles) {
            if (guild == null) {
                guild = role.getGuild();
                continue;
            }

            if (!role.getGuild().equals(guild)) {
                SkriptUtils.error(node, "All provided roles must be in the same guild!");
                return;
            }
        }

        final List<Member> members;
        try {
            members = guild.findMembersWithRoles(roles).get();
        } catch (Exception e) {
            DiSkyRuntimeHandler.error((Exception) e);
            return;
        }

        exprResult.change(event, members.toArray(new Member[0]), Changer.ChangeMode.SET);
    }

    @Override
    public @NotNull String toString(@Nullable Event event, boolean debug) {
        return "find members with roles " + exprRoles.toString(event, debug) + " and store them in " + exprResult.toString(event, debug);
    }

}
