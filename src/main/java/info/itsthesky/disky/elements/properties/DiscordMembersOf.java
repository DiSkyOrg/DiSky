package info.itsthesky.disky.elements.properties;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import info.itsthesky.disky.api.skript.MultiplyPropertyExpression;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.IMemberContainer;
import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Discord Members of Guild / Channel")
@Description({"Returns a list of members.",
"For Message text-related channel & category, it returns members with permission to view the channel",
"For Audio Channels it returns the currently connected members of the channel."})
@Examples({"members of event-channel",
        "members of voice channel with id \"0000\""})
public class DiscordMembersOf extends MultiplyPropertyExpression<Object, Member> {

    static {
        register(
                DiscordMembersOf.class,
                Member.class,
                "discord member[s] [list]",
                "guildchannel/guild"
        );
    }

    @Override
    public @Nullable Member[] convert(Object entity) {
        if (entity instanceof IMemberContainer)
            return ((IMemberContainer) entity).getMembers().toArray(new Member[0]);
        if (entity instanceof Guild)
            return ((Guild) entity).getMembers().toArray(new Member[0]);
        return new Member[0];
    }

    @Override
    public @NotNull Class<? extends Member> getReturnType() {
        return Member.class;
    }

    @Override
    protected String getPropertyName() {
        return "discord members of ";
    }}
