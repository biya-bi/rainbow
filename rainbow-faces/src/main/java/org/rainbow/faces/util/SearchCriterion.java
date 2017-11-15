package org.rainbow.faces.util;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 *
 * @author Biya-Bi
 */
@Target({ FIELD, METHOD })
@Retention(RUNTIME)
@Inherited
public @interface SearchCriterion {
}
