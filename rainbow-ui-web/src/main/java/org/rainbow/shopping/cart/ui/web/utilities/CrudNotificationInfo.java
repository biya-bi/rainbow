/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.shopping.cart.ui.web.utilities;

import static java.lang.annotation.ElementType.TYPE;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;

/**
 *
 * @author Biya-Bi
 */
@Target({TYPE})
@Retention(RUNTIME)
public @interface CrudNotificationInfo {

    String baseName() default ResourceBundles.CRUD_MESSAGES;

    String createdMessageKey() default "";

    String updatedMessageKey() default "";

    String deletedMessageKey() default "";
}
