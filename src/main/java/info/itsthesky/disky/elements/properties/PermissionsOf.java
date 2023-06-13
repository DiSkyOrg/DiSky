package info.itsthesky.disky.elements.properties;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.api.ReflectionUtils;
import info.itsthesky.disky.api.skript.EasyElement;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.IPermissionHolder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

@Name("Discord Permissions Of")
@Description({"Get or change the permissions of a specific member or role in an optional channel."})
@Examples({
        "add (manage server) to permissions of event-member in event-channel",
        "remove (administrator) from permissions of event-role"
})
@Since("4.0.0")
public class PermissionsOf extends SimpleExpression<Object> {

    static {
        try {
            ReflectionUtils.removeElement("ch.njol.skript.expressions.ExprPermissions",
                    "expressions");
            DiSky.debug("Removed original 'permissions' expression, to replace it with a new one.");
        } catch (Exception e) {
            Skript.error("DiSky were unable to remove the original 'permissions' expression, please report this error to the developer, with the following error.");
            e.printStackTrace();
        }

        Skript.registerExpression(PermissionsOf.class,
                Object.class,
                ExpressionType.PROPERTY,
                "[(all [[of] the]|the)] permissions of %member/role/player% [in %-channel/guildchannel%]",
                "[(all [[of] the]|the)] %member/role/player%'s permissions [in %-channel/guildchannel%]"
        );
    }


    private Expression<Object> exprHolder;
    private Expression<GuildChannel> exprChannel;

    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull SkriptParser.ParseResult parseResult) {
        exprHolder = (Expression<Object>) exprs[0];
        exprChannel = (Expression<GuildChannel>) exprs[1];
        return true;
    }

    @Override
    public Class<?> @NotNull [] acceptChange(@NotNull Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.ADD || mode == Changer.ChangeMode.REMOVE)
            return new Class[] {Permission[].class};
        return new Class[0];
    }

    @Override
    public void change(@NotNull Event e, @NotNull Object[] delta, @NotNull Changer.ChangeMode mode) {
        if (!EasyElement.isValid(delta))
            return;

        final IPermissionHolder holder = (IPermissionHolder) EasyElement.parseSingle(exprHolder, e, null);
        final @Nullable GuildChannel channel = EasyElement.parseSingle(exprChannel, e, null);
        final Permission[] perms = (Permission[]) delta;
        if (EasyElement.anyNull(this, holder, perms))
            return;
        if (channel != null && !(channel instanceof GuildChannel))
            return;
        switch (mode) {
            case ADD:
                if (holder instanceof Role && channel == null)
                    ((Role) holder).getManager().givePermissions(perms).queue();
                if (channel != null)
                    ((GuildChannel) channel).getPermissionContainer().upsertPermissionOverride(holder).grant(perms).queue();
                break;
            case REMOVE:
                if (holder instanceof Role && channel == null)
                    ((Role) holder).getManager().revokePermissions(perms).queue();
                if (channel != null)
                    ((GuildChannel) channel).getPermissionContainer().upsertPermissionOverride(holder).deny(perms).queue();
                break;
        }
    }

    @Override
    protected Object @NotNull [] get(@NotNull Event e) {
        final Object[] objects = EasyElement.parseList(exprHolder, e, null);
        if (objects == null)
            return new Permission[0];

        // ============ Skript's default permissions ============
        final Set<Player> players = new HashSet<>();
        for (Object object : objects)
            if (object instanceof Player)
                players.add((Player) object);
        if (!players.isEmpty()) {
            final Set<String> permissions = new HashSet<>();
            for (Player player : players)
                for (final PermissionAttachmentInfo permission : player.getEffectivePermissions())
                    permissions.add(permission.getPermission());
            return permissions.toArray(new String[0]);
        }

        // ============ Discord permissions ============
        final IPermissionHolder holder = (IPermissionHolder) EasyElement.parseSingle(exprHolder, e, null);
        final @Nullable GuildChannel channel = EasyElement.parseSingle(exprChannel, e, null);
        if (EasyElement.anyNull(this, holder))
            return new Permission[0];
        if (channel != null && !(channel instanceof GuildChannel))
            return new Permission[0];
        if (channel == null)
            return holder.getPermissions().toArray(new Permission[0]);
        return holder.getPermissions((GuildChannel) channel).toArray(new Permission[0]);
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public @NotNull Class<?> getReturnType() {
        return Object.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "discord permissions of " + exprHolder.toString(e, debug) + (exprChannel != null ? " in " + exprChannel.toString(e, debug) : "");
    }
}
