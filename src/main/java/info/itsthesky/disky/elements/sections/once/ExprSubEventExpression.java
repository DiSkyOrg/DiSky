package info.itsthesky.disky.elements.sections.once;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.doc.NoDoc;
import ch.njol.skript.expressions.base.EventValueExpression;
import ch.njol.skript.expressions.base.WrapperExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.util.Getter;
import ch.njol.skript.util.Utils;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import info.itsthesky.disky.DiSky;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.stream.Stream;

@NoDoc
public class ExprSubEventExpression extends WrapperExpression<Object> {

    static {
        Skript.registerExpression(ExprSubEventExpression.class, Object.class,
                ExpressionType.PROPERTY, "[the] outer event-%*classinfo%");// property so that it is parsed after most other expressions
    }

    public SecListenOnce secListenOnce;

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] exprs, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.ParseResult parser) {
        secListenOnce = getParser().getCurrentSection(SecListenOnce.class);
        if (secListenOnce == null) {
            Skript.error("The 'outer event' expression can only be used in a 'listen once' section.");
            return false;
        }

        ClassInfo<?> classInfo = ((Literal<ClassInfo<?>>) exprs[0]).getSingle();
        Class<?> c = classInfo.getC();

        boolean plural = Utils.getEnglishPlural(parser.expr).getSecond();
        EventValueExpression<?> eventValue = new EventValueExpression<>(plural ? CollectionUtils.arrayType(c) : c);
        setExpr(eventValue);

        var oldEvents = getParser().getCurrentEvents();
        getParser().setCurrentEvents(secListenOnce.getCurrentEvents());
        DiSky.debug("Current events: " + secListenOnce.getCurrentEvents().length + " [" + Stream.of(secListenOnce.getCurrentEvents()).map(Class::getSimpleName).reduce((a, b) -> a + ", " + b) + "]");
        boolean succeed = eventValue.init();
        getParser().setCurrentEvents(oldEvents);

        return succeed;
    }

    @Override
    protected Object @NotNull [] get(@NotNull Event event) {
        assert secListenOnce.getOuterEvent() != null;
        var evtExpr = (EventValueExpression<?>) getExpr();
        try {
            var field = evtExpr.getClass().getDeclaredField("getters");
            field.setAccessible(true);
            var getters = (Map<Class<? extends Event>, Getter>) field.get(evtExpr);
            getters.forEach((k, v) -> {
                DiSky.debug("Getter: " + k.getSimpleName() + " / " + v);
            });
            DiSky.debug("given event class: " + secListenOnce.getOuterEvent().getClass().getSimpleName() + " / " + event.getClass().getSimpleName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return getExpr().getArray(secListenOnce.getOuterEvent());
    }

    @Override
    public @NotNull String toString(@Nullable Event event, boolean debug) {
        return getExpr().toString(event, debug);
    }

}
