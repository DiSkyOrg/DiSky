package net.itsthesky.disky.elements.components.create;

import ch.njol.skript.doc.Since;
import net.dv8tion.jda.api.components.checkboxgroup.CheckboxGroupOption;
import net.dv8tion.jda.api.components.radiogroup.RadioGroupOption;
import net.itsthesky.disky.api.DiSkyRegistry;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import net.dv8tion.jda.api.components.selections.SelectOption;
import net.itsthesky.disky.api.emojis.Emote;
import net.itsthesky.disky.api.skript.EasyElement;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("New Component (Dropdown / Checkbox / Radio) Option")
@Description({"Create a new dropdown/checkbox/radio option with different properties.",
        "This is a predefined option holding a string value. It can only be used in string dropdowns.",
        "The value represent the returned string that this dropdown will return if this option is selected.",
        "The name / label is the actual shown name on the option.",
        "Description and emote are optional. They will be ignored for checkbox and radio options, as they are only supported in dropdowns."})
@Examples("set {_btn} to new enabled danger button with id \"button-id\" named \"Hello world :p\"")
@Since("4.28.0")
public class ExprNewComponentOption extends SimpleExpression<Object> {

    static {
        DiSkyRegistry.registerExpression(ExprNewComponentOption.class,
                Object.class,
                ExpressionType.COMBINED,
                "[a] new [:default] [:(dropdown|checkbox|radio)] option with value %string% (named|with label) %-string% [with description [%-string%]] [with [emoji] %-emote%]");
    }

    private enum Type {
        DROPDOWN, CHECKBOX, RADIO
    }

    private Expression<String> exprValue;
    private Expression<String> exprName;
    private Expression<String> exprDesc;
    private Expression<Emote> exprEmote;
    private boolean isDefault;
    private Type type;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, ParseResult parseResult) {
        exprValue = (Expression<String>) exprs[0];
        exprName = (Expression<String>) exprs[1];
        exprDesc = (Expression<String>) exprs[2];
        exprEmote = (Expression<Emote>) exprs[3];
        isDefault = parseResult.hasTag("default");

        for (Type t : Type.values()) {
            if (parseResult.hasTag(t.name().toLowerCase())) {
                type = t;
                break;
            }
        }

        return type != null;
    }

    @Override
    protected Object @NotNull [] get(@NotNull Event e) {
        final String value = EasyElement.parseSingle(exprValue, e, null);
        final String name = EasyElement.parseSingle(exprName, e, null);
        final @Nullable String desc = EasyElement.parseSingle(exprDesc, e, null);
        final @Nullable Emote emote = EasyElement.parseSingle(exprEmote, e, null);
        if (EasyElement.anyNull(this, value, name))
            return new SelectOption[0];

        if (type == Type.DROPDOWN) {
            SelectOption option = SelectOption
                    .of(name, value)
                    .withDefault(isDefault);

            if (desc != null)
                option = option.withDescription(desc);
            if (emote != null)
                option = option.withEmoji(emote.getEmoji());

            return new SelectOption[]{option};
        } else if (type == Type.CHECKBOX) {
            return new CheckboxGroupOption[]{
                    CheckboxGroupOption
                            .of(name, value)
                            .withDefault(isDefault)};
        } else if (type == Type.RADIO) {
            return new RadioGroupOption[]{
                    RadioGroupOption
                            .of(name, value)
                            .withDefault(isDefault)};
        }

        return new Object[0];
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<?> getReturnType() {
        return Object.class;
    }

    @Override
    public @NotNull String toString(Event e, boolean debug) {
        return "new " + (isDefault ? "default " : "") + type.name().toLowerCase() + " option with value " + exprValue.toString(e, debug) +
                " named " + exprName.toString(e, debug) +
                (exprDesc != null ? " with description " + exprDesc.toString(e, debug) : "") +
                (exprEmote != null ? " with emoji " + exprEmote.toString(e, debug) : "");
    }
}