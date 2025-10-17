package net.itsthesky.disky.elements.componentsv2.skript.create;

import ch.njol.skript.Skript;
import ch.njol.skript.config.Node;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import net.dv8tion.jda.api.utils.FileUpload;
import net.itsthesky.disky.api.skript.EasyElement;
import net.itsthesky.disky.core.JDAUtils;
import net.itsthesky.disky.elements.componentsv2.base.sub.FileDisplayBuilder;
import net.itsthesky.disky.elements.componentsv2.base.sub.ThumbnailBuilder;
import net.itsthesky.disky.elements.sections.handler.DiSkyRuntimeHandler;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprNewThumbnail extends SimpleExpression<ThumbnailBuilder> {

    static {
        Skript.registerExpression(
                ExprNewThumbnail.class,
                ThumbnailBuilder.class,
                ExpressionType.COMBINED,
                "[a] new thumbnail [display] [with [the] (source|url)] %fileupload/string% [with [unique] id %-integer%]"
        );
    }

    private Expression<Object> exprSource;
    private Expression<Integer> exprUniqueId;
    private Node node;

    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull SkriptParser.ParseResult parseResult) {
        node = getParser().getNode();

        exprSource = (Expression<Object>) exprs[0];
        exprUniqueId = (Expression<Integer>) exprs[1];

        return true;
    }

    @Override
    protected ThumbnailBuilder @NotNull [] get(@NotNull Event e) {
        final var uniqueId = EasyElement.parseSingle(exprUniqueId, e, -1);
        final var source = EasyElement.parseSingle(exprSource, e, null);
        if (source == null) {
            DiSkyRuntimeHandler.exprNotSet(node, exprSource);
            return new ThumbnailBuilder[0];
        }

        if (source instanceof FileUpload upload)
            return new ThumbnailBuilder[]{ new ThumbnailBuilder(upload, uniqueId) };
        else if (source instanceof String url)
            return new ThumbnailBuilder[]{ new ThumbnailBuilder(url, uniqueId) };

        DiSkyRuntimeHandler.error(new IllegalArgumentException("The source of a thumbnail must be a FileUpload or a String (url)."));
        return new ThumbnailBuilder[0];
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends ThumbnailBuilder> getReturnType() {
        return ThumbnailBuilder.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "new thumbnail display with source " + exprSource.toString(event, debug) +
                (exprUniqueId != null ? " with unique id "+ exprUniqueId.toString(event, debug) : "");
    }
}