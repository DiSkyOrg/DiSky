package net.itsthesky.disky.elements.componentsv2.skript.create;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import net.dv8tion.jda.api.components.separator.Separator;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.requests.restaction.pagination.PinnedMessagePaginationAction;
import net.itsthesky.disky.api.skript.EasyElement;
import net.itsthesky.disky.elements.componentsv2.base.sub.SeparatorBuilder;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprNewSeparator extends SimpleExpression<SeparatorBuilder> {
    
    static {
        Skript.registerExpression(
            ExprNewSeparator.class,
            SeparatorBuilder.class,
            ExpressionType.SIMPLE,
            "[a] new [invisible] [(small|:large)] (separator|divider) [with [unique] id %-integer%]"
        );
    }

    private boolean isInvisible;
    private boolean isLarge;
    private Expression<Integer> exprUniqueId;

    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull SkriptParser.ParseResult parseResult) {
        isLarge = parseResult.hasTag("large");
        isInvisible = parseResult.expr.contains("new invisible");
        exprUniqueId = (Expression<Integer>) exprs[0];
        return true;
    }

    @Override
    protected SeparatorBuilder @NotNull [] get(@NotNull Event e) {
        final var uniqueId = EasyElement.parseSingle(exprUniqueId, e, -1);

        return new SeparatorBuilder[] {
            new SeparatorBuilder(isInvisible,
                    isLarge ? Separator.Spacing.LARGE : Separator.Spacing.SMALL,
                    uniqueId)
        };
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends SeparatorBuilder> getReturnType() {
        return SeparatorBuilder.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "new " + (isInvisible ? "invisible " : "") + (isLarge ? "large " : "small ") + "separator";
    }
}