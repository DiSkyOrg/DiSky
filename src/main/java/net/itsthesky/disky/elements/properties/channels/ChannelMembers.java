package net.itsthesky.disky.elements.properties.channels;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import net.itsthesky.disky.api.skript.MultiplyPropertyExpression;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;

@Name("Voice Channel Members")
@Description("The list of members that are connected to this actual voice channel.")
@Examples({"audio members of event-channel",
        "voice members of voice channel with id \"0000\""})
public class ChannelMembers extends MultiplyPropertyExpression<VoiceChannel, Member> {

    static {
        register(
                ChannelMembers.class,
                Member.class,
                "(audio|stage|voice) member[s] [list]",
                "voicechannel"
        );
    }

    @Override
    public Class<? extends Member> getReturnType() {
        return Member.class;
    }

    @Override
    protected String getPropertyName() {
        return "threads";
    }

    @Override
    protected Member[] convert(VoiceChannel t) {
        return t.getMembers().toArray(new Member[0]);
    }
}
