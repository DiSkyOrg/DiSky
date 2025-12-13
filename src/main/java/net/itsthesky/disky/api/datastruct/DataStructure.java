package net.itsthesky.disky.api.datastruct;

import net.itsthesky.disky.elements.datastructs.structures.EmbedFieldStructure;
import net.itsthesky.disky.elements.datastructs.structures.EmbedStructure;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface DataStructure {

    /**
     * The actual class this data structure is representing.
     * @return The class of the data structure
     */
    Class<?> clazz();

    /**
     * Whether this structure can be made from a {@link net.itsthesky.disky.elements.sections.CreateStructSection create structure section}.
     * A data structure may only serve as "sub-data structure" for other structures (like {@link EmbedFieldStructure} for {@link EmbedStructure}).
     */
    boolean canBeCreated() default true;

    //region Documentation
    String[] validationRules() default {};
    //endregion

}
