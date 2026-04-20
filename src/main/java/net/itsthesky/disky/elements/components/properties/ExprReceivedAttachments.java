package net.itsthesky.disky.elements.components.properties;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import net.dv8tion.jda.api.components.Component;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;
import net.itsthesky.disky.api.DiSkyRegistry;
import net.itsthesky.disky.api.generator.SeeAlso;
import net.itsthesky.disky.elements.components.create.ExprNewAttachmentUpload;
import net.itsthesky.disky.elements.components.create.ExprNewLabel;
import net.itsthesky.disky.elements.components.create.ExprNewModal;
import net.itsthesky.disky.elements.events.rework.ComponentEvents;
import net.itsthesky.disky.elements.sections.handler.DiSkyRuntimeHandler;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Received Attachments")
@Description("Get the attachments received from an attachment upload component in a modal. You have to precise the id of the component, and it will return a list of attachments (or an empty list if no attachment was received, or if the component with the specified id is not an attachment upload).")
@Examples({"""
        set {_att::*} to received attachments with id "test"
        loop {_att::*}:
            send "- %attachment file name of loop-value% (ext: %file ext of loop-value%)" to console
            download loop-value in folder "plugins/data/attachments/%attachment file name of loop-value%"
        reply with "Got attachments: %size of {_att::*}%\""""})
@Since("4.28.0")
@SeeAlso({ExprNewAttachmentUpload.class, ExprNewModal.class, ExprNewLabel.class})
public class ExprReceivedAttachments extends SimpleExpression<Message.Attachment> {

    static {
        DiSkyRegistry.registerExpression(
                ExprReceivedAttachments.class,
                Message.Attachment.class,
                ExpressionType.PROPERTY,
                "received [the] attachments with [the] id %string%"
        );
    }

    private Expression<String> exprId;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        exprId = (Expression<String>) expressions[0];
        return true;
    }

    @Override
    protected Message.Attachment @Nullable [] get(Event e) {
        final var id = exprId.getSingle(e);
        if (id == null)
            return new Message.Attachment[0];
        final var event = ComponentEvents.MODAL_INTERACTION_EVENT.getJDAEvent(e);
        if (event == null)
            return new Message.Attachment[0];

        final ModalMapping mapping = event.getValue(id);
        if (mapping == null)
            return new Message.Attachment[0];

        if (!mapping.getType().equals(Component.Type.FILE_UPLOAD)) {
            DiSkyRuntimeHandler.error(new IllegalStateException("Received value with id " + id + " is not an attachment upload component! (got " + mapping.getType() + ")"), getNode());
            return new Message.Attachment[0];
        }

        return mapping.getAsAttachmentList().toArray(new Message.Attachment[0]);
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public Class<? extends Message.Attachment> getReturnType() {
        return Message.Attachment.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "received attachments with id " + exprId.toString(event, debug);
    }
}
