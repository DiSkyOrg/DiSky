package net.itsthesky.disky.elements.events.user;

import net.itsthesky.disky.api.events.DiSkyEvent;
import net.itsthesky.disky.api.events.SimpleDiSkyEvent;
import net.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.user.update.UserUpdateAvatarEvent;

public class UserAvatarEvent extends DiSkyEvent<UserUpdateAvatarEvent> {

    static {
        register("User Avatar Event", UserAvatarEvent.class, BukkitUserAvatarEvent.class,
                "[discord] user avatar (change|update)")
                .description("Fired when a user changes its avatar.")
                .examples("on user avatar change:");


        SkriptUtils.registerBotValue(UserAvatarEvent.BukkitUserAvatarEvent.class);

        SkriptUtils.registerValue(UserAvatarEvent.BukkitUserAvatarEvent.class, String.class,
                event -> event.getJDAEvent().getOldValue(), -1);

        SkriptUtils.registerValue(UserAvatarEvent.BukkitUserAvatarEvent.class, String.class,
                event -> event.getJDAEvent().getNewValue(), 0);

        SkriptUtils.registerValue(UserAvatarEvent.BukkitUserAvatarEvent.class, String.class,
                event -> event.getJDAEvent().getNewValue(), 1);

        SkriptUtils.registerValue(UserAvatarEvent.BukkitUserAvatarEvent.class, User.class,
                event -> event.getJDAEvent().getUser(), 0);

    }

    public static class BukkitUserAvatarEvent extends SimpleDiSkyEvent<UserUpdateAvatarEvent> {
        public BukkitUserAvatarEvent(UserAvatarEvent event) {
        }
    }
}