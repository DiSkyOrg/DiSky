package info.itsthesky.disky.elements.events.user;

import info.itsthesky.disky.api.events.DiSkyEvent;
import info.itsthesky.disky.api.events.SimpleDiSkyEvent;
import info.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.user.update.UserUpdateActivityOrderEvent;

public class UserActivityOrderEvent extends DiSkyEvent<UserUpdateActivityOrderEvent> {

    static {
        register("User Activity Event", UserActivityOrderEvent.class, BukkitUserActivityOrderEvent.class,
                "[discord] user activity (change|update)")
                .description("Fired when a user changes its online status \n a user can have more than one activities, so consider using the plural event-value expression")
                .examples("on user online status change:");


        SkriptUtils.registerBotValue(UserActivityOrderEvent.BukkitUserActivityOrderEvent.class);

        SkriptUtils.registerValue(UserActivityOrderEvent.BukkitUserActivityOrderEvent.class, Activity[].class,
                event -> event.getJDAEvent().getOldValue().toArray(new Activity[0]), 0);
        SkriptUtils.registerValue(UserActivityOrderEvent.BukkitUserActivityOrderEvent.class, Activity[].class,
                event -> event.getJDAEvent().getNewValue().toArray(new Activity[0]), 1);
        SkriptUtils.registerValue(UserActivityOrderEvent.BukkitUserActivityOrderEvent.class, Activity[].class,
                event -> event.getJDAEvent().getNewValue().toArray(new Activity[0]), -1);

        SkriptUtils.registerValue(UserActivityOrderEvent.BukkitUserActivityOrderEvent.class, User.class,
                event -> event.getJDAEvent().getUser());

        SkriptUtils.registerValue(UserActivityOrderEvent.BukkitUserActivityOrderEvent.class, Member.class,
                event -> event.getJDAEvent().getMember());

    }

    public static class BukkitUserActivityOrderEvent extends SimpleDiSkyEvent<UserUpdateActivityOrderEvent> {
        public BukkitUserActivityOrderEvent(UserActivityOrderEvent event) {
        }
    }
}