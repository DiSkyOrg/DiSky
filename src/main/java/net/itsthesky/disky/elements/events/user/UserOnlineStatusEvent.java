package net.itsthesky.disky.elements.events.user;

import net.itsthesky.disky.api.events.DiSkyEvent;
import net.itsthesky.disky.api.events.SimpleDiSkyEvent;
import net.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.user.update.UserUpdateOnlineStatusEvent;

public class UserOnlineStatusEvent extends DiSkyEvent<UserUpdateOnlineStatusEvent> {

    static {
        register("User Online Status Event", UserOnlineStatusEvent.class, BukkitUserOnlineStatusEvent.class,
                "[discord] user online status (change|update)")
                .description("Fired when a user changes its online status.")
                .examples("on user online status change:");


        SkriptUtils.registerBotValue(UserOnlineStatusEvent.BukkitUserOnlineStatusEvent.class);


        SkriptUtils.registerValue(UserOnlineStatusEvent.BukkitUserOnlineStatusEvent.class, OnlineStatus.class,
                event -> event.getJDAEvent().getOldValue(), -1);

        SkriptUtils.registerValue(UserOnlineStatusEvent.BukkitUserOnlineStatusEvent.class, OnlineStatus.class,
                event -> event.getJDAEvent().getNewValue(), 0);

        SkriptUtils.registerValue(UserOnlineStatusEvent.BukkitUserOnlineStatusEvent.class, OnlineStatus.class,
                event -> event.getJDAEvent().getNewValue(), 1);

        SkriptUtils.registerValue(UserOnlineStatusEvent.BukkitUserOnlineStatusEvent.class, User.class,
                event -> event.getJDAEvent().getUser(), 0);

        SkriptUtils.registerValue(UserOnlineStatusEvent.BukkitUserOnlineStatusEvent.class, Member.class,
                event -> event.getJDAEvent().getMember(), 0);

        SkriptUtils.registerValue(UserOnlineStatusEvent.BukkitUserOnlineStatusEvent.class, Guild.class,
                event -> event.getJDAEvent().getGuild(), 0);
    }

    public static class BukkitUserOnlineStatusEvent extends SimpleDiSkyEvent<UserUpdateOnlineStatusEvent> {
        public BukkitUserOnlineStatusEvent(UserOnlineStatusEvent event) {
        }
    }
}