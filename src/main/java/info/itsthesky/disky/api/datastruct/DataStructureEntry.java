package info.itsthesky.disky.api.datastruct;

import info.itsthesky.disky.api.datastruct.base.DataStruct;

import javax.annotation.Nullable;
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
    boolean optional() default true;

    /**
     * Get the sub structure type, to use when parsing an array
     * of structure for this field. Naturally, those structures, once
     * built, must return the same type as the field type.
     * @return The sub structure type
     */
    Class<? extends DataStruct> subStructureType() default DataStruct.class;

    //region Documentation

    /**
     * The description of this entry.
     * (what is it used for, what should be the value, etc.)
     * @return The description of this entry
     */
    String description() default "";

    //endregion

}
