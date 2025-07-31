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
import net.itsthesky.disky.elements.sections.handler.DiSkyRuntimeHandler;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprNewFileDisplay extends SimpleExpression<FileDisplayBuilder> {

    static {
        Skript.registerExpression(
                ExprNewFileDisplay.class,
                FileDisplayBuilder.class,
                ExpressionType.COMBINED,
                "[a] new [spoiler] file display [with [the] source] %string/fileupload% [with [unique] id %-integer%]"
        );
    }

    private boolean isSpoiler;
    private Expression<Object> exprSource;
    private Expression<Integer> exprUniqueId;
    private Node node;

    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull SkriptParser.ParseResult parseResult) {
        node = getParser().getNode();
        isSpoiler = parseResult.expr.contains("new spoiler");

        exprSource = (Expression<Object>) exprs[0];
        exprUniqueId = (Expression<Integer>) exprs[1];

        return true;
    }

    @Override
    protected FileDisplayBuilder @NotNull [] get(@NotNull Event e) {
        final var uniqueId = EasyElement.parseSingle(exprUniqueId, e, -1);
        final var source = EasyElement.parseSingle(exprSource, e, null);
        if (source == null) {
            DiSkyRuntimeHandler.exprNotSet(node, exprSource);
            return new FileDisplayBuilder[0];
        }

        FileUpload fileUpload = null;
        if (source instanceof final String raw)
            fileUpload = JDAUtils.parseFile(raw);
        else if (source instanceof final FileUpload upload)
            fileUpload = upload;

        if (fileUpload == null)
            return new FileDisplayBuilder[0];

        return new FileDisplayBuilder[]{
                new FileDisplayBuilder(fileUpload, isSpoiler, uniqueId)
        };
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends FileDisplayBuilder> getReturnType() {
        return FileDisplayBuilder.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "new " + (isSpoiler ? "spoiler " : "") + "file display with source " + exprSource.toString(event, debug)
                + (exprUniqueId != null ? " with unique id " + exprUniqueId.toString(event, debug) : "");
    }
}