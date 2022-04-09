package info.itsthesky.disky.elements.properties.guilds;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import net.dv8tion.jda.api.entities.Guild;

@Name("Guild Booster Count")
@Description({"Represent how many people are boosting the guild currently."})
@Examples("reply with \"There's %booster amount of event-guild% booster(s)!\"")
public class GuildBoostCount extends GuildProperty<Number>  {

    static {
        register(
                GuildBoostCount.class,
                Number.class,
                "boost[(ing|er)] (amount|number|size)"
        );
    }

    @Override
    public Number converting(Guild guild) {
        return guild.getBoostCount();
    }
}
