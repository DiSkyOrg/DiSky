package net.itsthesky.disky.elements.properties.guilds;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

@Name("Guild Boosters")
@Description({"Represent the current members booster of the guild."})
@Examples("reply with \"Boosters: %boosters of event-guild%!\"")
public class GuildBoosters extends MultipleGuildProperty<Member> {

    static {
        register(
                GuildBoosters.class,
                Member.class,
                "booster[s] [member[s]]"
        );
    }

    @Override
    public Member[] converting(Guild guild) {
        return guild.getBoosters().toArray(new Member[0]);
    }
}
