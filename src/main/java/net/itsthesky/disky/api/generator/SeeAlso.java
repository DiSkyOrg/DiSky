package net.itsthesky.disky.api.generator;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 * Indicates related classes or interfaces for documentation purposes.
 * Can be used to suggest additional relevant types. The specified class
 * must be a documented element; it'll then be "converted" to its matching
 * documentation ID when generating the docs.
 *
 * @author Sky
 * @since 4.27.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SeeAlso {

    Class<?>[] value();

}
