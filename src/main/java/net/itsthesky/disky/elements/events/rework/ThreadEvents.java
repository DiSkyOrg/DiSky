package net.itsthesky.disky.elements.events.rework;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.events.thread.GenericThreadEvent;
import net.dv8tion.jda.api.events.thread.member.ThreadMemberJoinEvent;
import net.dv8tion.jda.api.events.thread.member.ThreadMemberLeaveEvent;
import net.itsthesky.disky.api.events.rework.EventRegistryFactory;

public class ThreadEvents {

    static {
        EventRegistryFactory.builder(ThreadMemberJoinEvent.class)
                .eventCategory(GuildEvents.class)
                .name("Thread Join Event")
                .patterns("[discord] thread join")
                .description("Fired when a member joins a tread, either by joining itself or by a moderator can be used to get the thread, the guild and the member.")
                .example("on thread join:")
                .implementMessage(GenericThreadEvent::getThread)

                .channelValues(ThreadMemberJoinEvent::getThread)
                .value(Member.class, ThreadMemberJoinEvent::getMember)
                .value(Guild.class, ThreadMemberJoinEvent::getGuild)
                .register();

        EventRegistryFactory.builder(ThreadMemberLeaveEvent.class)
                .eventCategory(GuildEvents.class)
                .name("Thread Leave Event")
                .patterns("[discord] thread leave")
                .description("Fired when a member leaves a thread, either by leaving itself or by a moderator can be used to get the thread, the guild and the member.")
                .example("on thread leave:")
                .implementMessage(GenericThreadEvent::getThread)

                .channelValues(ThreadMemberLeaveEvent::getThread)
                .value(Member.class, ThreadMemberLeaveEvent::getMember)
                .value(Guild.class, ThreadMemberLeaveEvent::getGuild)
                .register();
    }
}