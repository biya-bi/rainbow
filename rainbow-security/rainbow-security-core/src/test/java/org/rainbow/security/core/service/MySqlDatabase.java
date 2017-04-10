/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.security.core.service;

/**
 *
 * @author Biya-Bi
 */
public class MySqlDatabase extends Database {

    public MySqlDatabase() {
        super("jdbc:mysql://localhost:3306/rainbow_security_dev", "root", "Passw0rd", "com.mysql.jdbc.Driver");
    }
}
