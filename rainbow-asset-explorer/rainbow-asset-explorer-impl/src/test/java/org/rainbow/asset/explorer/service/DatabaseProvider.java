package org.rainbow.asset.explorer.service;

import org.rainbow.common.test.Database;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class DatabaseProvider {
	private static Database database;

	public static Database getDatabase() {
		if (database == null) {
			try (ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(
					"/Spring/applicationContext.Common.xml", "/Spring/applicationContext.Database.xml")) {
				database = ctx.getBean(Database.class);
			}
		}
		return database;
	}

}
