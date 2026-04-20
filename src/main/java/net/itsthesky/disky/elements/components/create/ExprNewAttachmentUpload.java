package net.itsthesky.disky.elements.components.create;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import net.dv8tion.jda.api.components.attachmentupload.AttachmentUpload;
import net.itsthesky.disky.api.DiSkyRegistry;
import net.itsthesky.disky.api.generator.SeeAlso;
import net.itsthesky.disky.api.skript.EasyElement;
import net.itsthesky.disky.elements.components.properties.ExprReceivedAttachments;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("New Attachment Upload Builder")
@Description({
        "Create a new attachment upload component, to be used in **modals only**.",
        "Min and max should be within 1 and 10! By default it requires a SINGLE file."
})
@Examples({"""
        set {_modal} to new modal with id "feedback" named "Give Us Feedback"
        set {_att} to new attachment upload builder with id "test" min size 3 and max size 10
        set {_label} to new label "File?" with {_att}
        add {_label} to rows of {_modal}
        show {_modal} to the user"""})
@Since("4.28.0")
@SeeAlso({ExprReceivedAttachments.class, ExprNewModal.class, ExprNewLabel.class})
public class ExprNewAttachmentUpload extends SimpleExpression<AttachmentUpload.Builder> {

    static {
        DiSkyRegistry.registerExpression(
                ExprNewAttachmentUpload.class,
                AttachmentUpload.Builder.class,
                ExpressionType.COMBINED,
                "[a] new [:required] [attachment] upload with [the] id %string% [[and] [with] unique id %-integer%] [[and] [with] min file[s] %-integer%] [[and] [with] max file[s] %-integer%]"
        );
    }

    private boolean required;
    private Expression<String> exprId;
    private Expression<Integer> exprUniqueId;
    private Expression<Integer> exprMinSize;
    private Expression<Integer> exprMaxSize;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        required = parseResult.hasTag("required");
        exprId = (Expression<String>) expressions[0];
        exprUniqueId = (Expression<Integer>) expressions[1];
        exprMinSize = (Expression<Integer>) expressions[2];
        exprMaxSize = (Expression<Integer>) expressions[3];
        return true;
    }

    @Override
    protected AttachmentUpload.Builder @Nullable [] get(Event event) {
        final var id = exprId.getSingle(event);
        if (id == null)
            return new AttachmentUpload.Builder[0];
        final var uniqueId = EasyElement.parseSingle(exprUniqueId, event);
        final var minSize = EasyElement.parseSingle(exprMinSize, event);
        final var maxSize = EasyElement.parseSingle(exprMaxSize, event);

        final var builder = AttachmentUpload.create(id)
                .setRequired(required);
        if (uniqueId != null)
            builder.setUniqueId(uniqueId);
        if (minSize != null)
            builder.setMinValues(minSize);
        if (maxSize != null)
            builder.setMaxValues(maxSize);
        return new AttachmentUpload.Builder[]{builder};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends AttachmentUpload.Builder> getReturnType() {
        return AttachmentUpload.Builder.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "new attachment upload builder with id " + exprId.toString(event, debug)
                + (exprUniqueId != null ? " and unique id " + exprUniqueId.toString(event, debug) : "")
                + (exprMinSize != null ? " and min size " + exprMinSize.toString(event, debug) : "")
                + (exprMaxSize != null ? " and max size " + exprMaxSize.toString(event, debug) : "");
    }
}
