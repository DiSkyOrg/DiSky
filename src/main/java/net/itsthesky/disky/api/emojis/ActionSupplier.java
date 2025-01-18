package net.itsthesky.disky.api.emojis;

public interface ActionSupplier<T> {
	T get() throws Throwable;
}
