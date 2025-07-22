package net.itsthesky.disky.elements.componentsv2.skript;

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

import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.util.Kleenean;
import net.itsthesky.disky.api.skript.EasyElement;
import net.itsthesky.disky.api.skript.ReturningSection;
import net.itsthesky.disky.elements.componentsv2.base.ContainerBuilder;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SecNewContainer extends ReturningSection<ContainerBuilder> {

    public static class container extends LastBuilderExpression<ContainerBuilder, SecNewContainer> { }

    static {
        register(
                SecNewContainer.class,
                ContainerBuilder.class,
                container.class,
                "(make|create) [a] [new] [discord] container [with [unique] id %-integer%]"
        );
    }

    private Expression<Number> exprId;

    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.@NotNull ParseResult parseResult, @NotNull SectionNode sectionNode, @NotNull List<TriggerItem> triggerItems) {
        exprId = (Expression<Number>) exprs[0];
        return super.init(exprs, matchedPattern, isDelayed, parseResult, sectionNode, triggerItems);
    }

    @Override
    public ContainerBuilder createNewValue(@NotNull Event event) {
        final var id = EasyElement.parseSingle(exprId, event, -1);
        return new ContainerBuilder(id.intValue());
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "create new container";
    }

}
