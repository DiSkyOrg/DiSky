package info.itsthesky.disky.elements.datastructs;

import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.util.Color;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.api.datastruct.ChainDataStructElement;
import info.itsthesky.disky.api.datastruct.DataStructureEntry;
import info.itsthesky.disky.api.datastruct.base.ChainDS;
import info.itsthesky.disky.api.datastruct.base.EditableDataStructure;
import info.itsthesky.disky.api.skript.EasyElement;
import info.itsthesky.disky.core.SkriptUtils;
import info.itsthesky.disky.elements.sections.handler.DiSkyRuntimeHandler;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.managers.RoleManager;
import net.dv8tion.jda.api.requests.restaction.RoleAction;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RoleDS extends ChainDataStructElement<Role, RoleDS.RoleWrapper, RoleDS.RoleStructure> {

    static {
        Skript.registerExpression(
                RoleDS.class,
                Role.class,
                ExpressionType.SIMPLE,
                "[a] new role in [the] [guild] %guild%",
                "[a] new role (using|copying) %role% in [the] [guild] %guild%"
        );
    }

    private Expression<Guild> exprGuild;
    private Expression<Role> exprRole;
    private boolean copy;

    @Override
    public boolean init(Expression<?>[] expressions, int pattern, Kleenean delayed, SkriptParser.ParseResult result, @Nullable SectionNode node, @Nullable List<TriggerItem> triggerItems) {
        if (!super.init(expressions, pattern, delayed, result, node, triggerItems))
            return false;
        copy = pattern == 1;

        if (copy) {
            exprRole = (Expression<Role>) expressions[0];
            exprGuild = (Expression<Guild>) expressions[1];
        } else {
            exprGuild = (Expression<Guild>) expressions[0];
        }

        return true;
    }

    @Override
    public RoleWrapper getOriginalInstance(@NotNull Event event) {
        final Guild guild = EasyElement.parseSingle(exprGuild, event);
        final Role role = EasyElement.parseSingle(exprRole, event);

        if (guild == null) {
            DiSkyRuntimeHandler.exprNotSet(node, exprGuild);
            return null;
        }

        if (copy && role == null) {
            DiSkyRuntimeHandler.exprNotSet(node, exprRole);
            return null;
        }

        if (copy && !role.getGuild().getId().equals(guild.getId())) {
            DiSkyRuntimeHandler.error(new IllegalStateException("Cannot copy a role from a different guild! Got role from guild '"+role.getGuild().getName()+"' and trying to copy it into '"+guild.getName()+"'!"), node);
            return null;
        }

        return new RoleWrapper(copy ? guild.createCopyOfRole(role) : guild.createRole());
    }

    @Override
    public Role applyChanges(@NotNull Event event, @NotNull RoleWrapper edited) {
        return edited.getAction().complete();
    }

    //region [ META ]

    @Override
    public Class<RoleStructure> getDataStructClass() {
        return RoleStructure.class;
    }

    @Override
    public Class<? extends Role> getReturnType() {
        return Role.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "role data structure";
    }

    //endregion

    public static class RoleStructure implements ChainDS<RoleWrapper>, EditableDataStructure<RoleWrapper, RoleStructure> {

        @DataStructureEntry(value = "name")
        public String name;

        @DataStructureEntry(value = "color")
        public Color color;

        @DataStructureEntry(value = "hoist")
        public Boolean hoist;

        @DataStructureEntry(value = "mentionable")
        public Boolean mentionable;

        @DataStructureEntry(value = "permissions")
        public List<Permission> permissions;

        @Override
        public RoleWrapper edit(RoleWrapper original) {
            if (name != null)
                original.setName(name);

            if (color != null)
                original.setColor(color);

            if (hoist != null)
                original.setHoisted(hoist);

            if (mentionable != null)
                original.setMentionable(mentionable);

            if (permissions != null)
                original.setPermissions(permissions);

            return original;
        }

        @Override
        public List<Class<?>> getAcceptedClasses() {
            return List.of(Role.class);
        }

        @Override
        public RoleWrapper convert(@NotNull Object object) {
            if (!(object instanceof Role))
                throw new IllegalArgumentException("The object to convert must be a role!");

            return new RoleWrapper(((Role) object).getManager());
        }
    }

    public static class RoleWrapper {

        private @Nullable RoleAction action;
        private @Nullable RoleManager manager;

        private RoleWrapper(@Nullable RoleAction action, @Nullable RoleManager manager) {
            this.action = action;
            this.manager = manager;
        }

        public RoleWrapper(@NotNull RoleAction action) {
            this(action, null);
        }

        public RoleWrapper(@NotNull RoleManager manager) {
            this(null, manager);
        }

        //region Usual Methods

        public void setName(String name) {
            if (action != null)
                action.setName(name);
            else if (manager != null)
                manager.setName(name);
        }

        public void setColor(Color color) {
            if (action != null)
                action.setColor(SkriptUtils.convert(color));
            else if (manager != null)
                manager.setColor(SkriptUtils.convert(color));
        }

        public void setHoisted(boolean hoist) {
            if (action != null)
                action.setHoisted(hoist);
            else if (manager != null)
                manager.setHoisted(hoist);
        }

        public void setMentionable(boolean mentionable) {
            if (action != null)
                action.setMentionable(mentionable);
            else if (manager != null)
                manager.setMentionable(mentionable);
        }

        public void setPermissions(List<Permission> permissions) {
            if (action != null)
                action.setPermissions(permissions);
            else if (manager != null)
                manager.setPermissions(permissions);
        }

        public @Nullable RoleManager getManager() {
            return manager;
        }

        public @Nullable RoleAction getAction() {
            return action;
        }
    }

}
