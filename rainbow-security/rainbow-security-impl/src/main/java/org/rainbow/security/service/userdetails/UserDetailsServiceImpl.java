package org.rainbow.security.service.userdetails;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;

public class UserDetailsServiceImpl extends JdbcDaoImpl {

	public static final String DEF_USERS_BY_USERNAME_QUERY = "SELECT u.USER_NAME AS USER_NAME, m.PASSWORD AS PASSWORD, m.ENABLED AS ENABLED,m.LOCKED_OUT AS LOCKED_OUT FROM USERS u INNER JOIN "
			+ "MEMBERSHIPS m ON u.ID=m.USER_ID INNER JOIN APPLICATIONS a ON "
			+ "u.APPLICATION_ID=a.ID WHERE u.USER_NAME=? AND a.NAME=?";

	public static final String DEF_AUTHORITIES_BY_USERNAME_QUERY = "SELECT u.USER_NAME,auth.NAME FROM USERS u INNER JOIN "
			+ "USER_AUTHORITIES ua ON u.ID=ua.USER_ID INNER JOIN AUTHORITIES auth "
			+ "ON auth.ID=ua.AUTHORITY_ID INNER JOIN APPLICATIONS a ON u.APPLICATION_ID=a.ID "
			+ "WHERE u.USER_NAME=? AND a.NAME=?;";

	public static final String DEF_GROUP_AUTHORITIES_BY_USERNAME_QUERY = "SELECT g.ID,g.NAME,auth.NAME FROM GROUPS g INNER JOIN "
			+ "GROUP_AUTHORITIES ga ON g.ID=ga.GROUP_ID INNER JOIN AUTHORITIES auth "
			+ "ON ga.AUTHORITY_ID=auth.ID INNER JOIN GROUP_USERS gu ON "
			+ "g.ID=gu.GROUP_ID INNER JOIN USERS u ON gu.USER_ID=u.ID INNER JOIN APPLICATIONS a ON u.APPLICATION_ID=a.ID "
			+ "WHERE u.USER_NAME=? AND a.NAME=?";

	private final String applicationName;
	private String groupAuthoritiesByUsernameQuery;

	private final String passwordExpiredQuery = "SELECT m.LAST_PWD_CHANGE_DATE AS LAST_PWD_CHANGE_DATE,m.CREATION_DATE AS CREATION_DATE,pp.MAX_AGE AS MAX_AGE, pp.MIN_AGE "
			+ "FROM MEMBERSHIPS m INNER JOIN USERS u ON m.USER_ID=u.ID INNER JOIN APPLICATIONS a "
			+ "ON u.APPLICATION_ID=a.ID INNER JOIN PASSWORD_POLICIES pp ON a.ID=pp.APPLICATION_ID "
			+ "WHERE u.USER_NAME=? AND a.NAME=?";

	public UserDetailsServiceImpl(String applicationName) {
		super();
		if (applicationName == null)
			throw new IllegalArgumentException("The applicationName argument cannot be null.");

		this.applicationName = applicationName;

		super.setUsersByUsernameQuery(DEF_USERS_BY_USERNAME_QUERY);

		super.setAuthoritiesByUsernameQuery(DEF_AUTHORITIES_BY_USERNAME_QUERY);

		this.groupAuthoritiesByUsernameQuery = DEF_GROUP_AUTHORITIES_BY_USERNAME_QUERY;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public String getGroupAuthoritiesByUsernameQuery() {
		return groupAuthoritiesByUsernameQuery;
	}

	@Override
	public void setGroupAuthoritiesByUsernameQuery(String groupAuthoritiesByUsernameQuery) {
		this.groupAuthoritiesByUsernameQuery = groupAuthoritiesByUsernameQuery;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserDetails user = super.loadUserByUsername(username);
		boolean passwordExpired = passwordExpired(username);
		return new User(user.getUsername(), user.getPassword(), user.isEnabled(), user.isAccountNonExpired(),
				!passwordExpired, user.isAccountNonLocked(), user.getAuthorities());
	}

	@Override
	protected List<UserDetails> loadUsersByUsername(String username) {
		return getJdbcTemplate().query(super.getUsersByUsernameQuery(), new String[] { username, applicationName },
				new RowMapper<UserDetails>() {
					@Override
					public UserDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
						String username = rs.getString("USER_NAME");
						String password = rs.getString("PASSWORD");
						boolean enabled = rs.getBoolean("ENABLED");
						boolean lockedOut = rs.getBoolean("LOCKED_OUT");
						return new User(username, password, enabled, true, true, !lockedOut,
								AuthorityUtils.NO_AUTHORITIES);
					}

				});
	}

	@Override
	protected List<GrantedAuthority> loadUserAuthorities(String username) {
		return getJdbcTemplate().query(super.getAuthoritiesByUsernameQuery(),
				new String[] { username, this.getApplicationName() }, new RowMapper<GrantedAuthority>() {
					@Override
					public GrantedAuthority mapRow(ResultSet rs, int rowNum) throws SQLException {
						String roleName = getRolePrefix() + rs.getString(2);

						return new SimpleGrantedAuthority(roleName);
					}
				});
	}

	@Override
	protected List<GrantedAuthority> loadGroupAuthorities(String username) {
		return getJdbcTemplate().query(this.getGroupAuthoritiesByUsernameQuery(),
				new String[] { username, this.getApplicationName() }, new RowMapper<GrantedAuthority>() {
					@Override
					public GrantedAuthority mapRow(ResultSet rs, int rowNum) throws SQLException {
						String roleName = getRolePrefix() + rs.getString(3);

						return new SimpleGrantedAuthority(roleName);
					}
				});
	}

	@Override
	protected UserDetails createUserDetails(String username, UserDetails userFromUserQuery,
			List<GrantedAuthority> combinedAuthorities) {
		String returnUsername = userFromUserQuery.getUsername();

		if (!super.isUsernameBasedPrimaryKey()) {
			returnUsername = username;
		}

		return new User(returnUsername, userFromUserQuery.getPassword(), userFromUserQuery.isEnabled(),
				userFromUserQuery.isAccountNonExpired(), userFromUserQuery.isCredentialsNonExpired(),
				userFromUserQuery.isAccountNonLocked(), combinedAuthorities);
	}

	private Boolean passwordExpired(String username) {
		return getJdbcTemplate().queryForObject(passwordExpiredQuery,
				new String[] { username, this.getApplicationName() }, new RowMapper<Boolean>() {
					@Override
					public Boolean mapRow(ResultSet rs, int rowNum) throws SQLException {
						Date lastPasswordChangeDate = rs.getDate("LAST_PWD_CHANGE_DATE");
						Date creationDate = rs.getDate("CREATION_DATE");
						int maximumPasswordAge = rs.getInt("MAX_AGE");
						int minimumPasswordAge = rs.getInt("MIN_AGE");

						// If the maximum password age is greater than 0, then
						// the passwords
						// will expire. If the maximum password age is 0, then
						// passwords will never expire.
						if (maximumPasswordAge > 0) {

							if (lastPasswordChangeDate == null) {
								return true;
							}
							Calendar lastPasswordChangeDateCalendar = Calendar.getInstance();
							lastPasswordChangeDateCalendar.setTime(lastPasswordChangeDate);

							Calendar creationDateCalendar = Calendar.getInstance();
							creationDateCalendar.setTime(creationDate);

							// If minimum password age is 0, user must change
							// the password at next login.
							if (minimumPasswordAge == 0) {
								if (lastPasswordChangeDateCalendar.getTimeInMillis() < creationDateCalendar
										.getTimeInMillis())
									return true;
							}

							lastPasswordChangeDateCalendar.add(Calendar.DATE, maximumPasswordAge);
							if (lastPasswordChangeDateCalendar.getTimeInMillis() < Calendar.getInstance()
									.getTimeInMillis()) {
								return true;
							}
						}

						return false;
					}
				});
	}
}
