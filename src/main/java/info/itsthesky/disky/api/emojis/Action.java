package info.itsthesky.disky.api.emojis;

import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;

public interface Action<T> {
	T complete();

	void queue(@Nullable Consumer<T> onSuccess, @Nullable Consumer<Throwable> onThrow);

	default void queue() {
		queue(null, null);
	}

	default void queue(Consumer<T> onSuccess) {
		queue(onSuccess, null);
	}

	<R> Action<R> flatMap(Function<T, R> flatmap);
}