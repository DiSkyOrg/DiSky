package info.itsthesky.disky.api.datastruct.base;

/**
 * Represent a "basic" data structure: the class
 * is meant to be used to build a final, new object
 */
public interface BasicDS<T> extends DataStruct<T> {

    /**
     * Build the data structure into the final object,
     * according to the values of its fields.
     * @return The final object built
     */
    T build();

}
