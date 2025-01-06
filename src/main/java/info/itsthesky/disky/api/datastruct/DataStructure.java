package info.itsthesky.disky.api.datastruct;

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
     * Whether this structure can be made from a {@link info.itsthesky.disky.elements.sections.CreateStructSection create structure section}.
     * A data structure may only serve as "sub-data structure" for other structures (like {@link info.itsthesky.disky.elements.datastructs.structures.EmbedFieldStructure} for {@link info.itsthesky.disky.elements.datastructs.structures.EmbedStructure}).
     */
    boolean canBeCreated() default true;

}
