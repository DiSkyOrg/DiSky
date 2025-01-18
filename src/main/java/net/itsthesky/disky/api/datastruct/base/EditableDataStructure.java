package net.itsthesky.disky.api.datastruct.base;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface EditableDataStructure<T, D extends DataStruct<T>> {

    /**
     * Get a list of all accepted classes for this data structure to
     * be able to convert it into the T type.
     * @return List of all accepted classes
     */
    List<Class<?>> getAcceptedClasses();

    /**
     * Convert the object into the T type.
     * The given object will always be an instance of the accepted classes.
     * @param object The object to convert
     * @return The converted object
     */
    T convert(@NotNull Object object);

}
