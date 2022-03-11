package info.itsthesky.disky.api.events;

/**
 * Class which handle a custom event value, using an object and a class info reference.
 * @param <T> The object parameter type
 */
public class EventValue<T> {

    private T object;
    private final Class<T> aClass;
    private final String cInfo;

    public EventValue(Class<T> aClass, String cInfo) {
        this.aClass = aClass;
        this.cInfo = cInfo;
    }

    public T getObject() {
        return object;
    }

    public Class<T> getaClass() {
        return aClass;
    }

    public String getcInfo() {
        return cInfo;
    }

    public void setObject(T object) {
        this.object = object;
    }
}
