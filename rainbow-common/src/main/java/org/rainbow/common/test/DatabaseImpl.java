package org.rainbow.common.test;

import org.rainbow.common.test.Database;

public class DatabaseImpl extends Database {
	private String url;
	private String user;
	private String password;
	private String driverClassName;

	public DatabaseImpl(String url, String user, String password, String driverClassName) {
		super();
		this.url = url;
		this.user = user;
		this.password = password;
		this.driverClassName = driverClassName;
	}

	@Override
	public String getUrl() {
		return url;
	}

	@Override
	public String getUser() {
		return user;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getDriverClassName() {
		return driverClassName;
	}

}
