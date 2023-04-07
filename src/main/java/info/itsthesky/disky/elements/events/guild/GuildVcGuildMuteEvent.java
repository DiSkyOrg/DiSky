package info.itsthesky.disky.elements.events.guild;

import info.itsthesky.disky.api.events.DiSkyEvent;
import info.itsthesky.disky.api.events.SimpleDiSkyEvent;
import info.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceGuildMuteEvent;

public class GuildVcGuildMuteEvent extends DiSkyEvent<GuildVoiceGuildMuteEvent> {

    static {
        register("Guild Voice Guild Mute Event", GuildVcGuildMuteEvent.class, EvtGuildVcGuildMute.class,
                "[discord] guild [voice] guild mute")
                .description("Fired when a member is muted or unmuted by the guild.")
                .examples("on guild voice guild mute:");

        SkriptUtils.registerBotValue(EvtGuildVcGuildMute.class);
        SkriptUtils.registerAuthorValue(EvtGuildVcGuildMute.class, e -> e.getJDAEvent().getGuild());

        SkriptUtils.registerValue(EvtGuildVcGuildMute.class, Boolean.class,
                event -> event.getJDAEvent().isGuildMuted(), 0);
        SkriptUtils.registerValue(EvtGuildVcGuildMute.class, Member.class,
                event -> event.getJDAEvent().getMember(), 0);
    }

    public static class EvtGuildVcGuildMute extends SimpleDiSkyEvent<GuildVoiceGuildMuteEvent> {
        public EvtGuildVcGuildMute(GuildVcGuildMuteEvent event) { }
    }
}
