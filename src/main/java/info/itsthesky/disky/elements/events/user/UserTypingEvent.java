package info.itsthesky.disky.elements.events.user;

import info.itsthesky.disky.api.events.DiSkyEvent;
import info.itsthesky.disky.api.events.SimpleDiSkyEvent;
import info.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.*;
import net.dv8tion.jda.api.entities.channel.attribute.*;
import net.dv8tion.jda.api.entities.channel.middleman.*;
import net.dv8tion.jda.api.entities.channel.concrete.*;
import net.dv8tion.jda.api.entities.User;

public class UserTypingEvent extends DiSkyEvent<net.dv8tion.jda.api.events.user.UserTypingEvent> {

    static {
        register("User Typing Event", UserTypingEvent.class, BukkitUserTypingEvent.class,
                "[discord] user typ[e|ing]")
                .description("Fired when a user starts typing in a channel.")
                .examples("on user typing:");


        SkriptUtils.registerBotValue(UserTypingEvent.BukkitUserTypingEvent.class);

        SkriptUtils.registerValue(UserTypingEvent.BukkitUserTypingEvent.class, User.class,
                event -> event.getJDAEvent().getUser(), 0);

        SkriptUtils.registerValue(UserTypingEvent.BukkitUserTypingEvent.class, Guild.class,
                event -> event.getJDAEvent().getGuild(), 0);

        SkriptUtils.registerValue(UserTypingEvent.BukkitUserTypingEvent.class, Member.class,
                event -> event.getJDAEvent().getMember(), 0);
    }

    public static class BukkitUserTypingEvent extends SimpleDiSkyEvent<net.dv8tion.jda.api.events.user.UserTypingEvent> {
        public BukkitUserTypingEvent(UserTypingEvent event) {
        }
    }
}