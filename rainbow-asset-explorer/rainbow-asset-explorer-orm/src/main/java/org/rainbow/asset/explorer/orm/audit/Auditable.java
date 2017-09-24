package org.rainbow.asset.explorer.orm.audit;

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
public @interface Auditable {

	Class<?> audit();
}
