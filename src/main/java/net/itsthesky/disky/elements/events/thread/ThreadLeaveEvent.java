package net.itsthesky.disky.elements.events.thread;

import net.itsthesky.disky.api.events.DiSkyEvent;
import net.itsthesky.disky.api.events.SimpleDiSkyEvent;
import net.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.events.thread.member.ThreadMemberLeaveEvent;

public class ThreadLeaveEvent extends DiSkyEvent<ThreadMemberLeaveEvent> {

    static {
        register("Thread Leave Event", ThreadLeaveEvent.class, BukkitThreadLeaveEvent.class,
                "[discord] thread leave")
                .description("Fired when a member leaves a thread, either by leaving itself or by a moderator can be used to get the thread, the guild and the member.")
                .examples("on thread leave:");


        SkriptUtils.registerBotValue(ThreadLeaveEvent.BukkitThreadLeaveEvent.class);

        SkriptUtils.registerValue(ThreadLeaveEvent.BukkitThreadLeaveEvent.class, Member.class,
                event -> event.getJDAEvent().getMember(), 0);

        SkriptUtils.registerValue(ThreadLeaveEvent.BukkitThreadLeaveEvent.class, ThreadChannel.class,
                event -> event.getJDAEvent().getThread(), 0);

        SkriptUtils.registerValue(ThreadLeaveEvent.BukkitThreadLeaveEvent.class, Guild.class,
                event -> event.getJDAEvent().getGuild(), 0);

    }

    public static class BukkitThreadLeaveEvent extends SimpleDiSkyEvent<ThreadMemberLeaveEvent> {
        public BukkitThreadLeaveEvent(ThreadLeaveEvent event) {
        }
    }
}
