package net.itsthesky.disky.elements.effects;

import ch.njol.skript.Skript;
import net.itsthesky.disky.api.DiSkyRegistry;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.parser.ParserInstance;
import ch.njol.skript.util.AsyncEffect;
import ch.njol.util.Kleenean;
import net.dv8tion.jda.api.requests.RestAction;
import net.itsthesky.disky.api.events.SimpleDiSkyEvent;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Name("Retrieve Event Value")
@Description({"Retrieves specific event values asynchronously in certain DiSky events.",
        "These values need to be fetched from Discord's API and cannot be provided directly in the event.",
        "The value must be stored in a variable for later use.",
        "Each event has its own set of retrievable values - check the event documentation for available values."})
@Examples({"on guild member join:",
        "\tretrieve event-value \"user\" and store it in {_user}",
        "\tsend \"Welcome %{_user}%!\" to event-channel"})
@Since("4.0.0")
@SuppressWarnings("ALL")
public class RetrieveEventValue extends AsyncEffect {

    public static final HashMap<Class<? extends SimpleDiSkyEvent>, List<RetrieveValueInfo>> VALUES;

    static {
        VALUES = new HashMap<>();
        Skript.registerEffect(
                RetrieveEventValue.class,
                "retrieve [the] [event[(-| )]]value %string% and store (it|the value) in %objects%"
        );
    }

    private Expression<?> exprOutput;
    private RetrieveValueInfo<Object, ?, Object> valueInfo;
    private String id;

    @Override
    public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, ParseResult parseResult) {
        getParser().setHasDelayBefore(Kleenean.TRUE);

        final String rawId;
        try {
            rawId = ((Expression<String>) exprs[0]).getSingle(null);
        } catch (Exception ex) {
            Skript.error("The provided event-value string MUST be a literal / constant, aka not a variable or a changeable value.");
            return false;
        }
        final Class eventClass = ParserInstance.get().getCurrentEvents()[0];
        final List<RetrieveValueInfo> valuesInfo = VALUES.getOrDefault(eventClass, new ArrayList<>());

        final @Nullable RetrieveValueInfo valueInfo =
                valuesInfo.stream()
                        .filter(value -> value.getCodeName().equalsIgnoreCase(rawId))
                        .findAny()
                        .orElse(null);

        if (valueInfo == null) {
            Skript.error("Unknown event-value '" + rawId + "' for event '" + ParserInstance.get().getCurrentEventName() + "'. Found the following values: " +
                    (valuesInfo.isEmpty() ? "none" : valuesInfo
                            .stream()
                            .map(RetrieveValueInfo::getCodeName)
                            .collect(Collectors.joining(", "))));
            return false;
        }

        this.valueInfo = valueInfo;
        id = rawId;

        this.exprOutput = exprs[1];
        if (!Changer.ChangerUtils.acceptsChange(exprOutput, Changer.ChangeMode.SET, Object.class)) {
            Skript.error("The output expression must be a variable or a place where to store the retrieved value.");
            return false;
        }

        return true;
    }

    @Override
    public void execute(Event e) {
        final SimpleDiSkyEvent event = (SimpleDiSkyEvent<?>) e;
        final var value = valueInfo.getAction().apply(event).complete();
        final var converted = valueInfo.getConverter().apply(value);
        exprOutput.change(e, new Object[]{converted}, Changer.ChangeMode.SET);
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "retrieve event-value " + id + " and store it in " + exprOutput.toString(e, debug);
    }

    public static class RetrieveValueInfo<B, T, S> {

        private final Class<B> clazz;
        private final String codeName;
        private final Function<B, RestAction<T>> action;
        private final Function<S, T> converter;

        public RetrieveValueInfo(Class<B> clazz, String codeName, Function<B, RestAction<T>> action, Function<S, T> converter) {
            this.clazz = clazz;
            this.codeName = codeName;
            this.action = action;
            this.converter = converter;
        }

        public String getCodeName() {
            return codeName;
        }

        public Class<B> getClazz() {
            return clazz;
        }

        public Function<B, RestAction<T>> getAction() {
            return action;
        }

        public Function<S, T> getConverter() {
            return converter;
        }
    }
}
