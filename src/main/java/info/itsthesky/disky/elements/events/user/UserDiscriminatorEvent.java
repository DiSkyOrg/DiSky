package info.itsthesky.disky.elements.events.user;

import info.itsthesky.disky.api.events.DiSkyEvent;
import info.itsthesky.disky.api.events.SimpleDiSkyEvent;
import info.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.user.update.UserUpdateDiscriminatorEvent;

public class UserDiscriminatorEvent extends DiSkyEvent<UserUpdateDiscriminatorEvent> {

    static {
        register("User Discriminator Event", UserDiscriminatorEvent.class, BukkitUserDiscriminatorEvent.class,
                "[discord] user discriminator (change|update)")
                .description("Fired when a user changes its discriminator.")
                .examples("on user discriminator change:");


        SkriptUtils.registerBotValue(UserDiscriminatorEvent.BukkitUserDiscriminatorEvent.class);


        SkriptUtils.registerValue(UserDiscriminatorEvent.BukkitUserDiscriminatorEvent.class, String.class,
                event -> event.getJDAEvent().getNewValue(), 0);
        SkriptUtils.registerValue(UserDiscriminatorEvent.BukkitUserDiscriminatorEvent.class, String.class,
                event -> event.getJDAEvent().getNewValue(), 1);
        SkriptUtils.registerValue(UserDiscriminatorEvent.BukkitUserDiscriminatorEvent.class, String.class,
                event -> event.getJDAEvent().getOldValue(), -1);

        SkriptUtils.registerValue(UserDiscriminatorEvent.BukkitUserDiscriminatorEvent.class, User.class,
                event -> event.getJDAEvent().getUser());

    }

    public static class BukkitUserDiscriminatorEvent extends SimpleDiSkyEvent<UserUpdateDiscriminatorEvent> {
        public BukkitUserDiscriminatorEvent(UserDiscriminatorEvent event) {
        }
    }
}