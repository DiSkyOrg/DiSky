package info.itsthesky.disky.elements.events.user;

import info.itsthesky.disky.api.events.DiSkyEvent;
import info.itsthesky.disky.api.events.SimpleDiSkyEvent;
import info.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.entities.*;

public class UserUpdateAvatarEvent extends DiSkyEvent<net.dv8tion.jda.api.events.user.update.UserUpdateAvatarEvent> {

    static {
        register("User Avatar Event", UserUpdateAvatarEvent.class, BukkitUserAvatarEvent.class,
                "[discord] user avatar (change|update)")
                .description("Fired when a user changes its avatar.")
                .examples("on user avatar change:");


        SkriptUtils.registerBotValue(UserUpdateAvatarEvent.BukkitUserAvatarEvent.class);


        SkriptUtils.registerValue(UserUpdateAvatarEvent.BukkitUserAvatarEvent.class, String.class,
                event -> event.getJDAEvent().getNewAvatarUrl(), 0);
        SkriptUtils.registerValue(UserUpdateAvatarEvent.BukkitUserAvatarEvent.class, String.class,
                event -> event.getJDAEvent().getNewAvatarUrl(), 1);
        SkriptUtils.registerValue(UserUpdateAvatarEvent.BukkitUserAvatarEvent.class, String.class,
                event -> event.getJDAEvent().getOldAvatarUrl(), -1);

        SkriptUtils.registerValue(UserUpdateAvatarEvent.BukkitUserAvatarEvent.class, User.class,
                event -> event.getJDAEvent().getUser());

    }

    public static class BukkitUserAvatarEvent extends SimpleDiSkyEvent<net.dv8tion.jda.api.events.user.update.UserUpdateAvatarEvent> {
        public BukkitUserAvatarEvent(UserUpdateAvatarEvent event) {
        }
    }
}