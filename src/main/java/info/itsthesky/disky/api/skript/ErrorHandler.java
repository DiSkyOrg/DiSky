package info.itsthesky.disky.api.skript;

public interface ErrorHandler {

    default void exception(String message) {
        exception(new RuntimeException(message));
    }

    void exception(Throwable throwable);

}
