package org.rainbow.common.test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

import org.junit.rules.ExternalResource;

public class DatabaseExternalResource extends ExternalResource {
	private Database database;
	private String[] initializationScripts;
	private String[] cleanUpScripts;

	public DatabaseExternalResource(Database database) {
		this(database, null, null);
	}

	public DatabaseExternalResource(Database database, String[] initializationScripts, String[] cleanUpScripts) {
		super();
		if (database == null) {
			throw new IllegalArgumentException("The database argument cannot be null.");
		}
		this.database = database;
		this.initializationScripts = initializationScripts;
		this.cleanUpScripts = cleanUpScripts;
	}

	@Override
	protected void before() throws Throwable {
		database.load();
		execute(initializationScripts);
	}

	@Override
	protected void after() {
		try {
			execute(cleanUpScripts);
		} catch (SQLException | IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void execute(String[] scripts) throws FileNotFoundException, SQLException, IOException {
		if (scripts != null) {
			for (String script : scripts) {
				if (script != null && !script.trim().isEmpty()) {
					database.execute(script.trim());
				}
			}
		}
	}
}
