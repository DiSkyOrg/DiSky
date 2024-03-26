package info.itsthesky.disky.elements.properties.channels;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.api.skript.action.ActionProperty;
import info.itsthesky.disky.elements.properties.bot.SelfMember;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.attribute.IVoiceStatusChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ChannelStatus extends ActionProperty<GuildChannel, ChannelAction, String> {

    static {
        register(
                ChannelStatus.class,
                String.class,
                "[voice] channel status",
                "channel"
        );
    }


    @Override
    public Class<?>[] acceptChange(Changer.ChangeMode mode) {
        return mode.equals(Changer.ChangeMode.SET) ? new Class[] {String.class} : new Class[0];
    }

    @Override
    public void change(GuildChannel channel, String value) {
        if (!(channel instanceof IVoiceStatusChannel)) {
            Skript.error("The channel status can only be applied on voice channel!");
            return;
        }

        if (value.length() >= 500) {
            DiSky.runtimeError("The channel status cannot be more than 500 characters long!",
                    "Channel Status", value,
                    "Length", value.length() + " characters");
            return;
        }
        final Member selfMember = channel.getGuild().getSelfMember();

        if (channel.getGuild().getAudioManager().getConnectedChannel() == null
                && !selfMember.hasPermission(Permission.MANAGE_SERVER)) {
            DiSky.runtimeError("DiSky cannot set the channel status without the MANAGE_SERVER permission (or without being connected to a voice channel)!",
                    "Target Channel", channel.getName() + " [" + channel.getId() + "]",
                    "Permission Required", "MANAGE_SERVER");
            return;
        }

        final AudioChannel connectedChannel = channel.getGuild().getAudioManager().getConnectedChannel();
        if (connectedChannel != null) {
            if (!selfMember.hasPermission(Permission.VOICE_SET_STATUS)
                    && connectedChannel.getMembers().contains(selfMember)) {
                DiSky.runtimeError("DiSky cannot set the channel status without the VOICE_SET_STATUS permission!",
                        "Target Channel", channel.getName() + " [" + channel.getId() + "]",
                        "Permission Required", "VOICE_SET_STATUS");
                return;
            }
        }

        ((IVoiceStatusChannel) channel).modifyStatus(value).queue();
    }

    @Override
    public ChannelAction change(ChannelAction action, String value) {
        DiSky.getInstance().getLogger().warning("Cannot change the status of a voice channel before its creation.");
        return action;
    }

    @Override
    public String get(GuildChannel role) {
        return ((VoiceChannel) role).getStatus();
    }

    @Override
    public @NotNull Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "channel status";
    }
}
