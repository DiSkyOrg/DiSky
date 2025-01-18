package net.itsthesky.disky.elements.events.guild;

import net.itsthesky.disky.api.events.DiSkyEvent;
import net.itsthesky.disky.api.events.SimpleDiSkyEvent;
import net.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceGuildMuteEvent;

public class GuildVoiceMuteEvent extends DiSkyEvent<GuildVoiceGuildMuteEvent> {

    static {
        register("Guild Voice Mute Event", GuildVoiceMuteEvent.class, EvtGuildVcGuildMute.class,
                "[discord] guild [voice] mute[d]")
                .description("Fired when a member is muted or unmuted by the guild.")
                .examples("on guild voice mute:");

        SkriptUtils.registerBotValue(EvtGuildVcGuildMute.class);
        SkriptUtils.registerAuthorValue(EvtGuildVcGuildMute.class, e -> e.getJDAEvent().getGuild());

        SkriptUtils.registerValue(EvtGuildVcGuildMute.class, Boolean.class,
                event -> event.getJDAEvent().isGuildMuted(), 0);
        SkriptUtils.registerValue(EvtGuildVcGuildMute.class, Member.class,
                event -> event.getJDAEvent().getMember(), 0);
    }

    public static class EvtGuildVcGuildMute extends SimpleDiSkyEvent<GuildVoiceGuildMuteEvent> {
        public EvtGuildVcGuildMute(GuildVoiceMuteEvent event) { }
    }
}
