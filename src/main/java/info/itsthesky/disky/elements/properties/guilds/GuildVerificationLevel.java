package info.itsthesky.disky.elements.properties.guilds;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import net.dv8tion.jda.api.entities.Guild;

import java.util.Locale;

@Name("Guild Verification Level")
@Description({"Represent the verification level of the guild. It can either be:",
        "- None",
        "- Low",
        "- Medium",
        "- High",
        "- Very High"})
@Examples("reply with mention tag of everyone role of event-guild")
public class GuildVerificationLevel extends GuildProperty<String> {

    static {
        register(
                GuildVerificationLevel.class,
                String.class,
                "verification level[s]"
        );
    }

    @Override
    public String converting(Guild guild) {
        return guild.getVerificationLevel().name().toLowerCase(Locale.ROOT).replace("_", " ");
    }
}
