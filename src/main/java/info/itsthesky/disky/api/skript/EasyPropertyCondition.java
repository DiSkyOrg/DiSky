package info.itsthesky.disky.api.skript;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAPIException;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Used to execute condition through a specific event (init available)
 * @author Sky (SkirpLang team for the base code)
 */
public abstract class EasyPropertyCondition<T> extends Condition {

    private Expression<? extends T> expr;

    private static PropertyCondition.PropertyType propertyType1;
    private static String property1;
    private static String type1;

    public static void register(Class<? extends Condition> c, PropertyCondition.PropertyType propertyType, String property, String type) {
        if (type.contains("%")) {
            throw new SkriptAPIException("The type argument must not contain any '%'s");
        } else {

            propertyType1 = propertyType;
            property1 = property;
            type1 = type;

            switch(propertyType) {
                case BE:
                    Skript.registerCondition(c, "%" + type + "% (is|are) " + property, "%" + type + "% (isn't|is not|aren't|are not) " + property);
                    break;
                case CAN:
                    Skript.registerCondition(c, "%" + type + "% can " + property, "%" + type + "% (can't|cannot|can not) " + property);
                    break;
                case HAVE:
                    Skript.registerCondition(c, "%" + type + "% (has|have) " + property, "%" + type + "% (doesn't|does not|do not|don't) have " + property);
                    break;
                default:
                    assert false;
            }
        }
    }

    @Override
    public boolean check(@NotNull Event e) {
        return check(e, expr.getSingle(e));
    }

    public abstract boolean check(Event e, T entity);

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return PropertyCondition.toString(this, propertyType1, e, debug, expr,
                property1);
    }

    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull SkriptParser.ParseResult parseResult) {
        expr = (Expression<? extends T>) exprs[0];
        this.setNegated(matchedPattern == 1);
        return true;
    }
}
