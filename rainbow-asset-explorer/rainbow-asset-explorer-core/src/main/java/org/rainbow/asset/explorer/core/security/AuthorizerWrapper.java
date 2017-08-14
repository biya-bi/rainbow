/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.asset.explorer.core.security;

import javax.ejb.Singleton;

/**
 *
 * @author Biya-Bi
 */
@Singleton
public class AuthorizerWrapper {
    private Authorizer authorizer;

    public Authorizer getAuthorizer() {
        return authorizer;
    }

    public void setAuthorizer(Authorizer authorizer) {
        this.authorizer = authorizer;
    }
    
}
