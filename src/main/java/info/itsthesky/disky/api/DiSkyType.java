package info.itsthesky.disky.api;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

/**
 * @author ItsTheSky
 */
public class DiSkyType<T> {

    public static final List<DiSkyType<?>> DISKY_CLASSES = new ArrayList<>();

    private final Class<T> clazz;
    private final String codeName;
    private final String user;
    private final Function<T, String> toString;
    private final Function<String, T> parser;
    private final boolean isEnum;
    private final ClassInfo<T> classInfo;

    public DiSkyType(Class<T> clazz, String codeName, Function<T, String> toString, @Nullable Function<String, T> parser) {
        this(clazz, codeName, codeName, toString, parser, false);
    }

    public DiSkyType(Class<T> clazz, String codeName, String user, Function<T, String> toString, @Nullable Function<String, T> parser, boolean isEnum) {
        this.clazz = clazz;
        this.codeName = codeName;
        this.user = user;
        this.toString = toString;
        this.parser = parser;
        this.isEnum = isEnum;
        this.classInfo = new ClassInfo<>(clazz, codeName)
                .user(user)
                .parser(new Parser<T>() {
                    @Override
                    public @NotNull T parse(final @NotNull String input, final @NotNull ParseContext context) {
                        if (parser == null) return null;
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
                    public @NotNull String toString(T o, int flags) {
                        return toVariableNameString(o);
                    }

                    @Override
                    public @NotNull String toVariableNameString(T entity) {
                        try {
                            return toString.apply(entity);
                        } catch (Exception ex) {
                            return entity.toString();
                        }
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
        final DiSkyType<T> type = new DiSkyType<>(
                enumClass,
                typeName,
                user,
                entity -> entity.name().toLowerCase(Locale.ROOT).replaceAll("_", " "),
                input -> {
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

    public void register() {
        register(this.classInfo);
    }

    public boolean isEnum() {
        return isEnum;
    }

    public ClassInfo<T> getClassInfo() {
        return classInfo;
    }

    public Class<T> getClazz() {
        return clazz;
    }

    public Class<? extends Enum> getEnumClass() {
        return (Class<? extends Enum>) clazz;
    }

    public String getCodeName() {
        return codeName;
    }

    public String getUser() {
        return user;
    }

    public Function<String, T> getParser() {
        return parser;
    }

    public Function<T, String> getToString() {
        return toString;
    }
}