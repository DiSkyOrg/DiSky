package net.itsthesky.disky.api.generator;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Means the syntax is from a specific DiSky module.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Module {

	/**
	 * The module name.
	 * @return The module name.
	 */
	String value();

}
