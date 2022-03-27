package info.itsthesky.disky.elements.events.user;

import info.itsthesky.disky.api.events.DiSkyEvent;
import info.itsthesky.disky.api.events.SimpleDiSkyEvent;
import info.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.user.update.UserUpdateOnlineStatusEvent;

public class UserStatusEvent extends DiSkyEvent<UserUpdateOnlineStatusEvent> {

    static {
        register("User Status Event", UserStatusEvent.class, BukkitUserStatusEvent.class,
                "[discord] user [online] status (change|update)")
                .description("Fired when a user changes its online status.")
                .examples("on user online status change:");


        SkriptUtils.registerBotValue(UserStatusEvent.BukkitUserStatusEvent.class);


        SkriptUtils.registerValue(UserStatusEvent.BukkitUserStatusEvent.class, OnlineStatus.class,
                event -> event.getJDAEvent().getOldValue(), 0);
        SkriptUtils.registerValue(UserStatusEvent.BukkitUserStatusEvent.class, OnlineStatus.class,
                event -> event.getJDAEvent().getNewValue(), 1);
        SkriptUtils.registerValue(UserStatusEvent.BukkitUserStatusEvent.class, OnlineStatus.class,
                event -> event.getJDAEvent().getOldValue(), -1);

        SkriptUtils.registerValue(UserStatusEvent.BukkitUserStatusEvent.class, User.class,
                event -> event.getJDAEvent().getUser());

    }

    public static class BukkitUserStatusEvent extends SimpleDiSkyEvent<UserUpdateOnlineStatusEvent> {
        public BukkitUserStatusEvent(UserStatusEvent event) {
        }
    }
}