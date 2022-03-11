package info.itsthesky.disky.api.emojis;

public interface ActionSupplier<T> {
	T get() throws Throwable;
}
