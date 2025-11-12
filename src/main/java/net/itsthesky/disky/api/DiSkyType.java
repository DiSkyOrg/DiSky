package net.itsthesky.disky.api;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.expressions.base.EventValueExpression;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import lombok.Getter;
import net.dv8tion.jda.api.components.Component;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * @author ItsTheSky
 */
@Getter
public class DiSkyType<T> {

    public static final List<DiSkyType<?>> DISKY_CLASSES = new ArrayList<>();

    private final Class<T> clazz;
    private final String codeName;
    private final String user;
    private final Function<T, String> toString;
    private final Function<String, T> parser;
    private final boolean isEnum;
    private final ClassInfo<T> classInfo;

    private final List<Class> docsSeeAlso = new ArrayList<>();

    private @Nullable Function<String, RestAction<T>> restParser;

    public DiSkyType(Class<T> clazz, String codeName, Function<T, String> toString, @Nullable Function<String, T> parser) {
        this(clazz, codeName, codeName, toString, parser, false);
    }

    public DiSkyType(Class<T> clazz, String codeName, Function<T, String> toString, @Nullable Function<String, T> parser, boolean allowFullParsing) {
        this(clazz, codeName, codeName, toString, parser, allowFullParsing);
    }

    public DiSkyType(Class<T> clazz, String codeName, String user, Function<T, String> toString, @Nullable Function<String, T> parser, boolean isEnum) {
        this.clazz = clazz;
        this.codeName = codeName;
        this.user = user;
        this.toString = toString;
        this.parser = parser;
        this.isEnum = isEnum;
        this.classInfo = new DiSkyTypeWrapper<>(clazz, codeName)
                .diskyType(this)
                .user(user)
                .supplier(isEnum ? clazz.getEnumConstants() : (T[]) new Object[0])
                .parser(new Parser<T>() {
                    @Override
                    public @NotNull T parse(final @NotNull String input, final @NotNull ParseContext context) {
                        if (parser == null)
                            return null;
                        if (isEnum) {
                            try {
                                return parser.apply(input);
                            } catch (Exception ignored) {
                                return null;
                            }
                        } else {
                            if (context.equals(ParseContext.COMMAND))
                                return parser.apply(DiSkyType.this.parse(input));
                            return null;
                        }
                    }

                    @Override
                    public boolean canParse(@NotNull ParseContext context) {
                        if (parser == null) return false;
                        if (isEnum) return true;
                        return context.equals(ParseContext.COMMAND);
                    }

                    @Override
                    public @NotNull String toString(T entity, int flags) {
                        try {
                            return toString.apply(entity);
                        } catch (Exception ex) {
                            return entity.toString();
                        }
                    }

                    @Override
                    public @NotNull String toVariableNameString(T entity) {
                        if (entity instanceof final ISnowflake snowflake)
                            return snowflake.getId();
                        else if (entity instanceof final Component component)
                            return Integer.toString(component.getUniqueId());
                        else if (entity instanceof final Enum<?> enumValue)
                            return enumValue.name().toLowerCase(Locale.ROOT).replaceAll("_", " ");

                        return toString(entity, 0);
                    }
                });
    }

    @SuppressWarnings("unchecked")
    public static <T extends Enum> Parser<T> createParser(Class<T> enumClass) {
        return new Parser<T>() {
            @Override
            public @NotNull String toString(T o, int flags) {
                return o.name().toLowerCase(Locale.ROOT).replaceAll("_", " ");
            }

            @Override
            public @NotNull String toVariableNameString(T o) {
                return toString(o, 0);
            }

            @Override
            public boolean canParse(@NotNull ParseContext context) {
                return true;
            }

            @Override
            public T parse(@NotNull String s, @NotNull ParseContext context) {
                return (T) Enum.valueOf(enumClass, s.toUpperCase(Locale.ROOT).replaceAll(" ", "_"));
            }
        };
    }

    public static <T extends Enum<?>> DiSkyType<T> fromEnum(Class<T> enumClass, String typeName, String user) {
        return fromEnum(enumClass, typeName, user, null);
    }

    public static <T extends Enum<?>> DiSkyType<T> fromEnum(Class<T> enumClass, String typeName, String user,
                                                            @Nullable String suffix) {
        final DiSkyType<T> type = new DiSkyType<>(
                enumClass,
                typeName,
                user,
                entity -> entity.name().toLowerCase(Locale.ROOT).replaceAll("_", " "),
                input -> {
                    if (suffix != null && !input.endsWith(suffix))
                        return null;
                    if (suffix != null)
                        input = input.substring(0, input.length() - suffix.length());

                    try {
                        return ReflectionUtils.invokeMethodEx(enumClass, "valueOf", null, input.toUpperCase(Locale.ROOT).replaceAll(" ", "_"));
                    } catch (Exception ex) {
                        return null;
                    }
                },
                true
        );

        final String[] formatted = Arrays
                .stream(enumClass.getEnumConstants())
                .map(value -> value.name().replaceAll("_", " ").toLowerCase(Locale.ROOT))
                .map(value -> {
                    if (suffix != null)
                        return value + suffix;

                    return value;
                })
                .toArray(String[]::new);
        type.classInfo.examples(String.join(", ", formatted));
        return type;
    }

    private String parse(String input) {
        return input.replaceAll("[@{}<>&!#~]+", "");
    }

    // Should never be used outside of the class btw
    public void register(ClassInfo<T> classInfo) {
        DISKY_CLASSES.add(this);
        Classes.registerClass(classInfo);
    }

    public DiSkyType<T> documentation(String name, String description, Class... seeAlso) {
        this.classInfo.name(name);
        this.classInfo.description(description);
        this.docsSeeAlso.addAll(Arrays.asList(seeAlso));
        return this;
    }

    public void register() {
        register(this.classInfo);
    }

    public void register(Consumer<ClassInfo<T>> consumer) {
        consumer.accept(this.classInfo);
        register();
    }

    public DiSkyType<T> eventExpression() {
        this.classInfo.defaultExpression(new EventValueExpression<>(this.clazz));
        return this;
    }

    public DiSkyType<T> createRestParser(Function<String, RestAction<T>> parser) {
        this.restParser = parser;
        return this;
    }

    public Class<? extends Enum> getEnumClass() {
        return (Class<? extends Enum>) clazz;
    }

    public @Nullable Function<String, RestAction<T>> getRestParser() {
        return restParser;
    }

    @Getter
    public static class DiSkyTypeWrapper<T> extends ClassInfo<T> {

        private DiSkyType<T> diSkyType;

        /**
         * @param c        The class
         * @param codeName The name used in patterns
         */
        public DiSkyTypeWrapper(Class<T> c, String codeName) {
            super(c, codeName);
        }

        public DiSkyTypeWrapper<T> diskyType(DiSkyType<T> diSkyType) {
            this.diSkyType = diSkyType;
            return this;
        }
    }
}