package org.rainbow.persistence.dao;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 *
 * @author Biya-Bi
 */
@Target({ TYPE })
@Retention(RUNTIME)
public @interface Pageable {
	String attributeName();
}
