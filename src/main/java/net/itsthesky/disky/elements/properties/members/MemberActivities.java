package net.itsthesky.disky.elements.properties.members;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.NotNull;

@Name("Member Activities")
@Description({
        "Returns all activities of a member (playing, streaming, listening, watching, custom status, competing).",
        "If the member has no activity, this returns an empty list.",
        "",
        "Requires CacheFlag.ACTIVITY to be enabled on the bot, along with the GUILD_PRESENCES intent."
})
@Examples({
        "set {_activities::*} to activities of event-member",
        "loop activities of event-member:",
        "\tbroadcast \"%activity text of loop-value% (%activity type of loop-value%)\""
})
public class MemberActivities extends MultipleMemberProperty<Activity> {

    static {
        register(
                MemberActivities.class,
                Activity.class,
                "activit(y|ies)"
        );
    }

    @Override
    protected Activity[] convert(Member member) {
        return member.getActivities().toArray(new Activity[0]);
    }

    @Override
    public @NotNull Class<? extends Activity> getReturnType() {
        return Activity.class;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "activities";
    }
}