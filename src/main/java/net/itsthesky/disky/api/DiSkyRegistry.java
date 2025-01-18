package net.itsthesky.disky.api;

import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.*;
import net.itsthesky.disky.DiSky;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxOrigin;
import org.skriptlang.skript.registration.SyntaxRegistry;

public final class DiSkyRegistry {

    public static <E extends Expression<T>, T> void registerExpression(
            Class<E> expressionType, Class<T> returnType, ExpressionType type, SyntaxOrigin origin, String... patterns
    ) throws IllegalArgumentException {
        var addon = DiSky.getAddonInstance();
        addon.syntaxRegistry().register(SyntaxRegistry.EXPRESSION, SyntaxInfo.Expression.builder(expressionType, returnType)
                .priority(type.priority())
                .origin(origin)
                .addPatterns(patterns)
                .build()
        );
    }

    public static <E extends Expression<T>, T> void registerExpression(
            Class<E> expressionType, Class<T> returnType, ExpressionType type, String... patterns
    ) throws IllegalArgumentException {
        registerExpression(expressionType, returnType, type, SyntaxOrigin.of(DiSky.getAddonInstance()), patterns);
    }

    public static <E extends Condition> void registerCondition(Class<E> conditionClass, Condition.ConditionType type, SyntaxOrigin origin, String... patterns) throws IllegalArgumentException {
        var addon = DiSky.getAddonInstance();
        addon.syntaxRegistry().register(SyntaxRegistry.CONDITION, SyntaxInfo.builder(conditionClass)
                .priority(type.priority())
                .origin(origin)
                .addPatterns(patterns)
                .build()
        );
    }

    public static <E extends Effect> void registerEffect(Class<E> effectClass, SyntaxOrigin origin, String... patterns) throws IllegalArgumentException {
        var addon = DiSky.getAddonInstance();
        addon.syntaxRegistry().register(SyntaxRegistry.EFFECT, SyntaxInfo.builder(effectClass)
                .origin(origin)
                .addPatterns(patterns)
                .build()
        );
    }

    public static <E extends Effect> void registerEffect(Class<E> effectClass, String... patterns) throws IllegalArgumentException {
        registerEffect(effectClass, SyntaxOrigin.of(DiSky.getAddonInstance()), patterns);
    }

    public static <E extends Section> void registerSection(Class<E> sectionClass, SyntaxOrigin origin, String... patterns) throws IllegalArgumentException {
        var addon = DiSky.getAddonInstance();
        addon.syntaxRegistry().register(SyntaxRegistry.SECTION, SyntaxInfo.builder(sectionClass)
                .origin(origin)
                .addPatterns(patterns)
                .build()
        );
    }

    public static <E extends Section> void registerSection(Class<E> sectionClass, String... patterns) throws IllegalArgumentException {
        registerSection(sectionClass, SyntaxOrigin.of(DiSky.getAddonInstance()), patterns);
    }

    public static <E extends Condition> void registerCondition(Class<E> conditionClass, Condition.ConditionType type, String... patterns) throws IllegalArgumentException {
        registerCondition(conditionClass, type, SyntaxOrigin.of(DiSky.getAddonInstance()), patterns);
    }

    public static boolean unregisterElement(SyntaxRegistry.Key syntaxKey, Class<?> element) {
        try {
            var reg = DiSky.getAddonInstance().syntaxRegistry();

            for (var entry : reg.elements()) {
                if (entry.type().equals(element)) {
                    reg.unregister(syntaxKey, entry);
                    return true;
                }
            }

            DiSky.debug("The element " + element.getSimpleName() + " is not registered in the Skript registry.");
            return false;
        } catch (Exception e) {
            DiSky.debug("An error occurred while trying to unregister the element " + element.getSimpleName() + " from the Skript registry:");
            e.printStackTrace();
            return false;
        }
    }

    public static <T> void registerProperty(Class<? extends Expression<T>> expressionClass, Class<T> type, String property, String fromType) {
        registerExpression(expressionClass, type, ExpressionType.PROPERTY, PropertyExpression.getPatterns(property, fromType));
    }

    public static void registerPropertyCondition(Class<? extends Condition> condition, String property, String type) {
        registerCondition(condition, Condition.ConditionType.PROPERTY,
                PropertyCondition.getPatterns(PropertyCondition.PropertyType.BE, property, type));
    }

}
