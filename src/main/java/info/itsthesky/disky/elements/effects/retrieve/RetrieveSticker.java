package info.itsthesky.disky.elements.effects.retrieve;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import info.itsthesky.disky.api.skript.BaseRetrieveEffect;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.sticker.GuildSticker;
import net.dv8tion.jda.api.entities.sticker.Sticker;
import net.dv8tion.jda.api.entities.sticker.StickerSnowflake;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;

@Name("Retrieve Sticker")
@Description({"Retrieve a sticker from a guild using its per-guild name.",
        "This will return a sticker from the guild, not a global one."})
public class RetrieveSticker extends BaseRetrieveEffect<GuildSticker, Guild> {

    static {
        register(
                RetrieveSticker.class,
                "sticker",
                "guild"
        );
    }

    @Override
    protected RestAction<GuildSticker> retrieve(@NotNull String input, @NotNull Guild entity) {
        return entity.retrieveSticker(StickerSnowflake.fromId(input));
    }

}
