package net.itsthesky.disky.elements.components.create;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import net.itsthesky.disky.api.emojis.Emote;
import net.itsthesky.disky.api.skript.EasyElement;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("New Dropdown Option")
@Description({"Create a new dropdown option with different properties.",
        "This is a predefined option holding a string value. It can only be used in string dropdowns.",
        "The value represent the returned string that this dropdown will return if this option is selected.",
        "The name / label is the actual shown name on the option.",
        "Description and emote are optional."})
@Examples("set {_btn} to new enabled danger button with id \"button-id\" named \"Hello world :p\"")
public class ExprNewDropdownOption extends SimpleExpression<SelectOption> {
    
    static {
        Skript.registerExpression(ExprNewDropdownOption.class, SelectOption.class, ExpressionType.SIMPLE,
                "[a] new [default] [dropdown] option with value %string% (named|with label) %-string% [with description [%-string%]] [with [emoji] %-emote%]");
    }

    private Expression<String> exprValue;
    private Expression<String> exprName;
    private Expression<String> exprDesc;
    private Expression<Emote> exprEmote;
    private boolean isDefault;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, ParseResult parseResult) {
        exprValue = (Expression<String>) exprs[0];
        exprName = (Expression<String>) exprs[1];
        exprDesc = (Expression<String>) exprs[2];
        exprEmote = (Expression<Emote>) exprs[3];
        isDefault = parseResult.expr.contains("new default");
        return true;
    }

    @Override
    protected SelectOption @NotNull [] get(@NotNull Event e) {
        final String value = EasyElement.parseSingle(exprValue, e, null);
        final String name = EasyElement.parseSingle(exprName, e, null);
        final @Nullable String desc = EasyElement.parseSingle(exprDesc, e, null);
        final @Nullable Emote emote = EasyElement.parseSingle(exprEmote, e, null);
        if (EasyElement.anyNull(this, value, name))
            return new SelectOption[0];

        SelectOption option = SelectOption
                .of(name, value)
                .withDefault(isDefault);

        if (desc != null)
            option = option.withDescription(desc);
        if (emote != null)
            option = option.withEmoji(emote.getEmoji());

        return new SelectOption[] {option};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<? extends SelectOption> getReturnType() {
        return SelectOption.class;
    }

    @Override
    public @NotNull String toString(Event e, boolean debug) {
        return "new dropdown option with value " + exprValue.toString(e, debug) + " named " + exprName.toString(e, debug);
    }
}