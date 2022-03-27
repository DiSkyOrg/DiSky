package info.itsthesky.disky.elements.events.user;

import info.itsthesky.disky.api.events.DiSkyEvent;
import info.itsthesky.disky.api.events.SimpleDiSkyEvent;
import info.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.user.update.UserUpdateNameEvent;

public class UserNameEvent extends DiSkyEvent<UserUpdateNameEvent> {

    static {
        register("User Name Event", UserNameEvent.class, BukkitUserUpdateNameEvent.class,
                "[discord] user nick[name] (change|update)")
                .description("Fired when a user changes its nickname.")
                .examples("on user nickname change:");


        SkriptUtils.registerBotValue(UserNameEvent.BukkitUserUpdateNameEvent.class);


        SkriptUtils.registerValue(UserNameEvent.BukkitUserUpdateNameEvent.class, String.class,
                event -> event.getJDAEvent().getNewValue(), 0);
        SkriptUtils.registerValue(UserNameEvent.BukkitUserUpdateNameEvent.class, String.class,
                event -> event.getJDAEvent().getNewValue(), 1);
        SkriptUtils.registerValue(UserNameEvent.BukkitUserUpdateNameEvent.class, String.class,
                event -> event.getJDAEvent().getOldValue(), -1);

        SkriptUtils.registerValue(UserNameEvent.BukkitUserUpdateNameEvent.class, User.class,
                event -> event.getJDAEvent().getUser());

    }

    public static class BukkitUserUpdateNameEvent extends SimpleDiSkyEvent<UserUpdateNameEvent> {
        public BukkitUserUpdateNameEvent(UserUpdateNameEvent event) {
        }
    }
}