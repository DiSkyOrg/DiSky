package info.itsthesky.disky;

public interface ErrorHandler {

    void exception(String message);

    default void exception(Throwable throwable) {
        exception(throwable.getMessage());
    }

}
