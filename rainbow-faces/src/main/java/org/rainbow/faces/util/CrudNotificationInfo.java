package org.rainbow.faces.util;

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
public @interface CrudNotificationInfo {
	String baseName();

	String createdMessageKey() default "";

	String updatedMessageKey() default "";

	String deletedMessageKey() default "";
}
