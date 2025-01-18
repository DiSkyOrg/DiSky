package net.itsthesky.disky.elements.events.user;

import net.itsthesky.disky.api.events.DiSkyEvent;
import net.itsthesky.disky.api.events.SimpleDiSkyEvent;
import net.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.user.update.UserUpdateNameEvent;

public class UserNameEvent extends DiSkyEvent<UserUpdateNameEvent> {

    static {
        register("User Name Event", UserNameEvent.class, BukkitUserNameEvent.class,
                "[discord] user name (change|update)")
                .description("Fired when a user changes its name (not nickname).")
                .examples("on user name change:");


        SkriptUtils.registerBotValue(UserNameEvent.BukkitUserNameEvent.class);

        SkriptUtils.registerValue(UserNameEvent.BukkitUserNameEvent.class, User.class,
                event -> event.getJDAEvent().getUser(), 0);

        SkriptUtils.registerValue(UserNameEvent.BukkitUserNameEvent.class, String.class,
                event -> event.getJDAEvent().getOldValue(), -1);

        SkriptUtils.registerValue(UserNameEvent.BukkitUserNameEvent.class, String.class,
                event -> event.getJDAEvent().getNewValue(), 0);

        SkriptUtils.registerValue(UserNameEvent.BukkitUserNameEvent.class, String.class,
                event -> event.getJDAEvent().getNewValue(), 1);
    }

    public static class BukkitUserNameEvent extends SimpleDiSkyEvent<UserUpdateNameEvent> {
        public BukkitUserNameEvent(UserNameEvent event) {
        }
    }
}