package info.itsthesky.disky.elements.properties.embeds;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.api.skript.EasyElement;
import info.itsthesky.disky.core.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EmbedFromJSON extends SimpleExpression<EmbedBuilder> {

    static {
        Skript.registerExpression(
                EmbedFromJSON.class,
                EmbedBuilder.class,
                ExpressionType.COMBINED,
                "[the] embed (from|of) [json] %string%"
        );
    }

    private Expression<String> exprJSON;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.@NotNull ParseResult parseResult) {
        exprJSON = (Expression<String>) exprs[0];
        return true;
    }

    @Override
    protected EmbedBuilder @NotNull [] get(@NotNull Event e) {
        final String json = EasyElement.parseSingle(exprJSON, e);
        if (json == null)
            return new EmbedBuilder[0];

        return new EmbedBuilder[] {Utils.convertJSONToEmbed(json)};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<? extends EmbedBuilder> getReturnType() {
        return EmbedBuilder.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "embed from json " + exprJSON.toString(e, debug);
    }
}
