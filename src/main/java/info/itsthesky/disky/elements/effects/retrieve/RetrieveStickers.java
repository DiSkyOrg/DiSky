package info.itsthesky.disky.elements.effects.retrieve;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import info.itsthesky.disky.api.skript.BaseMultipleRetrieveEffect;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.sticker.GuildSticker;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Name("Retrieve Stickers")
@Description({"Retrieve every stickers (and cache them) from a specific guild."})
public class RetrieveStickers extends BaseMultipleRetrieveEffect<List<GuildSticker>, Guild> {

    static {
        register(
                RetrieveStickers.class,
                "stickers",
                "guild"
        );
    }

    @Override
    protected RestAction<List<GuildSticker>> retrieve(@NotNull Guild entity) {
        return entity.retrieveStickers();
    }

}
