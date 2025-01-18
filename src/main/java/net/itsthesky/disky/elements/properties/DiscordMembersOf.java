package net.itsthesky.disky.elements.properties;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import net.itsthesky.disky.api.skript.MultiplyPropertyExpression;
import net.itsthesky.disky.elements.changers.IAsyncGettableExpression;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.attribute.IMemberContainer;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Discord Members of Guild / Channel")
@Description({"Returns a list of members.",
"For Message text-related channel & category, it returns members with permission to view the channel",
"For Audio Channels it returns the currently connected members of the channel.",
"For threads & posts, it returns the members who are in the thread. " +
        "You can add or remove a member in this case."})
@Examples({"members of event-channel",
        "members of voice channel with id \"0000\"",
"add event-member to discord members of thread channel with id \"000\""})
public class DiscordMembersOf extends MultiplyPropertyExpression<Object, Member> implements IAsyncGettableExpression<Member> {

    static {
        register(
                DiscordMembersOf.class,
                Member.class,
                "discord member[s] [list]",
                "guildchannel/guild/threadchannel"
        );
    }

    @Override
    public @Nullable Member[] convert(Object entity) {
        return get(entity, false);
    }

    @Override
    public Member[] getAsync(Event e) {
        final Object entity = getExpr().getSingle(e);
        if (entity == null)
            return new Member[0];

        return get(entity, true);
    }

    public Member[] get(Object entity, boolean async) {
        if (entity instanceof final IMemberContainer memberContainer)
            return memberContainer.getMembers().toArray(new Member[0]);

        if (entity instanceof final Guild guild) {
            if (async) {
                return guild.loadMembers().get().toArray(new Member[0]);
            } else {
                return guild.getMembers().toArray(new Member[0]);
            }
        }

        return new Member[0];
    }

    @Override
    public @NotNull Class<? extends Member> getReturnType() {
        return Member.class;
    }

    @Override
    public Class<?> @NotNull [] acceptChange(Changer.@NotNull ChangeMode mode) {
        if (mode == Changer.ChangeMode.ADD || mode == Changer.ChangeMode.REMOVE)
            return new Class[] {Member.class, Member[].class};
        return new Class[0];
    }

    @Override
    public void change(@NotNull Event e, Object @NotNull [] delta, Changer.@NotNull ChangeMode mode) {
        final Object entity = getExpr().getSingle(e);
        if (!(entity instanceof ThreadChannel))
            return;

        final Member[] members = (Member[]) delta;
        final ThreadChannel thread = (ThreadChannel) entity;

        if (members.length == 0)
            return;

        switch (mode) {
            case ADD:
                for (Member member : members)
                    thread.addThreadMember(member).queue();
                break;
            case REMOVE:
                for (Member member : members)
                    thread.removeThreadMember(member).queue();
                break;
            default:
                break;
        }
    }

    @Override
    protected String getPropertyName() {
        return "discord members";
    }
}
