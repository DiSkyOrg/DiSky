package info.itsthesky.disky.elements.components.create;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.elements.components.core.ComponentRow;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

@Name("New Components Row")
@Description({"Create a new (empty) components row.",
        "You can add either max 5 buttons or one dropdown to it.",
        "A single message can hold 5 components rows."})
public class ExprNewButtonsRow extends SimpleExpression<ComponentRow> {

    static {
        Skript.registerExpression(ExprNewButtonsRow.class, ComponentRow.class, ExpressionType.SIMPLE,
                "[a] new component[s] row");
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull SkriptParser.ParseResult parseResult) {
        return true;
    }

    @Override
    protected ComponentRow @NotNull [] get(@NotNull Event e) {
        return new ComponentRow[] {new ComponentRow()};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<? extends ComponentRow> getReturnType() {
        return ComponentRow.class;
    }

    @Override
    public @NotNull String toString(Event e, boolean debug) {
        return "new components row";
    }
}