/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.common.test;

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
public abstract class Database {

	private static final Logger LOGGER = Logger.getLogger(Database.class.getName());
	private static boolean driverLoaded;

	public void load() {
		if (driverLoaded)
			return;
		if (this.getDriverClassName() == null)
			throw new IllegalStateException("The driverClassName cannot be null.");
		try {
			LOGGER.log(Level.INFO, String.format("*** Loading driver: '%s'", this.getDriverClassName()));
			try {
				Class.forName(this.getDriverClassName()).newInstance();
				LOGGER.log(Level.INFO, String.format("*** '%s' driver loaded", this.getDriverClassName()));
				driverLoaded = true;
			} catch (InstantiationException | IllegalAccessException ex) {
				LOGGER.log(Level.SEVERE,
						String.format("*** An error occured while loading the driver: '%s'", this.getDriverClassName()),
						ex);
			}
		} catch (ClassNotFoundException ex) {
			LOGGER.log(Level.SEVERE,
					String.format("*** An error occured while loading the driver: '%s'", this.getDriverClassName()),
					ex);
		}

	}

	public abstract String getUrl();

	public abstract String getUser();

	public abstract String getPassword();

	public abstract String getDriverClassName();

	public Connection getConnection() throws SQLException {
		if (this.getUrl() == null)
			throw new IllegalStateException("The url cannot be null.");
		if (this.getUser() == null)
			throw new IllegalStateException("The user cannot be null.");
		if (this.getPassword() == null)
			throw new IllegalStateException("The password cannot be null.");
		return DriverManager.getConnection(this.getUrl(), this.getUser(), this.getPassword());
	}

	public void execute(String sqlFilePath) throws SQLException, FileNotFoundException, IOException {
		load();
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
