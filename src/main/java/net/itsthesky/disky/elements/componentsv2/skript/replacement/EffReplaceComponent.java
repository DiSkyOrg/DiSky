package net.itsthesky.disky.elements.componentsv2.skript.replacement;

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
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import net.itsthesky.disky.elements.componentsv2.base.INewComponentBuilder;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

public class EffReplaceComponent extends Effect {

    static {
        Skript.registerEffect(
            EffReplaceComponent.class,
            "replace [the] component[s] with [the] [components] %newcomponent%",
                "(remove|delete) [the] component[s]"
        );
    }

    private SecApplyReplacement parentSection;
    private Expression<Object> exprNewComponent;
    private boolean isRemove;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        if (!getParser().isCurrentSection(SecApplyReplacement.class)) {
            Skript.error("The 'replace components' effect can only be used inside a 'apply replacement' section.");
            return false;
        }
        isRemove = matchedPattern == 1;

        if (!isRemove)
            exprNewComponent = (Expression<Object>) expressions[0];

        parentSection = getParser().getCurrentSection(SecApplyReplacement.class);
        return true;
    }

    @Override
    protected void execute(Event event) {
        if (isRemove)
            parentSection.setNextNewComponent(null);
        else
            parentSection.setNextNewComponent((INewComponentBuilder<?>) exprNewComponent.getSingle(event));

        parentSection.setHasChanged(true);
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "replace components with " + exprNewComponent.toString(event, debug);
    }
}
