package net.itsthesky.disky.elements.events.guild;

import net.itsthesky.disky.api.events.DiSkyEvent;
import net.itsthesky.disky.api.events.SimpleDiSkyEvent;
import net.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceGuildDeafenEvent;

public class GuildVoiceDeafenEvent extends DiSkyEvent<GuildVoiceGuildDeafenEvent> {
    static {
        register("Guild Voice Deafen Event", GuildVoiceDeafenEvent.class, BukkitGuildVoiceDeafenEvent.class,
                "[discord] guild [voice] deafen[ed]")
                .description("Fired when a member is deafened or undeafened by the guild.")
                .examples("on guild voice deafen:");

        SkriptUtils.registerBotValue(BukkitGuildVoiceDeafenEvent.class);
        SkriptUtils.registerAuthorValue(BukkitGuildVoiceDeafenEvent.class, e -> e.getJDAEvent().getGuild());

        SkriptUtils.registerValue(BukkitGuildVoiceDeafenEvent.class, Boolean.class,
                event -> event.getJDAEvent().isGuildDeafened(), 0);
        SkriptUtils.registerValue(BukkitGuildVoiceDeafenEvent.class, Member.class,
                event -> event.getJDAEvent().getMember(), 0);
    }

    public static class BukkitGuildVoiceDeafenEvent extends SimpleDiSkyEvent<GuildVoiceGuildDeafenEvent> {
        public BukkitGuildVoiceDeafenEvent(GuildVoiceDeafenEvent event) { }
    }
}