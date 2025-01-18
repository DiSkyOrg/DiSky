package net.itsthesky.disky.elements.events.user;

import net.itsthesky.disky.api.events.DiSkyEvent;
import net.itsthesky.disky.api.events.SimpleDiSkyEvent;
import net.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

public class UserActivityOrderEvent extends DiSkyEvent<net.dv8tion.jda.api.events.user.update.UserUpdateActivityOrderEvent> {

    static {
        register("User Activity Order Event", UserActivityOrderEvent.class, BukkitUserActivityOrderEvent.class,
                "[discord] user activity [order] (change|update)")
                .description("Fired when a user in a guild changes its activity. Ex: by playing something different can be used to get the old/new activities.")
                .examples("on user activity change:");


        SkriptUtils.registerBotValue(UserActivityOrderEvent.BukkitUserActivityOrderEvent.class);

        SkriptUtils.registerValues(BukkitUserActivityOrderEvent.class, Activity.class,
                "permissions", event -> event.getJDAEvent().getNewValue().toArray(new Activity[0]));

        SkriptUtils.registerValue(UserActivityOrderEvent.BukkitUserActivityOrderEvent.class, User.class,
                event -> event.getJDAEvent().getUser(), 0);

        SkriptUtils.registerValue(UserActivityOrderEvent.BukkitUserActivityOrderEvent.class, Guild.class,
                event -> event.getJDAEvent().getGuild(), 0);

        SkriptUtils.registerValue(UserActivityOrderEvent.BukkitUserActivityOrderEvent.class, Member.class,
                event -> event.getJDAEvent().getMember(), 0);
    }

    public static class BukkitUserActivityOrderEvent extends SimpleDiSkyEvent<net.dv8tion.jda.api.events.user.update.UserUpdateActivityOrderEvent> {
        public BukkitUserActivityOrderEvent(UserActivityOrderEvent event) {
        }
    }
}
