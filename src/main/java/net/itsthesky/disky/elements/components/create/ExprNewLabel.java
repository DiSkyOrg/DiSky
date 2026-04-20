package net.itsthesky.disky.elements.components.create;

/*
 * DiSky
 * Copyright (C) 2025 ItsTheSky
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import net.dv8tion.jda.api.components.attachmentupload.AttachmentUpload;
import net.dv8tion.jda.api.components.label.LabelChildComponent;
import net.itsthesky.disky.api.DiSkyRegistry;
import ch.njol.skript.config.Node;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import net.dv8tion.jda.api.components.ModalTopLevelComponent;
import net.dv8tion.jda.api.components.label.Label;
import net.dv8tion.jda.api.components.selections.SelectMenu;
import net.dv8tion.jda.api.components.textinput.TextInput;
import net.itsthesky.disky.api.generator.SeeAlso;
import net.itsthesky.disky.elements.sections.handler.DiSkyRuntimeHandler;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("New Model Label")
@Description("Create a new label component for modals. Labels are components that can be used to display text and a child component (like a text input or a select menu or an attachment input) in a modal.")
@Examples({
        """
        # Create a short text input
        set {_input} to new short text input with id "title"
        set placeholder of {_input} to "Enter a brief title..."
        set minimum range of {_input} to 3
        set maximum range of {_input} to 50
        set required state of {_input} to true
        
        # Wrap in a label
        set {_label} to new label "Feedback Title" with {_input}
        
        # Then adds it to the modal
        add {_label} to rows of {_modal}
        """,
        """
        set {_att} to new attachment upload builder with id "test" min size 3 and max size 10
        set {_label} to new label "File?" with {_att}
        add {_label} to rows of {_modal}
        """
})
@Since("4.25.0")
@SeeAlso({ExprNewAttachmentUpload.class, ExprNewInput.class, ExprNewDropdown.class, ExprNewModal.class})
public class ExprNewLabel extends SimpleExpression<ModalTopLevelComponent> {

    static {
        DiSkyRegistry.registerExpression(
                ExprNewLabel.class,
                ModalTopLevelComponent.class,
                ExpressionType.COMBINED,
                "[a] [new] label [with] [the] [(label|title)] %string% [and] with [the] [(child|component)] %object% [[and] with [the] description %-string%]"
        );
    }

    private Expression<String> exprLabel;
    private Expression<Object> exprComponent;
    private Expression<String> exprDesc;
    private Node node;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.node = getParser().getNode();

        this.exprLabel = (Expression<String>) expressions[0];
        this.exprComponent = (Expression<Object>) expressions[1];
        this.exprDesc = (Expression<String>) expressions[2];

        return true;
    }

    @Override
    protected ModalTopLevelComponent @Nullable [] get(Event event) {
        final var label = exprLabel.getSingle(event);
        final var component = exprComponent.getSingle(event);
        final var desc = exprDesc != null ? exprDesc.getSingle(event) : null;
        if (!DiSkyRuntimeHandler.checkSet(node, label, exprLabel, component, exprComponent))
            return new ModalTopLevelComponent[0];

        final LabelChildComponent child;
        switch (component) {
            case final TextInput.Builder textBuilder -> child = textBuilder.build();
            case final SelectMenu.Builder selectBuilder -> child = selectBuilder.build();
            case AttachmentUpload.Builder attachmentBuilder -> child = attachmentBuilder.build();
            case null, default -> {
                DiSkyRuntimeHandler.error(new IllegalStateException("The component provided cannot fit into a label (only text inputs & select menus are allowed)"), node);
                return new ModalTopLevelComponent[0];
            }
        }

        final var lbl = Label.of(label, desc, child);
        return new ModalTopLevelComponent[] {lbl};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends ModalTopLevelComponent> getReturnType() {
        return ModalTopLevelComponent.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "new label with label " + exprLabel.toString(event, debug)
                + " and component " + exprComponent.toString(event, debug)
                + (exprDesc == null ? "" : " and description " + exprDesc.toString(event, debug));
    }
}
