package info.itsthesky.disky.elements.properties.members;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MemberVoiceChannel extends MemberProperty<AudioChannel> {

    static {
        register(
                MemberVoiceChannel.class,
                AudioChannel.class,
                "(voice|audio) channel"
        );
    }

    @Override
    public @Nullable AudioChannel convert(Member member) {
        return member.getVoiceState().getChannel();
    }

    @Override
    public @NotNull Class<? extends AudioChannel> getReturnType() {
        return AudioChannel.class;
    }
}
