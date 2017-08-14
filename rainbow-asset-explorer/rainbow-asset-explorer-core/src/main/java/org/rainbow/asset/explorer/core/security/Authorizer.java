/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.asset.explorer.core.security;

import java.lang.reflect.AnnotatedElement;

/**
 *
 * @author Biya-Bi
 */
public interface Authorizer {
    void authorize(AnnotatedElement methodAnnotatedElement, AnnotatedElement classAnnotatedElement);
}
