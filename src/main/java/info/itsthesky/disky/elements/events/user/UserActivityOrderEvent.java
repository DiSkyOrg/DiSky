package info.itsthesky.disky.elements.events.user;

import info.itsthesky.disky.api.events.DiSkyEvent;
import info.itsthesky.disky.api.events.SimpleDiSkyEvent;
import info.itsthesky.disky.core.SkriptUtils;
import info.itsthesky.disky.elements.events.role.RolePermissionEvent;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.*;
import net.dv8tion.jda.api.entities.channel.attribute.*;
import net.dv8tion.jda.api.entities.channel.middleman.*;
import net.dv8tion.jda.api.entities.channel.concrete.*;
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
        public BukkitUserActivityOrderEvent(info.itsthesky.disky.elements.events.user.UserActivityOrderEvent event) {
        }
    }
}
