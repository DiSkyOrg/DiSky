package net.itsthesky.disky.elements.properties.embeds;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import net.itsthesky.disky.api.skript.EasyElement;
import net.itsthesky.disky.core.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EmbedToJSON extends SimpleExpression<String> {

    static {
        Skript.registerExpression(
                EmbedToJSON.class,
                String.class,
                ExpressionType.COMBINED,
                "[the] embed %embedbuilder% (to|as) json"
        );
    }

    private Expression<EmbedBuilder> exprJSON;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.@NotNull ParseResult parseResult) {
        exprJSON = (Expression<EmbedBuilder>) exprs[0];
        return true;
    }

    @Override
    protected String @NotNull [] get(@NotNull Event e) {
        final EmbedBuilder embedBuilder = EasyElement.parseSingle(exprJSON, e);
        if (embedBuilder == null)
            return new String[0];

        return new String[] {Utils.convertEmbedToJSON(embedBuilder)};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "embed from json " + exprJSON.toString(e, debug);
    }
}
