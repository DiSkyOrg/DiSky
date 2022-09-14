package info.itsthesky.disky.elements.events.thread;

import info.itsthesky.disky.api.events.DiSkyEvent;
import info.itsthesky.disky.api.events.SimpleDiSkyEvent;
import info.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.*;
import net.dv8tion.jda.api.entities.channel.attribute.*;
import net.dv8tion.jda.api.entities.channel.middleman.*;
import net.dv8tion.jda.api.entities.channel.concrete.*;
import net.dv8tion.jda.api.events.thread.member.ThreadMemberJoinEvent;

public class ThreadJoinEvent extends DiSkyEvent<ThreadMemberJoinEvent> {

    static {
        register("Thread Join Event", ThreadJoinEvent.class, BukkitThreadJoinEvent.class,
                "[discord] thread join")
                .description("Fired when a member joins a tread, either by joining itself or by a moderator can be used to get the thread, the guild and the member.")
                .examples("on thread join:");


        SkriptUtils.registerBotValue(ThreadJoinEvent.BukkitThreadJoinEvent.class);

        SkriptUtils.registerValue(ThreadJoinEvent.BukkitThreadJoinEvent.class, Member.class,
                event -> event.getJDAEvent().getMember(), 0);

        SkriptUtils.registerValue(ThreadJoinEvent.BukkitThreadJoinEvent.class, ThreadChannel.class,
                event -> event.getJDAEvent().getThread(), 0);

        SkriptUtils.registerValue(ThreadJoinEvent.BukkitThreadJoinEvent.class, Guild.class,
                event -> event.getJDAEvent().getGuild(), 0);

    }

    public static class BukkitThreadJoinEvent extends SimpleDiSkyEvent<ThreadMemberJoinEvent> {
        public BukkitThreadJoinEvent(ThreadJoinEvent event) {
        }
    }
}
