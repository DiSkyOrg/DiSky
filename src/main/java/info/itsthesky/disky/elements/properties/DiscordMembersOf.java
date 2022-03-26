package info.itsthesky.disky.elements.properties;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import info.itsthesky.disky.api.skript.MultiplyPropertyExpression;
import net.dv8tion.jda.api.entities.*;
import org.jetbrains.annotations.Nullable;

@Name("Discord Members of ")
@Description({"Returns a list of members.",
"\nFor Message Channels/category it returns members with permission to view the channel",
"\nFor Audio Channels it returns the currently connected members of the channel."})
@Examples({"discord members of event-channel",
        "discord members of voice channel with id \"0000\""})
public class DiscordMembersOf extends MultiplyPropertyExpression<Object, Member> {

    static {
        register(
                DiscordMembersOf.class,
                Member.class,
                "discord member[s] [list]",
                "voicechannel/textchannel/thread/category/guild"
        );
    }

    @Override
    public @Nullable Member[] convert(Object entity) {
        if (entity instanceof VoiceChannel)
            return ((VoiceChannel) entity).getMembers().toArray(new Member[0]);
        if (entity instanceof TextChannel)
            return ((TextChannel) entity).getMembers().toArray(new Member[0]);
        if (entity instanceof Category)
            return ((Category) entity).getMembers().toArray(new Member[0]);
        if (entity instanceof Guild)
            return ((Guild) entity).getMembers().toArray(new Member[0]);
        if (entity instanceof ThreadChannel)
            return ((ThreadChannel) entity).getMembers().toArray(new Member[0]);
        return new Member[0];
    }

    @Override
    public Class<? extends Member> getReturnType() {
        return Member.class;
    }

    @Override
    protected String getPropertyName() {
        return "discord members of ";
    }}