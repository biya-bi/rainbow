/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.security.core.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Biya-Bi
 */
public class Database {

	private static final Logger LOGGER = Logger.getLogger(Database.class.getName());

	private String url;
	private String user;
	private String password;
	private String driverClassName;

	public Database() {
		super();
	}

	public Database(String url, String user, String password, String driverClassName) {
		if (url == null)
			throw new IllegalArgumentException("The url cannot be null.");
		if (user == null)
			throw new IllegalArgumentException("The user cannot be null.");
		if (password == null)
			throw new IllegalArgumentException("The password cannot be null.");
		if (driverClassName == null)
			throw new IllegalArgumentException("The driverClassName cannot be null.");
		this.url = url;
		this.user = user;
		this.password = password;
		this.driverClassName = driverClassName;
		loadDriver();
	}

	private void loadDriver() {
		if (driverClassName == null)
			throw new IllegalStateException("The driverClassName cannot be null.");
		try {
			LOGGER.log(Level.INFO, String.format("*** Loading driver: '%s'", driverClassName));
			try {
				Class.forName(driverClassName).newInstance();
				LOGGER.log(Level.INFO, String.format("*** '%s' driver loaded", driverClassName));
			} catch (InstantiationException | IllegalAccessException ex) {
				LOGGER.log(Level.SEVERE,
						String.format("*** An error occured while loading the driver: '%s'", driverClassName), ex);
			}
		} catch (ClassNotFoundException ex) {
			LOGGER.log(Level.SEVERE,
					String.format("*** An error occured while loading the driver: '%s'", driverClassName), ex);
		}

	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		if (url == null)
			throw new IllegalArgumentException("The url cannot be null.");
		this.url = url;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		if (user == null)
			throw new IllegalArgumentException("The user cannot be null.");
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		if (password == null)
			throw new IllegalArgumentException("The password cannot be null.");
		this.password = password;
	}

	public String getDriverClassName() {
		return driverClassName;
	}

	public void setDriverClassName(String driverName) {
		if (driverClassName == null)
			throw new IllegalArgumentException("The driverClassName cannot be null.");
		this.driverClassName = driverName;
	}

	public Connection getConnection() throws SQLException {
		if (url == null)
			throw new IllegalStateException("The url cannot be null.");
		if (user == null)
			throw new IllegalStateException("The user cannot be null.");
		if (password == null)
			throw new IllegalStateException("The password cannot be null.");
		return DriverManager.getConnection(url, user, password);
	}

	public void execute(String sqlFilePath) throws SQLException, FileNotFoundException, IOException {
		StringBuilder stringBuilder = new StringBuilder();
		File file = new File(sqlFilePath);

		try (FileReader fileReader = new FileReader(file);
				BufferedReader bufferedReader = new BufferedReader(fileReader);
				Connection connection = getConnection();
				Statement statement = connection.createStatement();) {

			String line;
			// be sure to not have line starting with "--" or "/*" or any other
			// non aplhabetical character
			while ((line = bufferedReader.readLine()) != null) {
				stringBuilder.append(line);
			}

			// here is our splitter ! We use ";" as a delimiter for each request
			// then we are sure to have well formed statements
			String[] instructions = stringBuilder.toString().split(";");

			LOGGER.log(Level.INFO, String.format("Started executing the file: '%s'", file.getAbsolutePath()));
			for (String instruction : instructions) {
				// we ensure that there is no spaces before or after the request
				// string
				// in order to not execute empty statements
				if (!instruction.trim().equals("")) {
					statement.executeUpdate(instruction);
					LOGGER.log(Level.INFO, String.format(">> %s", instruction));
				}
			}
			LOGGER.log(Level.INFO, String.format("Finished executing the file: '%s'", file.getAbsolutePath()));

		} catch (SQLException e) {
			throw new SQLException(
					String.format("*** An exception was thrown while executing the file '%s': ", sqlFilePath), e);
		}
	}
}
