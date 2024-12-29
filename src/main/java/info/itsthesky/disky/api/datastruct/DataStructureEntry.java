package info.itsthesky.disky.api.datastruct;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface DataStructureEntry {

    /**
     * The name of this entry to be used in a Section.
     * @return The possible name of this entry
     */
    String value();

    /**
     * If the entry is optional or not.
     * @return If the entry is optional or not
     */
    boolean optional() default false;

    /**
     * In case of an array/list of object,
     * the key (usually singular) used to represent a new instance of that object.
     * @return The key used to represent a new instance of that object
     */
    String singleKey() default "";

}
