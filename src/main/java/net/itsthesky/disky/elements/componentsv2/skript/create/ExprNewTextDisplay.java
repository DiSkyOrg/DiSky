package net.itsthesky.disky.elements.componentsv2.skript.create;

import ch.njol.skript.Skript;
import ch.njol.skript.config.Node;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import net.dv8tion.jda.api.components.separator.Separator;
import net.itsthesky.disky.api.skript.EasyElement;
import net.itsthesky.disky.elements.componentsv2.base.sub.SeparatorBuilder;
import net.itsthesky.disky.elements.componentsv2.base.sub.TextDisplayBuilder;
import net.itsthesky.disky.elements.sections.handler.DiSkyRuntimeHandler;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprNewTextDisplay extends SimpleExpression<TextDisplayBuilder> {

    static {
        Skript.registerExpression(
                ExprNewTextDisplay.class,
                TextDisplayBuilder.class,
                ExpressionType.SIMPLE,
                "[a] new text display [with text] %string% [with [unique] id %-integer%]"
        );
    }

    private Expression<String> exprText;
    private Expression<Integer> exprUniqueId;
    private Node node;

    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull SkriptParser.ParseResult parseResult) {
        node = getParser().getNode();

        exprText = (Expression<String>) exprs[0];
        exprUniqueId = (Expression<Integer>) exprs[1];
        return true;
    }

    @Override
    protected TextDisplayBuilder @NotNull [] get(@NotNull Event e) {
        final var text = EasyElement.parseSingle(exprText, e);
        if (text == null) {
            DiSkyRuntimeHandler.exprNotSet(node, exprText);
            return new TextDisplayBuilder[0];
        }

        final var uniqueId = EasyElement.parseSingle(exprUniqueId, e, -1);

        return new TextDisplayBuilder[]{
                new TextDisplayBuilder(text, uniqueId)
        };
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends TextDisplayBuilder> getReturnType() {
        return TextDisplayBuilder.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "new text display with text " + exprText.toString(event, debug) +
                (exprUniqueId != null ? " and unique id " + exprUniqueId.toString(event, debug) : "");
    }
}