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
import ch.njol.skript.config.Node;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import net.dv8tion.jda.api.components.ModalTopLevelComponent;
import net.dv8tion.jda.api.components.attachmentupload.AttachmentUpload;
import net.dv8tion.jda.api.components.label.Label;
import net.dv8tion.jda.api.components.selections.SelectMenu;
import net.dv8tion.jda.api.components.textinput.TextInput;
import net.itsthesky.disky.elements.sections.handler.DiSkyRuntimeHandler;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

public class ExprNewLabel extends SimpleExpression<ModalTopLevelComponent> {

    static {
        Skript.registerExpression(
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

        final Label lbl;
        if (component instanceof final TextInput.Builder textBuilder)
            lbl = Label.of(label, desc, textBuilder.build());
        else if (component instanceof final SelectMenu.Builder selectBuilder)
            lbl = Label.of(label, desc, selectBuilder.build());
        else if (component instanceof final AttachmentUpload attachmentUpload)
            lbl = Label.of(label, desc, attachmentUpload);
        else {
            DiSkyRuntimeHandler.error(new IllegalStateException("The component provided cannot fit into a label (only text inputs, select menus, and attachment uploads are allowed)"), node);
            return new ModalTopLevelComponent[0];
        }

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
