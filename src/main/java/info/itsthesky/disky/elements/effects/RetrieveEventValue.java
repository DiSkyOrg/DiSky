package info.itsthesky.disky.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.parser.ParserInstance;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.api.events.SimpleDiSkyEvent;
import info.itsthesky.disky.api.skript.WaiterEffect;
import net.dv8tion.jda.api.requests.RestAction;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@SuppressWarnings("ALL")
public class RetrieveEventValue extends WaiterEffect<Object> {

	public static final HashMap<Class<? extends SimpleDiSkyEvent>, List<RetrieveValueInfo>> VALUES;

	static {
		VALUES = new HashMap<>();
		Skript.registerEffect(
				RetrieveEventValue.class,
				"retrieve [the] [event[(-| )]]value %string% and store (it|the value) in %objects%"
		);
	}

	private RetrieveValueInfo<Object, ?, Object> valueInfo;
	private String id;

	@Override
	public boolean initEffect(Expression<?>[] exprs, int i, Kleenean kleenean, ParseResult parseResult) {
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
			Skript.error("Unknown event-value '"+rawId+"' for event '"+ParserInstance.get().getCurrentEventName()+"'. Found the following values: " +
					(valuesInfo.isEmpty() ? "none" : valuesInfo
							.stream()
							.map(RetrieveValueInfo::getCodeName)
							.collect(Collectors.joining(", "))));
			return false;
		}

		this.valueInfo = valueInfo;
		id = rawId;

		return validateVariable(exprs[1], false, true);
	}

	@Override
	public void runEffect(Event e) {
		final SimpleDiSkyEvent event = (SimpleDiSkyEvent<?>) e;
		valueInfo.getAction().apply(event).queue(entity -> {
					restart(valueInfo.getConverter().apply(entity));
				},
				ex -> {
					restart();
					DiSky.getErrorHandler().exception(e, ex);
				});
	}

	@Override
	public @NotNull String toString(@Nullable Event e, boolean debug) {
		return "retrieve event-value " + id + " and store it in " + variableAsString(e, debug);
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
