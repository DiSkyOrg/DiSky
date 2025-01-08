package info.itsthesky.disky.elements.conditions;

import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.api.skript.EasyElement;
import info.itsthesky.disky.api.skript.EasyPropertyCondition;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Member Has Permissions")
@Description("Check if a member has permissions in an optional channel.")
@Examples({"if event-member has discord permission administrator: # global permission",
"if event-member has discord permission send message in event-channel: # channel specific permission"})
public class HasPermissions extends EasyPropertyCondition<Member> {

    static {
        register(
                HasPermissions.class,
                PropertyCondition.PropertyType.HAVE,
                "discord permission[s] %permissions% [in [the] [channel] %-guildchannel%]",
                "member"
        );
    }

    private Expression<Permission> exprPerms;
    private Expression<GuildChannel> exprChannel;

    @Override
    public boolean check(Event e, Member entity) {
        final Permission[] permissions = EasyElement.parseList(exprPerms, e, new Permission[0]);
        final @Nullable GuildChannel channel = EasyElement.parseSingle(exprChannel, e, null);
        if (permissions.length <= 0)
            return false;
        if (channel == null)
            return entity.hasPermission(permissions);
        else
            return entity.hasPermission(channel, permissions);
    }

    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull SkriptParser.ParseResult parseResult) {
        exprPerms = (Expression<Permission>) exprs[1];
        exprChannel = (Expression<GuildChannel>) exprs[2];
        return super.init(exprs, matchedPattern, isDelayed, parseResult);
    }
}
