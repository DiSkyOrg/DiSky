package info.itsthesky.disky.api.datastruct.base;

/**
 * Represent a data structure which can be made using
 * sections chaining in a script.
 */
public interface DataStruct<T> {

    /**
     * Build the data structure into the final object,
     * according to the values of its fields.
     * @return The final object built
     */
    T build();

}
