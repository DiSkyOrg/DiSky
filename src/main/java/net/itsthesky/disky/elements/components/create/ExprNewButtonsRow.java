package net.itsthesky.disky.elements.components.create;

import ch.njol.skript.Skript;
import net.itsthesky.disky.api.DiSkyRegistry;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import net.dv8tion.jda.api.components.buttons.Button;
import net.itsthesky.disky.api.skript.EasyElement;
import net.itsthesky.disky.elements.components.core.ComponentRow;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

@Name("New Components Row")
@Description({"Create a new (empty) components row.",
        "You can add either max 5 buttons or one dropdown to it.",
        "A single message can hold 5 components rows."})
public class ExprNewButtonsRow extends SimpleExpression<ComponentRow> {

    static {
        DiSkyRegistry.registerExpression(
                ExprNewButtonsRow.class,
                ComponentRow.class,
                ExpressionType.COMBINED,
                "[a] new component[s] row [with [the] [buttons[s]] %-buttons%]"
        );
    }

    private Expression<Button> exprButtons;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
        exprButtons = (Expression<Button>) exprs[0];
        return true;
    }

    @Override
    protected ComponentRow @NotNull [] get(@NotNull Event e) {
        final var buttons = EasyElement.parseList(exprButtons, e, new Button[0]);
        final var row = new ComponentRow();

        for (Button button : buttons)
            row.add(button);

        return new ComponentRow[]{row};
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