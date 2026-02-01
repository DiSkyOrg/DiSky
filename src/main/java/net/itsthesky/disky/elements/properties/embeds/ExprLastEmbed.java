package net.itsthesky.disky.elements.properties.embeds;

import ch.njol.skript.Skript;
import net.itsthesky.disky.api.DiSkyRegistry;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import net.itsthesky.disky.elements.sections.EmbedSection;
import net.dv8tion.jda.api.EmbedBuilder;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Last Embed")
@Description("This expression returns the last generated embed using the embed builder.")
@Since("1.0")
public class ExprLastEmbed extends SimpleExpression<EmbedBuilder> {

    static {
        DiSkyRegistry.registerExpression(ExprLastEmbed.class, EmbedBuilder.class, ExpressionType.SIMPLE,
                "[the] [last] (made|created|generated) embed",
                "[the] last embed"
        );
    }

    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull SkriptParser.ParseResult parseResult) {
        return true;
    }

    @Nullable
    @Override
    protected EmbedBuilder[] get(@NotNull Event e) {
        if (EmbedSection.lastSection == null)
            return new EmbedBuilder[0];

        return new EmbedBuilder[]{EmbedSection.lastSection.getCurrentValue()};
    }

    @Override
    public @NotNull Class<? extends EmbedBuilder> getReturnType() {
        return EmbedBuilder.class;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "the last generated embed";
    }
}