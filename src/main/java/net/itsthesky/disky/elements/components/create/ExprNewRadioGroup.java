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
import net.dv8tion.jda.api.components.radiogroup.RadioGroup;
import net.dv8tion.jda.api.components.radiogroup.RadioGroupOption;
import net.itsthesky.disky.api.DiSkyRegistry;
import net.itsthesky.disky.api.generator.SeeAlso;
import net.itsthesky.disky.api.skript.EasyElement;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("New Radio Group")
@Description("Create a new radio group component builder. A radio group is a component that allows users to select a single option from a list of radio buttons. It can be used in modals only, as a child of a label.")
@Examples({
        """
        set {_radio} to new radio group with id "test2"
        add (new radio option with value "option1" named "Option 1") to options of {_radio}
        add (new default radio option with value "option2" named "Option 2") to options of {_radio}
        set {_label} to new label "Choose another option:" with {_radio}
        add {_label} to rows of {_modal}
        """
})
@Since("4.28.0")
@SeeAlso({ExprNewLabel.class, ExprNewModal.class, RadioGroupOption.class, ExprNewComponentOption.class})
public class ExprNewRadioGroup extends SimpleExpression<RadioGroup.Builder> {

    static {
        DiSkyRegistry.registerExpression(
                ExprNewRadioGroup.class,
                RadioGroup.Builder.class,
                ExpressionType.COMBINED,
                "new radio group with [the] id %string% [[and] [with] unique id %-integer%]"
        );
    }

    private Expression<String> exprId;
    private Expression<Integer> exprUniqueId;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        exprId = (Expression<String>) expressions[0];
        exprUniqueId = (Expression<Integer>) expressions[1];
        return true;
    }

    @Override
    protected RadioGroup.Builder @Nullable [] get(Event event) {
        final var id = exprId.getSingle(event);
        if (id == null)
            return null;

        final var uniqueId = EasyElement.parseSingle(exprUniqueId, event, null);

        final var builder = RadioGroup.create(id);

        if (uniqueId != null) builder.setUniqueId(uniqueId);

        return new RadioGroup.Builder[] {builder};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends RadioGroup.Builder> getReturnType() {
        return RadioGroup.Builder.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "new radio group with id " + exprId.toString(event, debug) +
                (exprUniqueId != null ? " and unique id " + exprUniqueId.toString(event, debug) : "");
    }
}
