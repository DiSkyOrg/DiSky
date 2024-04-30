package info.itsthesky.disky.elements.properties.guilds;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;

public class GuildBoostTier extends GuildProperty<String>  {

    static {
        register(
                GuildBoostTier.class,
                String.class,
                "boost[(ing|er)] tier"
        );
    }

    @Override
    public String converting(Guild guild) {
        return guild.getBoostTier().name().toLowerCase().replace("_", " ");
    }
}
