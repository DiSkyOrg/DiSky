package net.itsthesky.disky.elements.events.rework;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.events.thread.member.ThreadMemberJoinEvent;
import net.dv8tion.jda.api.events.thread.member.ThreadMemberLeaveEvent;
import net.itsthesky.disky.api.events.rework.EventRegistryFactory;

public class ThreadEvents {

    static {
        EventRegistryFactory.builder(ThreadMemberJoinEvent.class)
                .name("Thread Join Event")
                .patterns("[discord] thread join")
                .description("Fired when a member joins a tread, either by joining itself or by a moderator can be used to get the thread, the guild and the member.")
                .example("on thread join:")
                .value(Member.class, ThreadMemberJoinEvent::getMember)
                .value(ThreadChannel.class, ThreadMemberJoinEvent::getThread)
                .value(Guild.class, ThreadMemberJoinEvent::getGuild)
                .register();

        EventRegistryFactory.builder(ThreadMemberLeaveEvent.class)
                .name("Thread Leave Event")
                .patterns("[discord] thread leave")
                .description("Fired when a member leaves a thread, either by leaving itself or by a moderator can be used to get the thread, the guild and the member.")
                .example("on thread leave:")
                .value(Member.class, ThreadMemberLeaveEvent::getMember)
                .value(ThreadChannel.class, ThreadMemberLeaveEvent::getThread)
                .value(Guild.class, ThreadMemberLeaveEvent::getGuild)
                .register();
    }
}