package info.itsthesky.disky.elements.properties;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.coll.CollectionUtils;
import info.itsthesky.disky.api.changers.MultipleChangeablePropertyExpression;
import info.itsthesky.disky.api.skript.EasyElement;
import info.itsthesky.disky.core.Bot;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

@Name("Guild / Member Roles")
@Description({"Represent the roles that a guild or a member currently have.",
        "Can be changed, SET and ADD ChangeMode can be used when passing a member.",
        "To modify guild's roles, check delete and create role effects."})
@Examples({"add role with id \"000\" to roles of event-member",
        "remove event-role from roles of event-member",
        "reply with \"Amount of roles in the guild: %size of roles of event-guild%\""})
public class DiscordRoles extends MultipleChangeablePropertyExpression<Object, Role> {

    static {
        register(
                DiscordRoles.class,
                Role.class,
                "roles",
                "guild/member"
        );
    }

    @Override
    public void change(Event e, @NotNull Object[] delta, Bot bot, Changer.ChangeMode mode) {
        final Role role = bot.getInstance().getRoleById(((Role) delta[0]).getId());
        final Object entity = getExpr().getSingle(e);
        if (EasyElement.anyNull(role, entity))
            return;

        if (!(entity instanceof Member))
            return;
        if (mode == Changer.ChangeMode.ADD)
            ((Member) entity).getGuild().addRoleToMember((Member) entity, role).queue();
        else
            ((Member) entity).getGuild().removeRoleFromMember((Member) entity, role).queue();
    }

    @Override
    protected Role[] convert(Object entity) {
        if (entity instanceof Member)
            return ((Member) entity).getRoles().toArray(new Role[0]);
        if (entity instanceof Guild)
            return ((Guild) entity).getRoles().toArray(new Role[0]);
        return new Role[0];
    }

    @Override
    public Class<?> @NotNull [] acceptChange(@NotNull Changer.ChangeMode mode) {
        if (EasyElement.equalAny(mode, Changer.ChangeMode.ADD, Changer.ChangeMode.REMOVE))
            return CollectionUtils.array(Role.class);
        return new Class[0];
    }

    @Override
    public @NotNull Class<? extends Role> getReturnType() {
        return Role.class;
    }

    @Override
    protected String getPropertyName() {
        return "roles";
    }
}
