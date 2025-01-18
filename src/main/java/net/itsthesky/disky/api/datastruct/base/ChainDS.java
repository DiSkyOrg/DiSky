package net.itsthesky.disky.api.datastruct.base;

/**
 * Represent a "chain" data structure: the class
 * is meant to edit an existing object, and return
 * the modified version of it.
 * @param <T> The type of the object to edit
 */
public interface ChainDS<T> extends DataStruct<T> {

    /**
     * Edit the object and return the modified version of it.
     * @param object The object to edit
     * @return The modified object
     */
    T edit(T object);

}
