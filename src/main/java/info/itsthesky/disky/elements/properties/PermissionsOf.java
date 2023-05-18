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
import info.itsthesky.disky.api.skript.EasyElement;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.*;
import net.dv8tion.jda.api.entities.channel.attribute.*;
import net.dv8tion.jda.api.entities.channel.middleman.*;
import net.dv8tion.jda.api.entities.channel.concrete.*;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

@Name("Discord Permissions Of")
@Description({"Get or change the permissions of a specific member or role in an optional channel."})
@Examples({
        "add (manage server) to permissions of event-member in event-channel",
        "remove (administrator) from permissions of event-role"
})
@Since("4.0.0")
public class PermissionsOf extends SimpleExpression<Permission> {

    static {
        Skript.registerExpression(PermissionsOf.class,
                Permission.class,
                ExpressionType.COMBINED,
                "permissions of %member/role% [in %-channel%]");
    }


    private Expression<IPermissionHolder> exprHolder;
    private Expression<GuildChannel> exprChannel;

    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull SkriptParser.ParseResult parseResult) {
        exprHolder = (Expression<IPermissionHolder>) exprs[0];
        exprChannel = (Expression<GuildChannel>) exprs[1];
        return true;
    }

    @Override
    public Class<?> @NotNull [] acceptChange(@NotNull Changer.ChangeMode mode) {
        return EasyElement.equalAny(mode, Changer.ChangeMode.ADD, Changer.ChangeMode.REMOVE) ? new Class[]{ Permission.class, Permission[].class } : new Class[0];
    }

    @Override
    public void change(@NotNull Event e, @NotNull Object[] delta, @NotNull Changer.ChangeMode mode) {
        if (!EasyElement.isValid(delta))
            return;
        final IPermissionHolder holder = EasyElement.parseSingle(exprHolder, e, null);
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
    protected Permission @NotNull [] get(@NotNull Event e) {
        final IPermissionHolder holder = EasyElement.parseSingle(exprHolder, e, null);
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
    public @NotNull Class<? extends Permission> getReturnType() {
        return Permission.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "permissions of " + exprHolder.toString(e, debug) + (exprChannel != null ? " in " + exprChannel.toString(e, debug) : "");
    }
}
