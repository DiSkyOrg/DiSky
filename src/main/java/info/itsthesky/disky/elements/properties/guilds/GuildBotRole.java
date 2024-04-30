package info.itsthesky.disky.elements.properties.guilds;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;

public class GuildBotRole extends GuildProperty<Role>  {

    static {
        register(
                GuildBotRole.class,
                Role.class,
                "bot role"
        );
    }

    @Override
    public Role converting(Guild guild) {
        return guild.getBotRole();
    }
}
