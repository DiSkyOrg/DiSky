package info.itsthesky.disky.elements.sections;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.Section;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.lang.parser.ParserInstance;
import ch.njol.skript.lang.util.SimpleLiteral;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.api.datastruct.DataStructureFactory;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;

import java.util.List;
import java.util.Map;

public class CreateStructSection extends Section {

    static {
        Skript.registerSection(
                CreateStructSection.class,
                "create structure [a] [new] <.+> and store (it|the result) in %object%"
        );
    }

    private Expression<?> exprVariable;
    private DataStructureFactory.DataStructureInfo structureInfo;

    private EntryContainer container;
    private Map<String, Expression<?>> entries = new java.util.HashMap<>();

    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.@NotNull ParseResult parseResult, @NotNull SectionNode sectionNode, @NotNull List<TriggerItem> triggerItems) {
        exprVariable = expressions[0];

        final String name = parseResult.regexes.get(0).group();
        structureInfo = DataStructureFactory.getInstance().getStructure(name);
        if (structureInfo == null) {
            Skript.error("The data structure named '" + name + "' doesn't exist!");
            return false;
        }

        final var validator = structureInfo.validator();
        container = validator.validate(sectionNode);
        if (container == null) {
            Skript.error("The data structure '" + name + "' is not correctly defined!");
            return false;
        }

        for (var entry : structureInfo.entries()) {
            @Nullable Expression<?> rawValue;
            try {
                rawValue = (Expression<?>) container.getOptional(entry.name(), false);
            } catch (Exception e) {
                Skript.error("The entry '" + entry.name() + "' throw an error while parsing in the data structure '" + structureInfo.name() + "':");
                e.printStackTrace();
                return false;
            }

            if (rawValue == null && !entry.optional()) {
                Skript.error("The entry '" + entry.name() + "' is required in the data structure '" + structureInfo.name() + "'!");
                return false;
            }

            if (rawValue == null && entry.defaultValue() != null)
                rawValue = new SimpleLiteral<>(entry.defaultValue(), false);

            entries.put(entry.name(), rawValue);

                /*@Nullable Object value;
                if (rawValue instanceof final Expression expression) {
                    value = expression.getSingle(event);
                } else if (rawValue instanceof final List list) {
                    // it's a list of expressions, we have to construct a new list with the parsed stuff
                    value = (List<Object>) list.stream()
                            .map(expr -> ((Expression<?>) expr).getSingle(event))
                            .toList();
                } else {
                    value = null;
                }

                if (value == null && !entry.optional()) {
                    Skript.error("The entry '" + entry.name() + "' is required in the data structure '" + structureInfo.name() + "'!");
                    return getNext();
                }

                if (value == null && entry.defaultValue() != null) {
                    value = entry.defaultValue();
                }

                field.set(instance, value);*/
        }

        return Changer.ChangerUtils.acceptsChange(exprVariable, Changer.ChangeMode.SET, structureInfo.returnedClazz());
    }

    @Override
    @Nullable
    protected TriggerItem walk(@NotNull Event event) {
        if (container == null)
            return null;

        try {
            final Object instance = structureInfo.structureClazz().newInstance();
            for (var entry : structureInfo.entries()) {
                final Expression<?> rawValue = entries.get(entry.name());
                Object value;
                /*if (rawValue instanceof final Expression expression) {
                    value = expression.getSingle(event);
                } else if (rawValue instanceof final List list) {
                    // it's a list of expressions, we have to construct a new list with the parsed stuff
                    value = (List<Object>) list.stream()
                            .map(expr -> ((Expression<?>) expr).getSingle(event))
                            .toList();
                } else {
                    value = null;
                }*/

                value = rawValue == null ? null : rawValue.getSingle(event);
                if (value == null && !entry.optional()) {
                    Skript.error("The entry '" + entry.name() + "' is required in the data structure '" + structureInfo.name() + "'!");
                    return getNext();
                }

                if (value == null && entry.defaultValue() != null)
                    value = entry.defaultValue();

                final var field = structureInfo.structureClazz().getField(entry.fieldName());
                field.set(instance, value);
            }

            final var buildMethod = structureInfo.structureClazz().getDeclaredMethod("build");
            final var result = buildMethod.invoke(instance);

            exprVariable.change(event, new Object[] {result}, Changer.ChangeMode.SET);
        } catch (Exception ex) {
            Skript.error("Cannot create a new instance of the data structure '" + structureInfo.name() + "':");
            ex.printStackTrace();
        }

        return getNext();
    }

    @Override
    public @NotNull String toString(@Nullable Event event, boolean debug) {
        return "create a new " + structureInfo.name() + " structure";
    }
}
