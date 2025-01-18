package net.itsthesky.disky.elements.properties.guilds;

import net.dv8tion.jda.api.entities.Guild;

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
