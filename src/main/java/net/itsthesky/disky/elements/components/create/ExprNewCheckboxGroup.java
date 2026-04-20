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
import net.dv8tion.jda.api.components.checkboxgroup.CheckboxGroup;
import net.dv8tion.jda.api.components.checkboxgroup.CheckboxGroupOption;
import net.itsthesky.disky.api.DiSkyRegistry;
import net.itsthesky.disky.api.generator.SeeAlso;
import net.itsthesky.disky.api.skript.EasyElement;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("New Checkbox Group")
@Description("Create a new checkbox group component builder. A checkbox group is a component that allows users to select multiple options from a list of checkboxes. It can be used in modals only, as a child of a label.")
@Examples({
        """
        set {_checks} to new checkbox group with id "test"
        add (new checkbox option with value "option1" named "Option 1") to options of {_checks}
        add (new default checkbox option with value "option2" named "Option 2") to options of {_checks}
        set {_label} to new label "Choose an option:" with {_checks}
        add {_label} to rows of {_modal}
        """
})
@Since("4.28.0")
@SeeAlso({ExprNewLabel.class, ExprNewModal.class, CheckboxGroupOption.class, ExprNewComponentOption.class})
public class ExprNewCheckboxGroup extends SimpleExpression<CheckboxGroup.Builder> {

    static {
        DiSkyRegistry.registerExpression(
                ExprNewCheckboxGroup.class,
                CheckboxGroup.Builder.class,
                ExpressionType.COMBINED,
                "new checkbox group with [the] id %string% [[and] [with] unique id %-integer%] [[and] [with] min [value] %-integer%] [[and] [with] max [value] %-integer%]"
        );
    }

    private Expression<String> exprId;
    private Expression<Integer> exprUniqueId;
    private Expression<Integer> exprMinValue;
    private Expression<Integer> exprMaxValue;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        exprId = (Expression<String>) expressions[0];
        exprUniqueId = (Expression<Integer>) expressions[1];
        exprMinValue = (Expression<Integer>) expressions[2];
        exprMaxValue = (Expression<Integer>) expressions[3];
        return true;
    }

    @Override
    protected CheckboxGroup.Builder @Nullable [] get(Event event) {
        final var id = exprId.getSingle(event);
        if (id == null)
            return null;

        final var uniqueId = EasyElement.parseSingle(exprUniqueId, event, null);
        final var minValue = EasyElement.parseSingle(exprMinValue, event, null);
        final var maxValue = EasyElement.parseSingle(exprMaxValue, event, null);

        final var builder = CheckboxGroup.create(id);

        if (uniqueId != null) builder.setUniqueId(uniqueId);
        if (minValue != null) builder.setMinValues(minValue);
        if (maxValue != null) builder.setMaxValues(maxValue);

        return new CheckboxGroup.Builder[] {builder};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends CheckboxGroup.Builder> getReturnType() {
        return CheckboxGroup.Builder.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "new checkbox group with id " + exprId.toString(event, debug) +
                (exprUniqueId != null ? " and unique id " + exprUniqueId.toString(event, debug) : "") +
                (exprMinValue != null ? " and min value " + exprMinValue.toString(event, debug) : "") +
                (exprMaxValue != null ? " and max value " + exprMaxValue.toString(event, debug) : "");
    }
}
