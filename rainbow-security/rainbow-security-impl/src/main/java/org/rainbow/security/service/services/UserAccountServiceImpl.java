package org.rainbow.security.service.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.rainbow.common.util.DateUtil;
import org.rainbow.security.orm.entities.RecoveryInformation;
import org.rainbow.security.service.exceptions.ApplicationNotFoundException;
import org.rainbow.security.service.exceptions.CredentialsNotFoundException;
import org.rainbow.security.service.exceptions.DuplicateRecoveryQuestionException;
import org.rainbow.security.service.exceptions.InvalidPasswordException;
import org.rainbow.security.service.exceptions.MinimumPasswordAgeViolationException;
import org.rainbow.security.service.exceptions.PasswordHistoryException;
import org.rainbow.security.service.exceptions.RecoveryInformationNotFoundException;
import org.rainbow.security.service.exceptions.RecoveryQuestionNotFoundException;
import org.rainbow.security.service.exceptions.UserNotFoundException;
import org.rainbow.security.service.exceptions.WrongRecoveryAnswerException;
import org.rainbow.security.service.reporter.ChangeReporter;
import org.rainbow.security.service.reporter.ChangeReporterInfo;
import org.rainbow.security.service.reporter.ChangeReporterInfoBuilder;
import org.rainbow.security.service.reporter.WriteOperation;
import org.rainbow.security.util.PasswordUtil;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

public class UserAccountServiceImpl extends JdbcDaoSupport implements UserAccountService {
	private final static Date FIRST_JAN_1754 = DateUtil.toDate("1754-01-01");

	private static final String RECOVERY_ANSWER_SELECT_QUERY = "SELECT ri.ANSWER FROM RECOVERY_INFORMATION ri INNER JOIN MEMBERSHIPS m ON ri.MEMBERSHIP_ID=m.ID INNER JOIN USERS u ON m.USER_ID=u.ID INNER JOIN APPLICATIONS a "
			+ "ON u.APPLICATION_ID=a.ID WHERE u.USER_NAME=? AND a.NAME=? AND ri.QUESTION=?";

	private static final String RECOVER_SELECT_QUERY = "SELECT m.USER_ID, m.ID AS MEMBERSHIP_ID, m.FAILED_REVRY_ATMPT_WIN_START, "
			+ "m.FAILED_REVRY_ATMPT_CNT, m.FAILED_PWD_ATMPT_CNT, m.FAILED_PWD_ATMPT_WIN_START, m.FAILED_PWD_ATMPT_WIN_START, "
			+ "m.LAST_LOCK_OUT_DATE, lp.ATTEMPT_WINDOW, m.LOCKED, lp.THRESHOLD AS LOCK_OUT_THRESHOLD "
			+ "FROM MEMBERSHIPS m INNER JOIN USERS u ON m.USER_ID=u.ID INNER JOIN APPLICATIONS a "
			+ "ON u.APPLICATION_ID=a.ID INNER JOIN LOCKOUT_POLICIES lp ON lp.APPLICATION_ID=a.ID "
			+ "WHERE u.USER_NAME=? AND a.NAME=?";

	private static final String RECOVER_UPDATE_QUERY = "UPDATE MEMBERSHIPS SET FAILED_REVRY_ATMPT_WIN_START=?, "
			+ "FAILED_REVRY_ATMPT_CNT=?,LOCKED=?,LAST_LOCK_OUT_DATE=?, FAILED_PWD_ATMPT_CNT=?, FAILED_PWD_ATMPT_WIN_START=? WHERE ID=?";

	private static final String LAST_ACTIVITY_DATE_UPDATE_QUERY = "UPDATE USERS SET LAST_ACTIVITY_DATE=? WHERE ID=?";

	private static final String CAN_CHANGE_PASSWORD_SELECT_QUERY = "SELECT m.LAST_PWD_CHANGE_DATE, m.CREATION_DATE, "
			+ "pp.MIN_AGE AS PASSWORD_MIN_AGE FROM MEMBERSHIPS m INNER JOIN USERS u ON m.USER_ID=u.ID INNER JOIN APPLICATIONS a "
			+ "ON u.APPLICATION_ID=a.ID INNER JOIN PASSWORD_POLICIES pp ON pp.APPLICATION_ID=a.ID WHERE u.USER_NAME=? AND a.NAME=?";

	private static final String SET_PASSWORD_SELECT_QUERY = "SELECT m.ID AS MEMBERSHIP_ID, pp.HISTORY_THRESHOLD AS PASSWORD_HISTORY_THRESHOLD "
			+ "FROM MEMBERSHIPS m INNER JOIN USERS u ON m.USER_ID=u.ID INNER JOIN APPLICATIONS a ON u.APPLICATION_ID=a.ID INNER JOIN "
			+ "PASSWORD_POLICIES pp ON a.ID=pp.APPLICATION_ID WHERE u.USER_NAME=? AND a.NAME=?";

	private static final String PASSWORD_HISTORY_IDS_SELECT_QUERY = "SELECT ID FROM PASSWORD_HISTORIES WHERE MEMBERSHIP_ID=? "
			+ "ORDER BY CREATION_DATE DESC";

	private static final String IDS_PARAMETER_PLACEHOLDER = ":ids";

	private static final String PASSWORD_HISTORIES_DELETE_QUERY = "DELETE FROM PASSWORD_HISTORIES WHER ID IN ("
			+ IDS_PARAMETER_PLACEHOLDER + ")";

	private static final String PASSWORD_HISTORIES_INSERT_QUERY = "INSERT INTO PASSWORD_HISTORIES (CHANGE_DATE,CREATION_DATE,CREATOR,LAST_UPDATE_DATE,PASSWORD,UPDATER,VERSION,MEMBERSHIP_ID) "
			+ "VALUES (?,?,?,?,?,?,?,?);";

	private static final String PASSWORD_UPDATE_QUERY = "UPDATE MEMBERSHIPS SET PASSWORD=?, LAST_PWD_CHANGE_DATE=? WHERE ID=?";

	private static final String RECOVERY_QUESTIONS_SELECT_QUERY = "SELECT ri.QUESTION FROM MEMBERSHIPS m INNER JOIN USERS u ON "
			+ "m.USER_ID=u.ID INNER JOIN RECOVERY_INFORMATION ri ON ri.MEMBERSHIP_ID=m.ID INNER JOIN APPLICATIONS a "
			+ "ON u.APPLICATION_ID=a.ID WHERE u.USER_NAME=? AND a.NAME=?";

	private static final String APPLICATIONS_COUNT_QUERY = "SELECT COUNT(ID) FROM APPLICATIONS WHERE NAME=?";

	private static final String USERS_COUNT_QUERY = "SELECT COUNT(u.ID) FROM USERS u INNER JOIN APPLICATIONS a on u.APPLICATION_ID=a.ID WHERE u.USER_NAME=? AND a.name=?";

	private static final String MEMBERSHIP_ID_SELECT_QUERY = "SELECT m.ID FROM MEMBERSHIPS m INNER JOIN USERS u ON m.USER_ID=u.ID INNER JOIN APPLICATIONS a on u.APPLICATION_ID=a.ID WHERE u.USER_NAME=? AND a.name=?";

	private static final String MEMBERSHIPS_COUNT_QUERY = "SELECT COUNT(m.ID) FROM MEMBERSHIPS m INNER JOIN USERS u ON m.USER_ID=u.ID INNER JOIN APPLICATIONS a on u.APPLICATION_ID=a.ID WHERE u.USER_NAME=? AND a.name=?";

	private static final String RECOVERY_QUESTION_COUNT_QUERY = "SELECT COUNT(ri.ID) FROM MEMBERSHIPS m INNER JOIN USERS u ON "
			+ "m.USER_ID=u.ID INNER JOIN RECOVERY_INFORMATION ri ON ri.MEMBERSHIP_ID=m.ID INNER JOIN APPLICATIONS a "
			+ "ON u.APPLICATION_ID=a.ID WHERE u.USER_NAME=? AND a.NAME=? AND ri.QUESTION=?";

	private static final String ADD_RECOVERY_QUESTION_SELECT_QUERY = "SELECT ri.VERSION FROM MEMBERSHIPS m INNER JOIN USERS u ON "
			+ "m.USER_ID=u.ID INNER JOIN RECOVERY_INFORMATION ri ON ri.MEMBERSHIP_ID=m.ID INNER JOIN APPLICATIONS a "
			+ "ON u.APPLICATION_ID=a.ID WHERE u.USER_NAME=? AND a.NAME=? AND ri.QUESTION=?";

	private static final String ADD_RECOVERY_INFORMATION_INSERT_QUERY = "INSERT INTO RECOVERY_INFORMATION(ANSWER,CREATION_DATE,CREATOR,ENCRYPTED,LAST_UPDATE_DATE,QUESTION,UPDATER,VERSION,MEMBERSHIP_ID) "
			+ "VALUES(?,?,?,?,?,?,?,?,?)";

	private static final String ADD_RECOVERY_INFORMATION_UPDATE_QUERY = "UPDATE RECOVERY_INFORMATION SET ANSWER=?,ENCRYPTED=?,LAST_UPDATE_DATE=?,UPDATER=?,VERSION=? WHERE QUESTION=? AND MEMBERSHIP_ID=?";

	private static final String QUESTIONS_PARAMETER_PLACEHOLDER = ":questions";

	private static final String ADD_RECOVERY_QUESTION_DELETE_QUERY = "DELETE FROM RECOVERY_INFORMATION WHERE QUESTION IN ("
			+ QUESTIONS_PARAMETER_PLACEHOLDER + ") AND MEMBERSHIP_ID=(" + MEMBERSHIP_ID_SELECT_QUERY + ")";

	private static final String CHECK_PASSWORD_HISTORY_VIOLATION_SELECT_QUERY = "SELECT ph.PASSWORD,pp.HISTORY_THRESHOLD FROM "
			+ "PASSWORD_HISTORIES ph INNER JOIN MEMBERSHIPS m ON ph.MEMBERSHIP_ID=m.ID INNER JOIN USERS u ON m.USER_ID=u.ID "
			+ "INNER JOIN APPLICATIONS a ON u.APPLICATION_ID=a.ID INNER JOIN PASSWORD_POLICIES pp ON a.ID=pp.APPLICATION_ID "
			+ "WHERE u.USER_NAME=? AND a.NAME=?";

	private static final String RESET_PASSWORD_SELECT_QUERY = "SELECT m.LOCKED, m.ENABLED FROM MEMBERSHIPS m INNER JOIN USERS u "
			+ "ON m.USER_ID=u.ID INNER JOIN APPLICATIONS a on u.APPLICATION_ID=a.ID WHERE u.USER_NAME=? AND a.name=?";

	private static final String RECOVERY_QUESTION_DELETE_QUERY = "DELETE FROM RECOVERY_INFORMATION WHERE QUESTION=? AND MEMBERSHIP_ID="
			+ "(SELECT m.ID FROM MEMBERSHIPS m INNER JOIN USERS u ON m.USER_ID=u.ID INNER JOIN APPLICATIONS a on u.APPLICATION_ID=a.ID "
			+ "WHERE u.USER_NAME=? AND a.NAME=?)";

	private static final String UNLOCK_SELECT_QUERY = "SELECT m.ENABLED FROM MEMBERSHIPS m INNER JOIN USERS u "
			+ "ON m.USER_ID=u.ID INNER JOIN APPLICATIONS a on u.APPLICATION_ID=a.ID WHERE u.USER_NAME=? AND a.name=?";

	private static final String CHANGE_RECOVERY_ANSWER_SELECT_QUERY = "SELECT u.ID AS USER_ID,ri.ID AS RECOVERY_INFORMATION_ID FROM "
			+ "MEMBERSHIPS m INNER JOIN USERS u ON m.USER_ID=u.ID INNER JOIN APPLICATIONS a ON u.APPLICATION_ID=a.ID INNER JOIN "
			+ "RECOVERY_INFORMATION ri ON m.ID=ri.MEMBERSHIP_ID WHERE u.USER_NAME=? AND a.NAME=? AND ri.QUESTION=?";

	private static final String CHANGE_RECOVERY_ANSWER_UPDATE_QUERY = "UPDATE RECOVERY_INFORMATION SET ANSWER=? WHERE ID=?";

	private static final String IS_PASSWORD_EXPIRED_SELECT_QUERY = "SELECT m.LAST_PWD_CHANGE_DATE,m.CREATION_DATE,pp.MIN_AGE,pp.MAX_AGE "
			+ "FROM MEMBERSHIPS m INNER JOIN USERS u ON m.USER_ID=u.ID INNER JOIN APPLICATIONS a ON u.APPLICATION_ID=a.ID "
			+ "INNER JOIN PASSWORD_POLICIES pp ON a.ID=pp.APPLICATION_ID WHERE u.USER_NAME=? AND a.NAME=?";

	private static final String LAST_LOGIN_DATE_SELECT_QUERY = "SELECT lh.LOGIN_DATE FROM MEMBERSHIPS m INNER JOIN USERS u ON m.USER_ID=u.ID "
			+ "INNER JOIN APPLICATIONS a ON u.APPLICATION_ID=a.ID INNER JOIN LOGIN_HISTORIES lh ON m.ID=lh.MEMBERSHIP_ID "
			+ "WHERE u.USER_NAME=? AND a.NAME=?";

	private static final String MEMBERSHIP_CHANGE_SELECT_QUERY = "SELECT m.* FROM MEMBERSHIPS m INNER JOIN USERS u ON m.USER_ID=u.ID INNER JOIN "
			+ "APPLICATIONS a on u.APPLICATION_ID=a.ID WHERE u.USER_NAME=? AND a.name=?";

	private static final String RECOVERY_QUESTION_CHANGE_SELECT_QUERY = "SELECT ri.* FROM MEMBERSHIPS m INNER JOIN USERS u ON "
			+ "m.USER_ID=u.ID INNER JOIN RECOVERY_INFORMATION ri ON ri.MEMBERSHIP_ID=m.ID INNER JOIN APPLICATIONS a "
			+ "ON u.APPLICATION_ID=a.ID WHERE u.USER_NAME=? AND a.NAME=? AND ri.QUESTION=?";

	private static final String USER_CHANGE_SELECT_QUERY = "SELECT u.* FROM USERS u INNER JOIN APPLICATIONS a on u.APPLICATION_ID=a.ID WHERE u.USER_NAME=? AND a.name=?";

	private static final String USER_ID_SELECT_QUERY = "SELECT u.ID FROM USERS u INNER JOIN APPLICATIONS a on u.APPLICATION_ID=a.ID WHERE u.USER_NAME=? AND a.name=?";

	private final Logger logger = Logger.getLogger(this.getClass().getName());

	private String applicationName;
	private PasswordEncoder passwordEncoder;
	private AuthenticationManager authenticationManager;
	private UserDetailsService userDetailsService;

	public UserAccountServiceImpl() {
	}

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public PasswordEncoder getPasswordEncoder() {
		return passwordEncoder;
	}

	public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	public AuthenticationManager getAuthenticationManager() {
		return authenticationManager;
	}

	public void setAuthenticationManager(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}

	public UserDetailsService getUserDetailsService() {
		return userDetailsService;
	}

	public void setUserDetailsService(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	@Override
	public void setPassword(String userName, String password)
			throws UserNotFoundException, InvalidPasswordException, PasswordHistoryException {
		checkDependencies();

		Objects.requireNonNull(userName, "The userName argument cannot be null.");
		Objects.requireNonNull(password, "The password argument cannot be null.");

		checkUser(userName);

		if (!PasswordUtil.isValidPassword(password, this.getApplicationName(), this.getJdbcTemplate())) {
			throw new InvalidPasswordException();
		}
		// We could have checked the password history of the user before setting
		// his/her password. But this is completely unnecessary, because we
		// assume that only the administrator will call this method. In this
		// case, the administrator can set the password to a default password
		// that the user will change. Also, the administrator can't know the
		// passwords that the user has used. Checking the password history at
		// this point will disclosing the type of passwords the user has used in
		// the past, and thus synonymous to compromising the user's account.

		// When an administrator sets a user's password, the last password
		// change date should be set in the past so that the user should be
		// prompted to change his/her password if the minimum password age is 0.
		setPassword(userName, password, FIRST_JAN_1754);

		// The user's password has changed. So, we have to report who did the
		// changes, and insert appropriate data in the corresponding audit
		// table.
		reportMembershipChange(userName, false);
	}

	@Override
	public void changePassword(String userName, String oldPassword, String newPassword)
			throws UserNotFoundException, InvalidPasswordException {
		checkDependencies();

		Objects.requireNonNull(userName, "The userName argument cannot be null.");
		Objects.requireNonNull(oldPassword, "The oldPassword argument cannot be null.");
		Objects.requireNonNull(newPassword, "The newPassword argument cannot be null.");

		if (!applicationExists(this.getApplicationName())) {
			throw new ApplicationNotFoundException(this.getApplicationName());
		}

		try {
			getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(userName, oldPassword));
		} catch (CredentialsExpiredException e) {
			// We want it to be possible for a user whose credentials have
			// expired to be able to change his/her password.
			logger.log(Level.INFO,
					String.format("The user '%s', whose credentials have expired, is about to change his/her password.",
							userName),
					e);
		}

		if (!PasswordUtil.isValidPassword(newPassword, this.getApplicationName(), this.getJdbcTemplate())) {
			throw new InvalidPasswordException();
		}
		if (!canChangePassword(userName)) {
			throw new MinimumPasswordAgeViolationException();
		}

		checkPasswordHistoryViolation(userName, newPassword);

		setPassword(userName, newPassword, new Date());

		setAuthentication(userName, newPassword);

		// The user's password has changed. So, we have to report who did the
		// changes, and insert appropriate data in the corresponding audit
		// table.
		reportMembershipChange(userName, true);
		reportUserLastActivity(userName);
	}

	@Override
	public void resetPassword(String userName, String newPassword, String question, String answer)
			throws UserNotFoundException, InvalidPasswordException, WrongRecoveryAnswerException,
			RecoveryQuestionNotFoundException {
		checkDependencies();

		Objects.requireNonNull(userName, "The userName argument cannot be null.");
		Objects.requireNonNull(newPassword, "The newPassword argument cannot be null.");
		Objects.requireNonNull(question, "The question argument cannot be null.");
		Objects.requireNonNull(answer, "The answer argument cannot be null.");

		checkUser(userName);

		if (!PasswordUtil.isValidPassword(newPassword, this.getApplicationName(), this.getJdbcTemplate())) {
			throw new InvalidPasswordException();
		}

		this.getJdbcTemplate().query(RESET_PASSWORD_SELECT_QUERY, new String[] { userName, this.getApplicationName() },
				new RowCallbackHandler() {
					@Override
					public void processRow(ResultSet rs) throws SQLException {
						boolean locked = rs.getBoolean("LOCKED");
						boolean enabled = rs.getBoolean("ENABLED");
						if (locked) {
							throw new LockedException(
									String.format("The user '%s' has been locked in the application '%s'.", userName,
											getApplicationName()));
						}

						if (!enabled) {
							throw new DisabledException(
									String.format("The user '%s' has been disabled in the application '%s'.", userName,
											getApplicationName()));
						}

					}
				});

		recover(userName, question, answer);

		checkPasswordHistoryViolation(userName, newPassword);

		setPassword(userName, newPassword, new Date());

		setAuthentication(userName, newPassword);

		// The user's password has changed. So, we have to report who did the
		// changes, and insert appropriate data in the corresponding audit
		// table.
		reportMembershipChange(userName, true);
		reportUserLastActivity(userName);
	}

	@Override
	public void addRecoveryQuestion(String userName, String password, String question, String answer)
			throws UserNotFoundException, DuplicateRecoveryQuestionException {
		checkDependencies();

		Objects.requireNonNull(userName, "The userName argument cannot be null.");
		Objects.requireNonNull(password, "The password argument cannot be null.");
		Objects.requireNonNull(question, "The question argument cannot be null.");
		Objects.requireNonNull(answer, "The answer argument cannot be null.");

		checkUser(userName);

		addRecoveryQuestion(userName, password, question, answer, false);

		reportUserLastActivity(userName);
	}

	@Override
	public void addRecoveryQuestions(String userName, String password, Map<String, String> questionAnswerPairs)
			throws UserNotFoundException, DuplicateRecoveryQuestionException {
		checkDependencies();

		Objects.requireNonNull(userName, "The userName argument cannot be null.");
		Objects.requireNonNull(password, "The password argument cannot be null.");
		Objects.requireNonNull(questionAnswerPairs, "The questionAnswerPairs argument cannot be null.");

		if (questionAnswerPairs.isEmpty() || questionAnswerPairs.keySet().stream()
				.filter(x -> x != null && questionAnswerPairs.get(x) != null).count() == 0) {
			throw new IllegalArgumentException(
					"The questionAnswerPairs argument can neither be null nor empty, and must contain at least one question with a non-null answer.");
		}

		addRecoveryQuestions(userName, password, questionAnswerPairs, false);

		reportUserLastActivity(userName);
	}

	@Override
	public void resetRecoveryQuestions(String userName, String password, Map<String, String> questionAnswerPairs)
			throws UserNotFoundException {
		checkDependencies();

		Objects.requireNonNull(userName, "The userName argument cannot be null.");
		Objects.requireNonNull(password, "The password argument cannot be null.");
		Objects.requireNonNull(questionAnswerPairs, "The questionAnswerPairs argument cannot be null.");

		if (questionAnswerPairs.isEmpty() || questionAnswerPairs.keySet().stream()
				.filter(x -> x != null && questionAnswerPairs.get(x) != null).count() == 0) {
			throw new IllegalArgumentException(
					"The questionAnswerPairs argument can neither be null nor empty, and must contain at least one question with a non-null answer.");
		}

		addRecoveryQuestions(userName, password, questionAnswerPairs, true);

		reportUserLastActivity(userName);
	}

	@Override
	public void changeRecoveryAnswer(String userName, String password, String question, String newAnswer)
			throws UserNotFoundException, RecoveryQuestionNotFoundException {
		checkDependencies();

		Objects.requireNonNull(userName, "The userName argument cannot be null.");
		Objects.requireNonNull(password, "The password argument cannot be null.");
		Objects.requireNonNull(question, "The question argument cannot be null.");
		Objects.requireNonNull(newAnswer, "The newAnswer argument cannot be null.");

		if (!applicationExists(getApplicationName())) {
			throw new ApplicationNotFoundException(getApplicationName());
		}

		getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(userName, password));

		if (!recoveryQuestionExists(userName, this.getApplicationName(), question)) {
			throw new RecoveryQuestionNotFoundException(userName, question);
		}

		getJdbcTemplate().query(CHANGE_RECOVERY_ANSWER_SELECT_QUERY,
				new String[] { userName, this.getApplicationName(), question }, new RowCallbackHandler() {

					@Override
					public void processRow(ResultSet rs) throws SQLException {
						final Long recoveryInformationId = rs.getLong("RECOVERY_INFORMATION_ID");

						getJdbcTemplate().update(new PreparedStatementCreator() {
							@Override
							public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
								PreparedStatement preparedStatement = con
										.prepareStatement(CHANGE_RECOVERY_ANSWER_UPDATE_QUERY);
								preparedStatement.setString(1, getPasswordEncoder().encode(newAnswer.toUpperCase()));
								preparedStatement.setLong(2, recoveryInformationId);
								return preparedStatement;
							}
						});

					}

				});

		reportRecoveryQuestionChange(userName, question, WriteOperation.UPDATE);

		reportUserLastActivity(userName);
	}

	@Override
	public void deleteRecoveryQuestion(String userName, String password, String question)
			throws UserNotFoundException, RecoveryQuestionNotFoundException {
		checkDependencies();

		Objects.requireNonNull(userName, "The userName argument cannot be null.");
		Objects.requireNonNull(password, "The password argument cannot be null.");
		Objects.requireNonNull(question, "The question argument cannot be null.");

		if (!applicationExists(this.getApplicationName())) {
			throw new ApplicationNotFoundException(this.getApplicationName());
		}
		if (!userExists(userName, this.getApplicationName())) {
			throw new UserNotFoundException(userName);
		}

		getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(userName, password));

		if (!recoveryQuestionExists(userName, this.getApplicationName(), question)) {
			throw new RecoveryQuestionNotFoundException(userName, question);
		}

		// Report the delete before the delete happens.
		reportRecoveryQuestionChange(userName, question, WriteOperation.DELETE);

		getJdbcTemplate().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement preparedStatement = con.prepareStatement(RECOVERY_QUESTION_DELETE_QUERY);
				preparedStatement.setString(1, question);
				preparedStatement.setString(2, userName);
				preparedStatement.setString(3, getApplicationName());
				return preparedStatement;
			}
		});

		reportUserLastActivity(userName);
	}

	@Override
	public void unlock(String userName, String question, String answer)
			throws UserNotFoundException, WrongRecoveryAnswerException, RecoveryQuestionNotFoundException {
		checkDependencies();

		Objects.requireNonNull(userName, "The userName argument cannot be null.");

		checkUser(userName);

		this.getJdbcTemplate().query(UNLOCK_SELECT_QUERY, new String[] { userName, getApplicationName() },
				new RowCallbackHandler() {
					@Override
					public void processRow(ResultSet rs) throws SQLException {
						boolean enabled = rs.getBoolean("ENABLED");
						if (!enabled) {
							// A user who is disabled should not be able to
							// unlock his/her
							// account.
							throw new DisabledException(
									String.format("The user '%s' has been disabled in the application '%s'.", userName,
											getApplicationName()));
						}
					}
				});

		recover(userName, question, answer);

		// The user has been unlocked. So, we have to report who did the
		// changes, and insert appropriate data in the corresponding audit
		// table.
		reportMembershipChange(userName, true);
		reportUserLastActivity(userName);
	}

	@Override
	public boolean isPasswordExpired(String userName) throws UserNotFoundException {
		checkDependencies();

		Objects.requireNonNull(userName, "The userName argument cannot be null.");

		checkUser(userName);

		return getJdbcTemplate().queryForObject(IS_PASSWORD_EXPIRED_SELECT_QUERY,
				new String[] { userName, this.getApplicationName() }, new RowMapper<Boolean>() {

					@Override
					public Boolean mapRow(ResultSet rs, int rowNum) throws SQLException {
						final Date lastPasswordChangeDate = rs.getDate("LAST_PWD_CHANGE_DATE");
						final Date creationDate = rs.getDate("CREATION_DATE");
						final short minimumPasswordAge = rs.getShort("MIN_AGE");
						final short maximumPasswordAge = rs.getShort("MAX_AGE");

						// If the maximum password age is greater than 0, then
						// the passwords
						// will expire. If the maximum password age is 0, then
						// passwords will
						// never expire.
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

	@Override
	public boolean userExists(String userName) {
		checkDependencies();

		Objects.requireNonNull(userName, "The userName argument cannot be null.");

		return userExists(userName, this.getApplicationName());
	}

	@Override
	public String getRecoveryQuestion(String userName)
			throws UserNotFoundException, RecoveryInformationNotFoundException, CredentialsNotFoundException {
		checkDependencies();

		Objects.requireNonNull(userName, "The userName argument cannot be null.");

		checkUser(userName);

		final List<String> questions = getRecoveryQuestionsForUser(userName);
		if (questions == null || questions.isEmpty()) {
			throw new RecoveryInformationNotFoundException(userName);
		}
		// We want to get a random question from the available recovery
		// questions.
		Random random = new Random();
		int index = random.nextInt(questions.size());
		return questions.get(index);
	}

	@Override
	public List<String> getRecoveryQuestions(String userName)
			throws UserNotFoundException, RecoveryInformationNotFoundException {
		checkDependencies();

		Objects.requireNonNull(userName, "The userName argument cannot be null.");

		checkUser(userName);

		List<String> questions = getRecoveryQuestionsForUser(userName);
		if (questions == null || questions.isEmpty()) {
			throw new RecoveryInformationNotFoundException(userName);
		}
		return questions;
	}

	@Override
	public Date getLastLoginDate(String userName) throws UserNotFoundException {
		checkDependencies();

		Objects.requireNonNull(userName, "The userName argument cannot be null.");

		checkUser(userName);

		final List<Date> loginDates = getJdbcTemplate().query(LAST_LOGIN_DATE_SELECT_QUERY,
				new String[] { userName, this.getApplicationName() }, new RowMapper<Date>() {

					@Override
					public Date mapRow(ResultSet rs, int rowNum) throws SQLException {
						return rs.getDate("LOGIN_DATE");
					}

				});

		if (loginDates == null || loginDates.isEmpty() || loginDates.size() < 2) {
			return null;
		}
		Collections.sort(loginDates, new Comparator<Date>() {

			@Override
			public int compare(Date d1, Date d2) {
				// We want to sort the login dates starting from the
				// most recent history (i.e. in reverse order of
				// creation). We therefore multiply by -1. By so doing, we
				// will be able to select the second to last login date;
				return -1 * d1.compareTo(d2);
			}

		});

		return loginDates.get(1);
	}

	private void checkPasswordHistoryViolation(String userName, String rawPassword) {
		this.getJdbcTemplate().query(CHECK_PASSWORD_HISTORY_VIOLATION_SELECT_QUERY,
				new String[] { userName, this.getApplicationName() }, new RowCallbackHandler() {
					@Override
					public void processRow(ResultSet rs) throws SQLException {
						short passwordHistoryThreshold = rs.getShort("HISTORY_THRESHOLD");
						final String encodedassword = rs.getString("PASSWORD");
						if (getPasswordEncoder().matches(rawPassword, encodedassword)) {
							throw new PasswordHistoryException(passwordHistoryThreshold);
						}
					}

				});

	}

	private void addRecoveryQuestions(String userName, String password, Map<String, String> questionAnswerPairs,
			boolean reset) {
		if (!applicationExists(this.getApplicationName())) {
			throw new ApplicationNotFoundException(this.getApplicationName());
		}
		if (!userExists(userName, this.getApplicationName())) {
			throw new UserNotFoundException(userName);
		}

		getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(userName, password));

		if (!membershipExists(userName, this.getApplicationName())) {
			throw new CredentialsNotFoundException(userName);
		}

		final List<String> oldRecoveryQuestions = getRecoveryQuestionsForUser(userName);

		questionAnswerPairs.keySet().stream().filter(x -> x != null)
				.forEach(x -> addRecoveryQuestion(userName, password, x, questionAnswerPairs.get(x), reset));

		if (reset) {
			final Set<String> questions = questionAnswerPairs.keySet();
			final LinkedList<String> questionsTobeDeleted = new LinkedList<>();
			for (String question : oldRecoveryQuestions) {
				if (!questions.contains(question)) {
					questionsTobeDeleted.push(question);
				}
			}
			if (!questionsTobeDeleted.isEmpty()) {
				final int numberOfQuestionsToBeDeleted = questionsTobeDeleted.size();
				StringBuilder inClause = new StringBuilder();
				for (int i = 0; i < numberOfQuestionsToBeDeleted; i++) {
					if (i > 0) {
						inClause.append(',');
					}
					inClause.append('?');
				}

				getJdbcTemplate().update(new PreparedStatementCreator() {
					@Override
					public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
						PreparedStatement preparedStatement = con.prepareStatement(ADD_RECOVERY_QUESTION_DELETE_QUERY
								.replace(QUESTIONS_PARAMETER_PLACEHOLDER, inClause.toString()));
						for (int i = 0; i < numberOfQuestionsToBeDeleted; i++) {
							preparedStatement.setString(i + 1, questionsTobeDeleted.pop());
						}
						preparedStatement.setString(numberOfQuestionsToBeDeleted + 1, userName);
						preparedStatement.setString(numberOfQuestionsToBeDeleted + 2, getApplicationName());
						return preparedStatement;
					}
				});
			}
		}
	}

	/**
	 * Gets the answer corresponding to the supplied question and associated to
	 * the supplied user name.
	 * 
	 * @param userName
	 *            the user name for the user for whom we want the recovery
	 *            answer.
	 * @param question
	 *            the question identifying the {@link RecoveryInformation}
	 * @return the recovery answer corresponding to the supplied question and
	 *         associated to the supplied user name
	 * @throws RecoveryQuestionNotFoundException
	 *             if no recovery answer corresponding to the supplied question
	 *             and associated to the supplied user name was found.
	 */
	private String getRecoveryAnswer(String userName, String question) throws RecoveryQuestionNotFoundException {
		final List<String> answers = getJdbcTemplate().query(RECOVERY_ANSWER_SELECT_QUERY,
				new String[] { userName, this.getApplicationName(), question }, new RowMapper<String>() {
					@Override
					public String mapRow(ResultSet rs, int rowNum) throws SQLException {
						return rs.getString(1);
					}
				});

		if (answers == null || answers.isEmpty()) {
			throw new RecoveryQuestionNotFoundException(userName, question);
		}

		return answers.get(0);
	}

	private void addRecoveryQuestion(String userName, String password, String question, String answer, boolean reset) {
		if (!applicationExists(this.getApplicationName())) {
			throw new ApplicationNotFoundException(this.getApplicationName());
		}
		if (!userExists(userName, this.getApplicationName())) {
			throw new UserNotFoundException(userName);
		}

		getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(userName, password));

		if (!membershipExists(userName, this.getApplicationName())) {
			throw new CredentialsNotFoundException(userName);
		}
		final boolean questionExists = recoveryQuestionExists(userName, getApplicationName(), question);
		if (!reset) {
			if (questionExists) {
				throw new DuplicateRecoveryQuestionException(userName, question);
			}
		}

		final Timestamp now = new Timestamp(Calendar.getInstance().getTimeInMillis());
		final String encodedAnswer = this.getPasswordEncoder().encode(answer.toUpperCase());

		getJdbcTemplate().query(MEMBERSHIP_ID_SELECT_QUERY, new String[] { userName, getApplicationName() },
				new RowCallbackHandler() {

					@Override
					public void processRow(ResultSet rs) throws SQLException {
						getJdbcTemplate().update(new PreparedStatementCreator() {
							@Override
							public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
								final Long membershipId = rs.getLong("ID");
								if (!questionExists) {
									PreparedStatement preparedStatement = con
											.prepareStatement(ADD_RECOVERY_INFORMATION_INSERT_QUERY);
									preparedStatement.setString(1, encodedAnswer);
									preparedStatement.setTimestamp(2, now);
									preparedStatement.setString(3, userName);
									preparedStatement.setBoolean(4, true);
									preparedStatement.setTimestamp(5, now);
									preparedStatement.setString(6, question);
									preparedStatement.setString(7, userName);
									preparedStatement.setLong(8, 1);
									preparedStatement.setLong(9, membershipId);
									return preparedStatement;
								} else {
									final long version = getJdbcTemplate().queryForObject(
											ADD_RECOVERY_QUESTION_SELECT_QUERY,
											new String[] { userName, getApplicationName(), question },
											new RowMapper<Long>() {
												@Override
												public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
													return rs.getLong("VERSION");
												}
											});

									PreparedStatement preparedStatement = con
											.prepareStatement(ADD_RECOVERY_INFORMATION_UPDATE_QUERY);
									preparedStatement.setString(1, encodedAnswer);
									preparedStatement.setBoolean(2, true);
									preparedStatement.setTimestamp(3, now);
									preparedStatement.setString(4, userName);
									preparedStatement.setLong(5, version + 1);
									preparedStatement.setString(6, question);
									preparedStatement.setLong(7, membershipId);
									return preparedStatement;
								}
							}
						});
					}
				});

		reportRecoveryQuestionChange(userName, question,
				questionExists ? WriteOperation.UPDATE : WriteOperation.INSERT);
	}

	private boolean recoveryQuestionExists(String userName, String applicationName, String question) {
		return getJdbcTemplate().queryForObject(RECOVERY_QUESTION_COUNT_QUERY,
				new String[] { userName, applicationName, question }, new RowMapper<Boolean>() {
					@Override
					public Boolean mapRow(ResultSet rs, int rowNum) throws SQLException {
						return rs.getInt(1) == 1;
					}
				});
	}

	private boolean applicationExists(String applicationName) {
		return getJdbcTemplate().queryForObject(APPLICATIONS_COUNT_QUERY, new String[] { applicationName },
				new RowMapper<Boolean>() {
					@Override
					public Boolean mapRow(ResultSet rs, int rowNum) throws SQLException {
						return rs.getInt(1) == 1;
					}
				});
	}

	private boolean userExists(String userName, String applicationName) {
		return getJdbcTemplate().queryForObject(USERS_COUNT_QUERY, new String[] { userName, applicationName },
				new RowMapper<Boolean>() {
					@Override
					public Boolean mapRow(ResultSet rs, int rowNum) throws SQLException {
						return rs.getInt(1) == 1;
					}
				});
	}

	private boolean membershipExists(String userName, String applicationName) {
		return getJdbcTemplate().queryForObject(MEMBERSHIPS_COUNT_QUERY, new String[] { userName, applicationName },
				new RowMapper<Boolean>() {
					@Override
					public Boolean mapRow(ResultSet rs, int rowNum) throws SQLException {
						return rs.getInt(1) == 1;
					}
				});
	}

	private void checkUser(String userName) {
		if (!applicationExists(this.getApplicationName())) {
			throw new ApplicationNotFoundException(this.getApplicationName());
		}
		if (!userExists(userName, this.getApplicationName())) {
			throw new UserNotFoundException(userName);
		}
		if (!membershipExists(userName, this.getApplicationName())) {
			throw new CredentialsNotFoundException(userName);
		}
	}

	private List<String> getRecoveryQuestionsForUser(String userName) {
		return getJdbcTemplate().query(RECOVERY_QUESTIONS_SELECT_QUERY,
				new String[] { userName, this.getApplicationName() }, new RowMapper<String>() {
					@Override
					public String mapRow(ResultSet rs, int rowNum) throws SQLException {
						return rs.getString(1);
					}
				});
	}

	private void recover(String userName, String question, String answer) {
		final String persistentAnswer = getRecoveryAnswer(userName, question);
		final Timestamp now = new Timestamp(Calendar.getInstance().getTimeInMillis());

		getJdbcTemplate().query(RECOVER_SELECT_QUERY, new String[] { userName, this.getApplicationName() },
				new RowCallbackHandler() {
					@Override
					public void processRow(ResultSet rs) throws SQLException {
						getJdbcTemplate().update(new PreparedStatementCreator() {
							@Override
							public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
								final long membershipId = rs.getLong("MEMBERSHIP_ID");
								Timestamp failedRecoveryAttemptsWindowStart = rs
										.getTimestamp("FAILED_REVRY_ATMPT_WIN_START");
								final short attemptWindow = rs.getShort("ATTEMPT_WINDOW");
								short failedRecoveryAttemptsCount = rs.getShort("FAILED_REVRY_ATMPT_CNT");
								short lockoutThreshold = rs.getShort("LOCK_OUT_THRESHOLD");
								short failedPasswordAttemptCount = rs.getShort("FAILED_PWD_ATMPT_CNT");
								Timestamp failedPasswordAttemptWindowStart = rs
										.getTimestamp("FAILED_PWD_ATMPT_WIN_START");
								Timestamp lastLockoutDate = rs.getTimestamp("LAST_LOCK_OUT_DATE");
								boolean locked = rs.getBoolean("LOCKED");
								Timestamp firstJan1754 = new Timestamp(FIRST_JAN_1754.getTime());

								if (!getPasswordEncoder().matches(answer.toUpperCase(), persistentAnswer)) {
									Calendar c = Calendar.getInstance();
									c.setTime(failedRecoveryAttemptsWindowStart);
									c.add(Calendar.MINUTE, attemptWindow);
									if (now.getTime() > c.getTimeInMillis()) {
										failedRecoveryAttemptsWindowStart = now;
										failedRecoveryAttemptsCount = 1;
									} else {
										failedRecoveryAttemptsCount = (short) (failedRecoveryAttemptsCount + 1);
									}
									if (lockoutThreshold > 0) {
										// We start counting failed attempts at
										// 0.
										// Therefore, we must
										// lock the account at lockoutThreshold
										// minus 1.
										if (failedRecoveryAttemptsCount >= lockoutThreshold - 1) {
											locked = true;
											lastLockoutDate = now;
										}
									}
								} else {
									locked = false;
									failedRecoveryAttemptsCount = 0;
									failedRecoveryAttemptsWindowStart = firstJan1754;
									failedPasswordAttemptCount = 0;
									failedPasswordAttemptWindowStart = firstJan1754;
								}

								PreparedStatement preparedStatement = con.prepareStatement(RECOVER_UPDATE_QUERY);
								preparedStatement.setTimestamp(1, failedRecoveryAttemptsWindowStart);
								preparedStatement.setShort(2, failedRecoveryAttemptsCount);
								preparedStatement.setBoolean(3, locked);
								preparedStatement.setTimestamp(4, lastLockoutDate);
								preparedStatement.setShort(5, failedPasswordAttemptCount);
								preparedStatement.setTimestamp(6, failedPasswordAttemptWindowStart);
								preparedStatement.setLong(7, membershipId);
								return preparedStatement;
							}
						});

						getJdbcTemplate().update(new PreparedStatementCreator() {
							@Override
							public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
								final long userId = rs.getLong("USER_ID");
								PreparedStatement preparedStatement = con
										.prepareStatement(LAST_ACTIVITY_DATE_UPDATE_QUERY);
								preparedStatement.setTimestamp(1, now);
								preparedStatement.setLong(2, userId);
								return preparedStatement;
							}
						});
					}
				});

		if (!getPasswordEncoder().matches(answer.toUpperCase(), persistentAnswer)) {
			throw new WrongRecoveryAnswerException(userName, question);
		}
	}

	private void setPassword(String userName, String newPassword, Date changeDate) {
		getJdbcTemplate().query(SET_PASSWORD_SELECT_QUERY, new String[] { userName, this.getApplicationName() },
				new RowCallbackHandler() {
					@Override
					public void processRow(ResultSet rs) throws SQLException {
						final Long membershipId = rs.getLong("MEMBERSHIP_ID");
						final Short passwordHistoryThreshold = rs.getShort("PASSWORD_HISTORY_THRESHOLD");
						final LinkedList<Long> passwordHistoryIds = new LinkedList<>();

						getJdbcTemplate().query(PASSWORD_HISTORY_IDS_SELECT_QUERY, new Long[] { membershipId },
								new RowCallbackHandler() {

									@Override
									public void processRow(ResultSet rs) throws SQLException {
										int row = rs.getRow();
										if (row >= passwordHistoryThreshold) {
											// We have sorted the password
											// histories starting
											// from the
											// most recently created (i.e. in
											// reverse order of
											// creation). By so doing, we
											// will be able to delete old
											// password histories;
											passwordHistoryIds.push(rs.getLong("ID"));
										}
									}
								});
						if (!passwordHistoryIds.isEmpty()) {

							// Build the in-clause parameters.
							StringBuilder inClause = new StringBuilder();
							for (int i = 0; i < passwordHistoryIds.size(); i++) {
								if (i > 0) {
									inClause.append(',');
								}
								inClause.append('?');
							}

							getJdbcTemplate().update(new PreparedStatementCreator() {
								@Override
								public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
									PreparedStatement preparedStatement = con
											.prepareStatement(PASSWORD_HISTORIES_DELETE_QUERY
													.replace(IDS_PARAMETER_PLACEHOLDER, inClause.toString()));
									for (int i = 0; i < passwordHistoryIds.size(); i++) {
										preparedStatement.setLong(i + 1, passwordHistoryIds.pop());
									}
									return preparedStatement;
								}
							});
						}

						final String password = getPasswordEncoder().encode(newPassword);
						final Timestamp now = new Timestamp(Calendar.getInstance().getTimeInMillis());

						getJdbcTemplate().update(new PreparedStatementCreator() {
							@Override
							public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
								PreparedStatement preparedStatement = con
										.prepareStatement(PASSWORD_HISTORIES_INSERT_QUERY);
								preparedStatement.setTimestamp(1,
										changeDate != null ? new Timestamp(changeDate.getTime()) : null);
								preparedStatement.setTimestamp(2, now);
								preparedStatement.setString(3, userName);
								preparedStatement.setTimestamp(4, now);
								preparedStatement.setString(5, password);
								preparedStatement.setString(6, userName);
								preparedStatement.setLong(7, 1);
								preparedStatement.setLong(8, membershipId);
								return preparedStatement;
							}
						});

						getJdbcTemplate().update(new PreparedStatementCreator() {
							@Override
							public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
								PreparedStatement preparedStatement = con.prepareStatement(PASSWORD_UPDATE_QUERY);
								preparedStatement.setString(1, password);
								preparedStatement.setTimestamp(2, now);
								preparedStatement.setLong(3, membershipId);
								return preparedStatement;
							}
						});

					}
				});

	}

	private void checkDependencies() {
		if (this.getApplicationName() == null) {
			throw new IllegalStateException("The application name cannot be null.");
		}
		if (this.getPasswordEncoder() == null) {
			throw new IllegalStateException("The password encoder cannot be null.");
		}
		if (this.getAuthenticationManager() == null) {
			throw new IllegalStateException("The authentication manager cannot be null.");
		}
		if (this.getUserDetailsService() == null) {
			throw new IllegalStateException("The user details service cannot be null.");
		}
	}

	private Authentication setAuthentication(String userName, String password) {
		final UserDetails userDetails = this.getUserDetailsService().loadUserByUsername(userName);
		final UsernamePasswordAuthenticationToken newAuthentication = new UsernamePasswordAuthenticationToken(userName,
				password, userDetails.getAuthorities());

		SecurityContextHolder.getContext().setAuthentication(newAuthentication);

		return newAuthentication;
	}

	private boolean canChangePassword(String userName) {
		return getJdbcTemplate().queryForObject(CAN_CHANGE_PASSWORD_SELECT_QUERY,
				new String[] { userName, this.getApplicationName() }, new RowMapper<Boolean>() {
					@Override
					public Boolean mapRow(ResultSet rs, int rowNum) throws SQLException {
						final Timestamp lastPasswordChangeDate = rs.getTimestamp("LAST_PWD_CHANGE_DATE");
						final Timestamp creationDate = rs.getTimestamp("CREATION_DATE");
						final short passwordMinAge = rs.getShort("PASSWORD_MIN_AGE");
						Calendar calendar = Calendar.getInstance();
						if (lastPasswordChangeDate == null
								|| lastPasswordChangeDate.getTime() < creationDate.getTime()) {
							calendar.setTime(creationDate);
						} else {
							calendar.setTime(lastPasswordChangeDate);
						}
						calendar.add(Calendar.DATE, passwordMinAge);

						return calendar.getTimeInMillis() <= Calendar.getInstance().getTimeInMillis();
					}
				});
	}

	private void reportMembershipChange(String userName, boolean isSelfService) {
		ChangeReporterInfo changeReporterInfo = getGenericChangeReporterInfoBuilder(MEMBERSHIP_CHANGE_SELECT_QUERY,
				new String[] { userName, this.getApplicationName() }, "MEMBERSHIPS", "MEMBERSHIPS_AUDIT",
				WriteOperation.UPDATE).build();
		new ChangeReporter(this.getJdbcTemplate()).report(userName, isSelfService, changeReporterInfo);
	}

	private void reportRecoveryQuestionChange(String userName, String question, WriteOperation writeOperation) {
		ChangeReporterInfo changeReporterInfo = getGenericChangeReporterInfoBuilder(
				RECOVERY_QUESTION_CHANGE_SELECT_QUERY, new String[] { userName, this.getApplicationName(), question },
				"RECOVERY_INFORMATION", "RECOVERY_INFORMATION_AUDIT", writeOperation).build();
		new ChangeReporter(this.getJdbcTemplate()).report(userName, true, changeReporterInfo);
	}

	private ChangeReporterInfoBuilder getGenericChangeReporterInfoBuilder(String selectQuery, Object[] selectArgs,
			String sourceTableName, String targetTableName, WriteOperation writeOperation) {
		return new ChangeReporterInfoBuilder().selectQuery(selectQuery).selectArgs(selectArgs).sourceIdColumnLabel("ID")
				.sourceIdColumnName("ID").sourceVersionColumnLabel("VERSION").sourceVersionColumnName("VERSION")
				.sourceUpdaterColumnLabel("UPDATER").sourceUpdaterColumnName("UPDATER")
				.sourceLastUpdateDateColumnLabel("LAST_UPDATE_DATE").sourceLastUpdateDateColumnName("LAST_UPDATE_DATE")
				.sourceTableName(sourceTableName).targetTableName(targetTableName)
				.targetLastUpdateDateColumnName("LAST_UPDATE_DATE").targetUpdaterColumnName("UPDATER")
				.targetVersionColumnName("VERSION").targetWriteOperation(writeOperation)
				.targetWriteOperationColumnName("WRITE_OPERATION");

	}

	private void reportUserChange(String userName, boolean isSelfService) {
		ChangeReporterInfo changeReporterInfo = getGenericChangeReporterInfoBuilder(USER_CHANGE_SELECT_QUERY,
				new String[] { userName, this.getApplicationName() }, "USERS", "USERS_AUDIT", WriteOperation.UPDATE)
						.build();
		new ChangeReporter(this.getJdbcTemplate()).report(userName, isSelfService, changeReporterInfo);
	}

	private void reportUserLastActivity(String userName) {
		getJdbcTemplate().query(USER_ID_SELECT_QUERY, new String[] { userName, this.getApplicationName() },
				new RowCallbackHandler() {
					@Override
					public void processRow(ResultSet rs) throws SQLException {
						final Timestamp now = new Timestamp(Calendar.getInstance().getTimeInMillis());

						getJdbcTemplate().update(new PreparedStatementCreator() {
							@Override
							public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
								PreparedStatement preparedStatement = con
										.prepareStatement(LAST_ACTIVITY_DATE_UPDATE_QUERY);
								preparedStatement.setTimestamp(1, now);
								preparedStatement.setLong(2, rs.getLong("ID"));
								return preparedStatement;
							}
						});

					}
				});

		reportUserChange(userName, true);
	}
}
