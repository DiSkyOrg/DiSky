package net.itsthesky.disky.elements.getters;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.SeeAlso;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import net.itsthesky.disky.api.skript.EasyElement;
import net.itsthesky.disky.elements.changers.IAsyncGettableExpression;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.sticker.GuildSticker;
import net.dv8tion.jda.api.entities.sticker.Sticker;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

@Name("Get Sticker")
@Description({"Get a cached sticker from its per-guild name",
        "This expression is here to get a sticker from its name.",
        "If you success to get a sticker's ID, use the retrieve sticker effect instead!",
        "This expression cannot be changed"})
@Examples({"sticker with named \"meliodas\" from event-guild"})
@Since("4.9.0")
@SeeAlso({Guild.class, GuildSticker.class, Sticker.class})
public class GetSticker extends SimpleExpression<Sticker> implements IAsyncGettableExpression<Sticker> {

    static {
        Skript.registerExpression(
                GetSticker.class,
                Sticker.class,
                ExpressionType.COMBINED,
                "[get] [the] sticker (with name|named) %string% (from|in|of) [the] [guild] %guild%"
        );
    }

    private Expression<String> exprId;
    private Expression<Guild> exprGuild;

    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
        exprId = (Expression<String>) exprs[0];
        exprGuild = (Expression<Guild>) exprs[1];
        return true;
    }

    @Override
    protected Sticker @NotNull [] get(@NotNull Event e) {
        final String id = exprId.getSingle(e);
        final Guild guild = exprGuild.getSingle(e);
        if (EasyElement.anyNull(this, id, guild))
            return new Sticker[0];
        return new Sticker[] {guild.getStickersByName(id, false).stream().findFirst().orElse(null)};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<? extends Sticker> getReturnType() {
        return Sticker.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "sticker with name " + exprId.toString(e, debug) +" in " + exprGuild.toString(e, debug);
    }

    @Override
    public Sticker[] getAsync(Event e) {
        final String id = exprId.getSingle(e);
        final Guild guild = exprGuild.getSingle(e);
        if (EasyElement.anyNull(this, id, guild))
            return new Sticker[0];

        final Sticker found = guild.getStickersByName(id, false).stream().findFirst().orElse(null);
        if (found != null)
            return new Sticker[] {found};

        final List<GuildSticker> retrieveFound = guild.retrieveStickers().complete()
                .stream().filter(sticker -> sticker.getName().equalsIgnoreCase(id)).collect(Collectors.toList());

        return retrieveFound.isEmpty() ? new Sticker[0] : new Sticker[] {retrieveFound.get(0)};
    }
}
