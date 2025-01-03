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
     * The minimum amount of this entry, in case it's
     * an array or a list.
     * @return The minimum amount of this entry
     */
    int minimum() default 0;

    /**
     * The maximum amount of this entry, in case it's
     * an array or a list.
     * @return The maximum amount of this entry
     */
    int maximum() default 0;

}
