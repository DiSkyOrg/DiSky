package info.itsthesky.disky.elements.properties.guilds;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;

@Name("Guild Booster Role")
@Description({"Represent the booster role of this guild.",
"Any member that got this role is actually a booster of the guild."})
@Examples("reply with \"Thanks to our %mention tag of boost role of event-guild%!\"")
public class GuildBoostRole extends GuildProperty<Role>  {

    static {
        register(
                GuildBoostRole.class,
                Role.class,
                "boost[(ing|er)] role[s]"
        );
    }

    @Override
    public Role converting(Guild guild) {
        return guild.getBoostRole();
    }
}
