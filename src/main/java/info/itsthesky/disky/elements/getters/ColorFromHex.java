package info.itsthesky.disky.elements.getters;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.util.Color;
import ch.njol.skript.util.ColorRGB;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.api.skript.EasyElement;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Color from Hex")
@Description("Get a color from a hexadecimal string.")
@Examples("set embed color of embed to hex \"ff0000\"")
public class ColorFromHex extends SimpleExpression<Color> {

    static {
        Skript.registerExpression(ColorFromHex.class, Color.class, ExpressionType.PROPERTY,
                "[the] (hex|color) %string%");
    }

    private Expression<String> exprHex;

    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.@NotNull ParseResult parseResult) {
        exprHex = (Expression<String>) exprs[0];
        return true;
    }

    @Override
    protected Color @NotNull [] get(@NotNull Event e) {
        final String hex = EasyElement.parseSingle(exprHex, e, null);
        if (hex == null)
            return new Color[0];
        final java.awt.Color awtColor = java.awt.Color.decode(hex);
        return new Color[] {
            new ColorRGB(awtColor.getRed(), awtColor.getGreen(), awtColor.getBlue())
        };
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<? extends Color> getReturnType() {
        return Color.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "the hex color " + exprHex.toString(e, debug);
    }
}
