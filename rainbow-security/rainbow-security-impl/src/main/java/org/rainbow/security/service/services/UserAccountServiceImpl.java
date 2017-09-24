package org.rainbow.security.service.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.rainbow.persistence.Dao;
import org.rainbow.persistence.SearchOptions;
import org.rainbow.security.orm.entities.Application;
import org.rainbow.security.orm.entities.LockoutPolicy;
import org.rainbow.security.orm.entities.LoginHistory;
import org.rainbow.security.orm.entities.Membership;
import org.rainbow.security.orm.entities.PasswordHistory;
import org.rainbow.security.orm.entities.PasswordPolicy;
import org.rainbow.security.orm.entities.RecoveryInformation;
import org.rainbow.security.orm.entities.User;
import org.rainbow.security.service.exceptions.ApplicationNotFoundException;
import org.rainbow.security.service.exceptions.CredentialsNotFoundException;
import org.rainbow.security.service.exceptions.DuplicateRecoveryQuestionException;
import org.rainbow.security.service.exceptions.InvalidPasswordException;
import org.rainbow.security.service.exceptions.MinimumPasswordAgeViolationException;
import org.rainbow.security.service.exceptions.PasswordHistoryException;
import org.rainbow.security.service.exceptions.RainbowSecurityServiceException;
import org.rainbow.security.service.exceptions.RecoveryInformationNotFoundException;
import org.rainbow.security.service.exceptions.RecoveryQuestionNotFoundException;
import org.rainbow.security.service.exceptions.UserNotFoundException;
import org.rainbow.security.service.exceptions.WrongRecoveryAnswerException;
import org.rainbow.security.utilities.DateUtil;
import org.rainbow.security.utilities.PasswordUtil;
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

public class UserAccountServiceImpl implements UserAccountService {
	private final Logger logger = Logger.getLogger(this.getClass().getName());
	private final static Date FIRST_JAN_1754 = DateUtil.toDate("1754-01-01");

	private Dao<User, Long, SearchOptions> userDao;
	private Dao<Application, Long, SearchOptions> applicationDao;
	private String applicationName;
	private PasswordEncoder passwordEncoder;
	private AuthenticationManager authenticationManager;
	private UserDetailsService userDetailsService;

	public UserAccountServiceImpl() {
	}

	public Dao<User, Long, SearchOptions> getUserDao() {
		return userDao;
	}

	public void setUserDao(Dao<User, Long, SearchOptions> dao) {
		this.userDao = dao;
	}

	public Dao<Application, Long, SearchOptions> getApplicationDao() {
		return applicationDao;
	}

	public void setApplicationDao(Dao<Application, Long, SearchOptions> applicationDao) {
		this.applicationDao = applicationDao;
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
		if (userName == null) {
			throw new IllegalArgumentException("The userName argument cannot be null.");
		}
		if (password == null) {
			throw new IllegalArgumentException("The password argument cannot be null.");
		}

		if (!SearchUtil.applicationExists(getApplicationName(), this.getApplicationDao())) {
			throw new ApplicationNotFoundException(getApplicationName());
		}

		if (!userExists(userName)) {
			throw new UserNotFoundException(userName);
		}

		final User user = SearchUtil.getUser(userName, getApplicationName(), this.getUserDao(),
				this.getApplicationDao());

		Membership membership = user.getMembership();

		if (membership == null) {
			throw new CredentialsNotFoundException(userName);
		}

		final Application application = user.getApplication();

		if (!PasswordUtil.isValidPassword(password, application.getPasswordPolicy())) {
			throw new InvalidPasswordException();
		}

		int passwordHistoryLength = application.getPasswordPolicy().getHistoryThreshold();

		List<PasswordHistory> passwordHistories = membership.getPasswordHistories();

		if (passwordHistories != null) {
			for (PasswordHistory passwordHistory : passwordHistories) {
				if (getPasswordEncoder().matches(password, passwordHistory.getPassword())) {
					throw new PasswordHistoryException(passwordHistoryLength);
				}
			}
		}
		// When an administrator sets a user's password, the last password
		// change date should be set in the past so that the user should be
		// prompted to change his/her password if the minimum password age is 0.
		setPassword(membership, password, FIRST_JAN_1754, passwordHistories, passwordHistoryLength);

		updateUser(user);
	}

	@Override
	public void changePassword(String userName, String oldPassword, String newPassword)
			throws UserNotFoundException, InvalidPasswordException {
		checkDependencies();
		if (userName == null) {
			throw new IllegalArgumentException("The userName argument cannot be null.");
		}
		if (oldPassword == null) {
			throw new IllegalArgumentException("The oldPassword argument cannot be null.");
		}
		if (newPassword == null) {
			throw new IllegalArgumentException("The newPassword argument cannot be null.");
		}

		final Application application = SearchUtil.getApplication(getApplicationName(), this.getApplicationDao());

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

		Membership membership = SearchUtil
				.getUser(userName, getApplicationName(), this.getUserDao(), this.getApplicationDao()).getMembership();

		if (!PasswordUtil.isValidPassword(newPassword, application.getPasswordPolicy())) {
			throw new InvalidPasswordException();
		}
		if (!canChangePassword(membership, application)) {
			throw new MinimumPasswordAgeViolationException();
		}

		final int passwordHistoryThreshold = application.getPasswordPolicy().getHistoryThreshold();

		final List<PasswordHistory> passwordHistories = membership.getPasswordHistories();

		if (passwordHistories != null) {
			if (passwordHistories.stream().filter(x -> getPasswordEncoder().matches(newPassword, x.getPassword()))
					.findFirst().isPresent()) {
				throw new PasswordHistoryException(passwordHistoryThreshold);
			}
		}

		setPassword(membership, newPassword, new Date(), passwordHistories, passwordHistoryThreshold);

		setAuthentication(userName, newPassword);
	}

	@Override
	public void resetPassword(String userName, String newPassword, String question, String answer)
			throws UserNotFoundException, InvalidPasswordException, WrongRecoveryAnswerException,
			RecoveryQuestionNotFoundException {
		checkDependencies();
		if (userName == null) {
			throw new IllegalArgumentException("The userName argument cannot be null.");
		}
		if (newPassword == null) {
			throw new IllegalArgumentException("The newPassword argument cannot be null.");
		}
		if (question == null) {
			throw new IllegalArgumentException("The question argument cannot be null.");
		}
		if (answer == null) {
			throw new IllegalArgumentException("The answer argument cannot be null.");
		}

		final Application application = SearchUtil.getApplication(getApplicationName(), this.getApplicationDao());

		if (!userExists(userName)) {
			throw new UserNotFoundException(userName);
		}

		final User user = SearchUtil.getUser(userName, getApplicationName(), this.getUserDao(),
				this.getApplicationDao());

		final PasswordPolicy passwordPolicy = application.getPasswordPolicy();

		if (!PasswordUtil.isValidPassword(newPassword, passwordPolicy))
			throw new InvalidPasswordException();

		final Membership membership = user.getMembership();

		if (membership == null) {
			throw new CredentialsNotFoundException(userName);
		}

		if (membership.isLockedOut()) {
			throw new LockedException(String.format("The user '%s' has been locked in the application '%s'.", userName,
					getApplicationName()));
		}

		if (!membership.isEnabled()) {
			throw new DisabledException(String.format("The user '%s' has been disabled in the application '%s'.",
					userName, getApplicationName()));
		}

		updateFailedRecoveryAttemptsCount(membership, question, answer);

		if (!getPasswordEncoder().matches(answer.toUpperCase(), getRecoveryAnswer(membership, question))) {
			throw new WrongRecoveryAnswerException(userName, question);
		}

		// PasswordGenerator passwordGenerator = new
		// PasswordGenerator(passwordPolicy.getMinimumPasswordLength(),
		// passwordPolicy.getMaximumPasswordLength(),
		// passwordPolicy.getMinimumSpecialCharacters(),
		// passwordPolicy.getMinimumLowercaseCharacters(),
		// passwordPolicy.getMinimumUppercaseCharacters(),
		// passwordPolicy.getMinimumNumeric());
		// String generatedPassword = passwordGenerator.generate();

		final int passwordHistoryThreshold = application.getPasswordPolicy().getHistoryThreshold();

		final List<PasswordHistory> passwordHistories = membership.getPasswordHistories();

		if (passwordHistories != null) {
			if (passwordHistories.stream().filter(x -> getPasswordEncoder().matches(newPassword, x.getPassword()))
					.findFirst().isPresent()) {
				throw new PasswordHistoryException(passwordHistoryThreshold);
			}
		}

		setPassword(membership, newPassword, new Date(), passwordHistories, passwordPolicy.getHistoryThreshold());

		updateUser(user);

		setAuthentication(userName, newPassword);
	}

	@Override
	public void addRecoveryQuestion(String userName, String password, String question, String answer)
			throws UserNotFoundException, DuplicateRecoveryQuestionException {
		checkDependencies();
		if (userName == null) {
			throw new IllegalArgumentException("The userName argument cannot be null.");
		}
		if (password == null) {
			throw new IllegalArgumentException("The password argument cannot be null.");
		}
		if (question == null) {
			throw new IllegalArgumentException("The question argument cannot be null.");
		}
		if (answer == null) {
			throw new IllegalArgumentException("The answer argument cannot be null.");
		}

		if (!SearchUtil.applicationExists(getApplicationName(), this.getApplicationDao())) {
			throw new ApplicationNotFoundException(this.getApplicationName());
		}

		final User user = SearchUtil.getUser(userName, getApplicationName(), this.getUserDao(),
				this.getApplicationDao());

		getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(userName, password));

		addRecoveryInformation(user, question, answer, false);

		updateUser(user);
	}

	@Override
	public void addRecoveryQuestions(String userName, String password, Map<String, String> questionAnswerPairs)
			throws UserNotFoundException, DuplicateRecoveryQuestionException {
		addRecoveryQuestions(userName, password, questionAnswerPairs, false);
	}

	@Override
	public void resetRecoveryQuestions(String userName, String password, Map<String, String> questionAnswerPairs)
			throws UserNotFoundException {
		addRecoveryQuestions(userName, password, questionAnswerPairs, true);
	}

	private void addRecoveryQuestions(String userName, String password, Map<String, String> questionAnswerPairs,
			boolean reset) {
		checkDependencies();
		if (userName == null) {
			throw new IllegalArgumentException("The userName argument cannot be null.");
		}
		if (password == null) {
			throw new IllegalArgumentException("The password argument cannot be null.");
		}
		if (questionAnswerPairs == null || questionAnswerPairs.isEmpty() || questionAnswerPairs.keySet().stream()
				.filter(x -> x != null && questionAnswerPairs.get(x) != null).count() == 0) {
			throw new IllegalArgumentException(
					"The questionAnswerPairs argument can neither be null nor empty, and must contain at least one question with a non-null answer.");
		}
		if (!SearchUtil.applicationExists(getApplicationName(), this.getApplicationDao())) {
			throw new ApplicationNotFoundException(this.getApplicationName());
		}

		final User user = SearchUtil.getUser(userName, getApplicationName(), this.getUserDao(),
				this.getApplicationDao());

		getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(userName, password));

		final Membership membership = user.getMembership();

		if (membership == null) {
			throw new CredentialsNotFoundException(user.getUserName());
		}

		final List<RecoveryInformation> recoveryInformation = membership.getRecoveryInformation();
		final List<RecoveryInformation> oldRecoveryInformation = new ArrayList<>(recoveryInformation);

		questionAnswerPairs.keySet().stream().filter(x -> x != null)
				.forEach(x -> addRecoveryInformation(user, x, questionAnswerPairs.get(x), reset));

		if (reset) {
			final Set<String> questions = questionAnswerPairs.keySet();
			for (RecoveryInformation info : oldRecoveryInformation) {
				if (!questions.contains(info.getQuestion())) {
					recoveryInformation.remove(info);
				}
			}
		}
		updateUser(user);
	}

	@Override
	public void changeRecoveryAnswer(String userName, String password, String question, String newAnswer)
			throws UserNotFoundException, RecoveryQuestionNotFoundException {
		checkDependencies();
		if (userName == null) {
			throw new IllegalArgumentException("The userName argument cannot be null.");
		}
		if (password == null) {
			throw new IllegalArgumentException("The password argument cannot be null.");
		}
		if (question == null) {
			throw new IllegalArgumentException("The question argument cannot be null.");
		}
		if (newAnswer == null) {
			throw new IllegalArgumentException("The newAnswer argument cannot be null.");
		}
		if (!SearchUtil.applicationExists(getApplicationName(), this.getApplicationDao())) {
			throw new ApplicationNotFoundException(getApplicationName());
		}

		getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(userName, password));

		final User user = SearchUtil.getUser(userName, getApplicationName(), this.getUserDao(),
				this.getApplicationDao());
		Membership membership = user.getMembership();

		if (membership == null) {
			throw new CredentialsNotFoundException(userName);
		}

		RecoveryInformation recoveryInformation = getRecoveryInformation(membership, question);
		recoveryInformation.setAnswer(getPasswordEncoder().encode(newAnswer.toUpperCase()));

		user.setLastActivityDate(new Date());

		updateUser(user);

	}

	@Override
	public void deleteRecoveryQuestion(String userName, String password, String question)
			throws UserNotFoundException, RecoveryQuestionNotFoundException {
		checkDependencies();
		if (userName == null) {
			throw new IllegalArgumentException("The userName argument cannot be null.");
		}
		if (password == null) {
			throw new IllegalArgumentException("The password argument cannot be null.");
		}
		if (question == null) {
			throw new IllegalArgumentException("The question argument cannot be null.");
		}

		if (!SearchUtil.applicationExists(getApplicationName(), this.getApplicationDao())) {
			throw new ApplicationNotFoundException(this.getApplicationName());
		}

		final User user = SearchUtil.getUser(userName, getApplicationName(), this.getUserDao(),
				this.getApplicationDao());

		getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(userName, password));

		Membership membership = user.getMembership();

		List<RecoveryInformation> recoveryInformation = membership.getRecoveryInformation();
		if (recoveryInformation == null) {
			recoveryInformation = new ArrayList<>();
			membership.setRecoveryInformation(recoveryInformation);
		}

		final Optional<RecoveryInformation> optional = recoveryInformation.stream()
				.filter(x -> question.equals(x.getQuestion())).findFirst();
		if (!optional.isPresent()) {
			throw new RecoveryQuestionNotFoundException(userName, question);
		}

		RecoveryInformation info = optional.get();

		recoveryInformation.remove(info);

		updateUser(user);

	}

	@Override
	public void unlock(String userName, String question, String answer)
			throws UserNotFoundException, WrongRecoveryAnswerException, RecoveryQuestionNotFoundException {
		checkDependencies();
		if (userName == null) {
			throw new IllegalArgumentException("The userName argument cannot be null.");
		}

		if (!SearchUtil.applicationExists(getApplicationName(), this.getApplicationDao())) {
			throw new ApplicationNotFoundException(getApplicationName());
		}

		if (!userExists(userName)) {
			throw new UserNotFoundException(userName);
		}

		final User user = SearchUtil.getUser(userName, getApplicationName(), this.getUserDao(),
				this.getApplicationDao());

		Membership membership = user.getMembership();

		if (membership == null) {
			throw new CredentialsNotFoundException(userName);
		}

		if (!membership.isEnabled()) {
			// A user who is disabled should not be able to unlock his/her
			// account.
			throw new DisabledException(String.format("The user '%s' has been disabled in the application '%s'.",
					userName, this.getApplicationName()));
		}

		updateFailedRecoveryAttemptsCount(membership, question, answer);

		if (!getPasswordEncoder().matches(answer.toUpperCase(), getRecoveryAnswer(membership, question))) {
			throw new WrongRecoveryAnswerException(userName, question);
		}

		membership.setFailedRecoveryAttemptsWindowStart(FIRST_JAN_1754);
		membership.setFailedPasswordAttemptCount((short) 0);
		membership.setFailedPasswordAttemptWindowStart(FIRST_JAN_1754);
		membership.setFailedRecoveryAttemptsCount((short) 0);
		membership.setLockedOut(false);

		updateUser(user);

	}

	@Override
	public boolean isPasswordExpired(String userName) throws UserNotFoundException {
		checkDependencies();
		if (userName == null) {
			throw new IllegalArgumentException("The userName argument cannot be null.");
		}
		Application application = SearchUtil.getApplication(getApplicationName(), this.getApplicationDao());

		User user = SearchUtil.getUser(userName, getApplicationName(), this.getUserDao(), this.getApplicationDao());

		Membership membership = user.getMembership();

		if (membership == null) {
			throw new CredentialsNotFoundException(userName);
		}

		PasswordPolicy passwordPolicy = application.getPasswordPolicy();

		Date lastPasswordChangeDate = membership.getLastPasswordChangeDate();
		Date creationDate = membership.getCreationDate();
		Integer maximumPasswordAge = passwordPolicy.getMaxAge();
		Integer minimumPasswordAge = passwordPolicy.getMinAge();
		// If the maximum password age is greater than 0, then the passwords
		// will expire. If the maximum password age is 0, then passwords will
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
				if (lastPasswordChangeDateCalendar.getTimeInMillis() < creationDateCalendar.getTimeInMillis())
					return true;
			}

			lastPasswordChangeDateCalendar.add(Calendar.DATE, maximumPasswordAge);
			if (lastPasswordChangeDateCalendar.getTimeInMillis() < Calendar.getInstance().getTimeInMillis()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean userExists(String userName) {
		checkDependencies();
		return SearchUtil.userExists(userName, this.getApplicationName(), this.getUserDao(), this.getApplicationDao());
	}

	@Override
	public String getRecoveryQuestion(String userName)
			throws UserNotFoundException, RecoveryInformationNotFoundException, CredentialsNotFoundException {
		checkDependencies();
		final List<String> recoveryInformation = getRecoveryQuestionsForUser(userName);
		// We want to get a random question from the available recovery
		// questions.
		Random random = new Random();
		int index = random.nextInt(recoveryInformation.size());
		return recoveryInformation.get(index);
	}

	@Override
	public List<String> getRecoveryQuestions(String userName)
			throws UserNotFoundException, RecoveryInformationNotFoundException {
		checkDependencies();
		return getRecoveryQuestionsForUser(userName);
	}

	private String getRecoveryAnswer(Membership membership, String question) {
		return getRecoveryInformation(membership, question).getAnswer();
	}

	@Override
	public Date getLastLoginDate(String userName) throws UserNotFoundException {
		checkDependencies();
		final Membership membership = SearchUtil
				.getUser(userName, getApplicationName(), this.getUserDao(), this.getApplicationDao()).getMembership();

		if (membership == null) {
			throw new CredentialsNotFoundException(userName);
		}

		final List<LoginHistory> histories = membership.getLoginHistories();

		if (histories == null || histories.isEmpty() || histories.size() < 2) {
			return null;
		}
		Collections.sort(histories, new Comparator<LoginHistory>() {

			@Override
			public int compare(LoginHistory ph1, LoginHistory ph2) {
				// We want to sort the login histories starting from the
				// most recent history (i.e. in reverse order of
				// creation). We therefore multiply by -1. By so doing, we
				// will be able to select the second to last login history;
				return -1 * ph1.getLoginDate().compareTo(ph2.getLoginDate());
			}

		});

		return histories.get(1).getLoginDate();
	}

	/**
	 * Gets the {@link RecoveryInformation} corresponding to the supplied
	 * question and associated to the supplied {@link Membership}.
	 * 
	 * @param membership
	 *            the {@link Membership} from which we want to get the
	 *            {@link RecoveryInformation}
	 * @param question
	 *            the question identifying the {@link RecoveryInformation}
	 * @return the {@link RecoveryInformation} corresponding to the supplied
	 *         question and associated to the supplied {@link Membership}
	 * @throws RecoveryQuestionNotFoundException
	 *             if no {@link RecoveryInformation} corresponding to the
	 *             supplied question and associated to the supplied
	 *             {@link Membership} was found.
	 */
	private RecoveryInformation getRecoveryInformation(Membership membership, String question)
			throws RecoveryQuestionNotFoundException {
		Optional<RecoveryInformation> optional = membership.getRecoveryInformation().stream()
				.filter(x -> x.getQuestion().equals(question)).findFirst();
		if (!optional.isPresent()) {
			throw new RecoveryQuestionNotFoundException(membership.getUser().getUserName(), question);
		}
		return optional.get();
	}

	private void addRecoveryInformation(final User user, final String question, final String answer, boolean reset) {
		Membership membership = user.getMembership();

		List<RecoveryInformation> recoveryInformation = membership.getRecoveryInformation();
		if (recoveryInformation == null) {
			recoveryInformation = new ArrayList<>();
			membership.setRecoveryInformation(recoveryInformation);
		}
		if (!reset) {
			if (recoveryInformation.stream().filter(x -> question.equals(x.getQuestion())).findFirst().isPresent()) {
				throw new DuplicateRecoveryQuestionException(user.getUserName(), question);
			}
		}

		RecoveryInformation info = recoveryInformation.stream().filter(x -> x.getQuestion().equals(question))
				.findFirst().orElse(null);

		if (info == null) {
			info = new RecoveryInformation();
			recoveryInformation.add(info);
		}
		info.setMembership(membership);
		info.setAnswer(this.getPasswordEncoder().encode(answer.toUpperCase()));
		info.setQuestion(question);
		info.setEncrypted(true);
	}

	private List<String> getRecoveryQuestionsForUser(String userName) {
		if (userName == null) {
			throw new IllegalArgumentException("The userName argument cannot be null.");
		}
		if (!SearchUtil.applicationExists(getApplicationName(), this.getApplicationDao())) {
			throw new ApplicationNotFoundException(getApplicationName());
		}

		Membership membership = SearchUtil
				.getUser(userName, getApplicationName(), this.getUserDao(), this.getApplicationDao()).getMembership();

		if (membership == null) {
			throw new CredentialsNotFoundException(userName);
		}

		final List<RecoveryInformation> recoveryInformation = membership.getRecoveryInformation();

		if (recoveryInformation == null || recoveryInformation.isEmpty()) {
			throw new RecoveryInformationNotFoundException(userName);
		}
		return recoveryInformation.stream().map(x -> x.getQuestion()).collect(Collectors.toList());
	}

	private void updateFailedRecoveryAttemptsCount(Membership membership, String question, String answer) {
		final Application application = membership.getUser().getApplication();
		final LockoutPolicy lockoutPolicy = application.getLockoutPolicy();
		String persistentAnswer = getRecoveryAnswer(membership, question);
		Date now = new Date();
		if (!getPasswordEncoder().matches(answer.toUpperCase(), persistentAnswer)) {
			Calendar c = Calendar.getInstance();
			c.setTime(membership.getFailedRecoveryAttemptsWindowStart());
			c.add(Calendar.MINUTE, lockoutPolicy.getAttemptWindow());
			if (now.getTime() > c.getTimeInMillis()) {
				membership.setFailedRecoveryAttemptsWindowStart(now);
				membership.setFailedRecoveryAttemptsCount((short) 1);
			} else {
				membership.setFailedRecoveryAttemptsCount((short) (membership.getFailedRecoveryAttemptsCount() + 1));
			}
			if (lockoutPolicy != null && lockoutPolicy.getThreshold() > 0) {
				if (membership.getFailedRecoveryAttemptsCount() >= lockoutPolicy.getThreshold()) {
					membership.setLockedOut(true);
					membership.setLastLockoutDate(now);
				}
			}
		} else if (membership.getFailedRecoveryAttemptsCount() > 0) {
			membership.setFailedRecoveryAttemptsCount((short) 0);
			membership.setFailedRecoveryAttemptsWindowStart(FIRST_JAN_1754);
		}
		User user = membership.getUser();
		user.setLastActivityDate(now);
	}

	private void setPassword(Membership membership, String newPassword, Date changeDate,
			List<PasswordHistory> passwordHistories, int passwordHistoryThreshold) {
		if (passwordHistories == null) {
			passwordHistories = new ArrayList<>();
		}
		final int passwordHistorySize = passwordHistories.size();
		if (passwordHistorySize > 0 && passwordHistorySize > passwordHistoryThreshold) {
			Collections.sort(passwordHistories, new Comparator<PasswordHistory>() {

				@Override
				public int compare(PasswordHistory ph1, PasswordHistory ph2) {
					// We want to sort the password histories starting from the
					// most recently created (i.e. in reverse order of
					// creation). We therefore multiply by -1. By so doing, we
					// will be able to delete old password histories;
					return -1 * ph1.getCreationDate().compareTo(ph2.getCreationDate());
				}

			});

			for (int i = passwordHistorySize - 1; i >= passwordHistoryThreshold - 1; i--) {
				passwordHistories.remove(i);
			}
		}
		String password = getPasswordEncoder().encode(newPassword);

		PasswordHistory passwordHistory = new PasswordHistory();

		passwordHistory.setChangeDate(changeDate);
		passwordHistory.setMembership(membership);
		passwordHistory.setPassword(password);

		passwordHistories.add(passwordHistory);

		membership.setPasswordHistories(passwordHistories);
		membership.setPassword(password);
		membership.setLastPasswordChangeDate(changeDate);
	}

	private void checkDependencies() {
		if (this.getUserDao() == null) {
			throw new IllegalStateException("The user data access object cannot be null.");
		}
		if (this.getApplicationDao() == null) {
			throw new IllegalStateException("The application data access object cannot be null.");
		}
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

	private void updateUser(final User user) {
		try {
			this.getUserDao().update(user);
		} catch (Exception e) {
			throw new RainbowSecurityServiceException(e);
		}
	}

	private boolean canChangePassword(Membership membership, Application application) {
		Date lastPasswordChangeDate = membership.getLastPasswordChangeDate();
		Date creationDate = membership.getCreationDate();
		Calendar calendar = Calendar.getInstance();
		if (lastPasswordChangeDate == null || lastPasswordChangeDate.getTime() < creationDate.getTime()) {
			calendar.setTime(creationDate);
		} else {
			calendar.setTime(lastPasswordChangeDate);
		}
		calendar.add(Calendar.DATE, application.getPasswordPolicy().getMinAge());

		return calendar.getTimeInMillis() <= Calendar.getInstance().getTimeInMillis();
	}

}
