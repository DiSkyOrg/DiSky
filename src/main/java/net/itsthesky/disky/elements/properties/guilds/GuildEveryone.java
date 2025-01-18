package net.itsthesky.disky.elements.properties.guilds;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import org.jetbrains.annotations.NotNull;

@Name("Everyone Role")
@Description({"Represent the @everyone role of a guild.",
"Even if it's not a real role, it share multiple properties such as permissions."})
@Examples("reply with mention tag of everyone role of event-guild")
public class GuildEveryone extends GuildProperty<Role> {

    static {
        register(
                GuildEveryone.class,
                Role.class,
                "[discord] (public|everyone) role",
                "guild"
        );
    }

    @Override
    public @NotNull Role converting(Guild guild) {
        return guild.getPublicRole();
    }

}
