package info.itsthesky.disky.api.skript;

public interface ErrorHandler {

    void exception(String message);

    default void exception(Throwable throwable) {
        exception(throwable.getMessage());
    }

}
