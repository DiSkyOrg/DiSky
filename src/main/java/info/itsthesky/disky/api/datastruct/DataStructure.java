package info.itsthesky.disky.api.datastruct;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface DataStructure {

    /**
     * The name of the data structure, used to create the section's pattern.
     * For instance, if {@link #value()} returns {@code "embed"}, the pattern will be:
     * <code>create a new embed:</code>
     * @return The name of the data structure
     */
    String value();

    /**
     * The actual class this data structure is representing.
     * @return The class of the data structure
     */
    Class<?> clazz();

    /**
     * Whether this structure can be made from a {@link info.itsthesky.disky.elements.sections.CreateStructSection create structure section}.
     * A data structure may only serve as "sub-data structure" for other structures (like {@link info.itsthesky.disky.api.datastruct.structures.EmbedFieldStructure} for {@link info.itsthesky.disky.api.datastruct.structures.EmbedStructure}).
     */
    boolean canBeCreated() default true;

}
