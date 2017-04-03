package org.rainbow.security.core.persistence.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.rainbow.core.persistence.DaoImpl;
import org.rainbow.core.persistence.Pageable;
import org.rainbow.security.core.entities.Application;
import org.rainbow.security.core.entities.Membership;
import org.rainbow.security.core.entities.PasswordHistory;
import org.rainbow.security.core.entities.PasswordPolicy;
import org.rainbow.security.core.entities.User;
import org.rainbow.security.core.persistence.exceptions.ApplicationNotFoundException;
import org.rainbow.security.core.persistence.exceptions.DuplicateUserException;
import org.rainbow.security.core.persistence.exceptions.InvalidPasswordException;
import org.rainbow.security.core.persistence.exceptions.MembershipNotFoundException;
import org.rainbow.security.core.persistence.exceptions.MinimumPasswordAgeViolationException;
import org.rainbow.security.core.persistence.exceptions.PasswordHistoryException;
import org.rainbow.security.core.persistence.exceptions.UserNotFoundException;
import org.rainbow.security.core.persistence.exceptions.UserNotFoundNameException;
import org.rainbow.security.core.persistence.exceptions.WrongPasswordQuestionAnswerException;
import org.rainbow.security.core.utilities.DateHelper;
import org.rainbow.security.core.utilities.EntityManagerHelper;
import org.rainbow.security.core.utilities.PasswordGenerator;
import org.rainbow.security.core.utilities.PersistenceSettings;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

@Pageable(attributeName = "id")
public class UserDao extends DaoImpl<User, Long> implements UserManager {

	@PersistenceContext(unitName = PersistenceSettings.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	private final String applicationName;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;

	private final static Date FIRST_JAN_1754 = DateHelper.toDate("1754-01-01");

	public UserDao(String applicationName, PasswordEncoder passwordEncoder,
			AuthenticationManager authenticationManager) {
		super(User.class);
		if (applicationName == null)
			throw new IllegalArgumentException("The applicationName argument cannot be null.");
		if (passwordEncoder == null)
			throw new IllegalArgumentException("The passwordEncoder argument cannot be null.");
		if (authenticationManager == null)
			throw new IllegalArgumentException("The authenticationManager argument cannot be null.");
		this.applicationName = applicationName;
		this.passwordEncoder = passwordEncoder;
		this.authenticationManager = authenticationManager;
	}

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	@Override
	public String getApplicationName() {
		return applicationName;
	}

	private boolean isValidPassword(String password, PasswordPolicy passwordPolicy) {
		if (password == null || password.isEmpty()) {
			return false;
		}

		if (password.length() < passwordPolicy.getMinimumPasswordLength()) {
			return false;
		}
		if (password.length() > passwordPolicy.getMaximumPasswordLength()) {
			return false;
		}
		int upperCaseCount = 0;
		int lowerCaseCount = 0;
		int digitsCount = 0;
		int specialCharCount = 0;
		for (int i = 0; i < password.length(); i++) {
			final int codePoint = password.codePointAt(i);
			if (Character.isUpperCase(codePoint)) {
				upperCaseCount++;
			} else if (Character.isLowerCase(codePoint)) {
				lowerCaseCount++;
			} else if (Character.isDigit(codePoint)) {
				digitsCount++;
			} else {
				specialCharCount++;
			}
		}
		if (upperCaseCount < passwordPolicy.getMinimumUppercaseCharacters()) {
			return false;
		}
		if (lowerCaseCount < passwordPolicy.getMinimumLowercaseCharacters()) {
			return false;
		}
		if (digitsCount < passwordPolicy.getMinimumNumeric()) {
			return false;
		}
		if (specialCharCount < passwordPolicy.getMinimumSpecialCharacters()) {
			return false;
		}
		return true;
	}

	@Override
	public void create(User user) throws Exception {
		Membership membership = user.getMembership();
		if (membership != null && !user.equals(membership.getUser())) {
			membership.setUser(user);
		}

		EntityManagerHelper helper = new EntityManagerHelper(em);
		Application persistentApplication = helper.getReference(Application.class, user.getApplication().getId());

		Map<String, Object> pathValuePairs = new HashMap<>();
		pathValuePairs.put("userName", user.getUserName());
		pathValuePairs.put("application.id", persistentApplication.getId());
		if (helper.exists(User.class, pathValuePairs)) {
			throw new DuplicateUserException(user.getUserName(), persistentApplication.getName());
		}

		user.setApplication(persistentApplication);

		if (membership != null && membership.getApplication() == null) {
			membership.setApplication(persistentApplication);
		}

		user.setLastActivityDate(FIRST_JAN_1754);

		// Membership membership = user.getMembership();
		// It can happen that the membership of the user is null. This can occur
		// particularly if we want an external system such as Active Directory
		// to perform the authentication.
		// In this case, it should still be possible to create a user without a
		// membership.
		if (membership != null) {
			if (!isValidPassword(membership.getPassword(), persistentApplication.getPasswordPolicy())) {
				throw new InvalidPasswordException();
			}

			Date now = new Date();

			membership.setCreationDate(now);
			membership.setFailedPasswordAnswerAttemptWindowStart(FIRST_JAN_1754);
			membership.setFailedPasswordAttemptWindowStart(FIRST_JAN_1754);
			if (membership.isLockedOut()) {
				membership.setLastLockoutDate(now);
			} else {
				membership.setLastLockoutDate(FIRST_JAN_1754);
			}
			membership.setLastLoginDate(FIRST_JAN_1754);
			membership.setLastPasswordChangeDate(FIRST_JAN_1754);

			membership.setPassword(passwordEncoder.encode(membership.getPassword()));
			membership.setEncrypted(true);

			if (membership.getPasswordQuestionAnswer() != null) {
				membership.setPasswordQuestionAnswer(
						passwordEncoder.encode(membership.getPasswordQuestionAnswer().toUpperCase()));
			}

			List<PasswordHistory> passwordHistories = new ArrayList<>();
			PasswordHistory passwordHistory = new PasswordHistory();
			passwordHistory.setChangeDate(now);
			passwordHistory.setMembership(membership);
			passwordHistory.setPassword(membership.getPassword());
			passwordHistories.add(passwordHistory);

			membership.setPasswordHistories(passwordHistories);

			em.persist(passwordHistory);

		}
		super.persist(user);
	}

	private Membership getMembership(String userName, String applicationName) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Membership> cq = cb.createQuery(Membership.class);
		Root<Membership> rt = cq.from(Membership.class);
		cq.select(rt);
		Predicate p1 = cb.equal(cb.upper(rt.join("user").get("userName")), userName.toUpperCase());
		Predicate p2 = cb.equal(cb.upper(rt.join("application").get("name")), applicationName.toUpperCase());
		cq.where(cb.and(p1, p2));
		TypedQuery<Membership> tq = em.createQuery(cq); // tq.getSingleResult()
														// throws an exception
														// when the user does
														// not exists.
		List<Membership> memberships = tq.getResultList();
		if (memberships != null && memberships.size() == 1) {
			return memberships.get(0);
		}
		return null;
	}

	private Boolean canChangePassword(String userName) throws UserNotFoundException, ApplicationNotFoundException {
		if (userName == null) {
			throw new IllegalArgumentException("The userName argument cannot be null.");
		}
		if (!userExists(userName, applicationName, new EntityManagerHelper(em))) {
			throw new UserNotFoundNameException(userName, applicationName);
		}
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createTupleQuery();
		Root<Membership> rt = cq.from(Membership.class);
		cq.select(cb.tuple(rt.get("lastPasswordChangeDate"), rt.get("application"), rt.get("creationDate")));
		Predicate p1 = cb.equal(cb.upper(rt.join("user").get("userName")), userName.toUpperCase());
		Predicate p2 = cb.equal(cb.upper(rt.join("application").get("name")), applicationName.toUpperCase());
		cq.where(cb.and(p1, p2));
		TypedQuery<Tuple> tq = em.createQuery(cq);
		List<Tuple> tuples = tq.getResultList();
		if (!tuples.isEmpty()) {
			Tuple tuple = tuples.get(0);
			Application application = (Application) tuple.get(1);
			if (application != null) {
				PasswordPolicy passwordPolicy = application.getPasswordPolicy();
				if (passwordPolicy != null) {
					Date lastPasswordChangeDate = (Date) tuple.get(0);
					Date creationDate = (Date) tuple.get(2);
					Calendar calendar = Calendar.getInstance();
					if (lastPasswordChangeDate == null || lastPasswordChangeDate.getTime() < creationDate.getTime()) {
						calendar.setTime(creationDate);
					} else {
						calendar.setTime(lastPasswordChangeDate);
					}
					calendar.add(Calendar.DATE, passwordPolicy.getMinimumPasswordAge());
					return calendar.getTimeInMillis() <= Calendar.getInstance().getTimeInMillis();
				}
			}
		}
		return null;
	}

	private List<PasswordHistory> getPasswordHistories(String userName, String applicationName) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PasswordHistory> cq = cb.createQuery(PasswordHistory.class);
		Root<PasswordHistory> rt = cq.from(PasswordHistory.class);
		cq.select(rt);
		Join<PasswordHistory, Membership> membershipJoin = rt.join("membership");
		Predicate p1 = cb.equal(cb.upper(membershipJoin.join("user").get("userName")), userName.toUpperCase());
		Predicate p2 = cb.equal(cb.upper(membershipJoin.join("application").get("name")),
				applicationName.toUpperCase());
		// Order them in descending order. This will allow us easily eliminate
		// older ones.
		cq.where(cb.and(p1, p2)).orderBy(cb.desc(rt.get("changeDate")));
		TypedQuery<PasswordHistory> tq = em.createQuery(cq);
		return tq.getResultList();
	}

	@Override
	public void setPassword(String userName, String password)
			throws InvalidPasswordException, PasswordHistoryException {

		if (userName == null) {
			throw new IllegalArgumentException("The userName argument cannot be null.");
		}
		if (password == null) {
			throw new IllegalArgumentException("The password argument cannot be null.");
		}

		if (!new EntityManagerHelper(em).exists(Application.class, "name", applicationName)) {
			throw new ApplicationNotFoundException(applicationName);
		}

		Membership membership = getMembership(userName, applicationName);

		if (membership == null) {
			throw new MembershipNotFoundException(userName, applicationName);
		}

		if (!isValidPassword(password, membership.getApplication().getPasswordPolicy())) {
			throw new InvalidPasswordException();
		}

		Application application = membership.getApplication();
		int passwordHistoryLength = application.getPasswordPolicy().getPasswordHistoryLength();

		List<PasswordHistory> passwordHistories = getPasswordHistories(userName, applicationName);
		if (passwordHistories != null) {
			for (PasswordHistory passwordHistory : passwordHistories) {
				if (passwordEncoder.matches(password, passwordHistory.getPassword())) {
					throw new PasswordHistoryException(passwordHistoryLength);
				}
			}
		}
		// When an administrator sets a user's password, the last password
		// change date should be set in the past so that the user should be
		// prompted to change his/her password if the minimum password age is 0.
		setPassword(membership, password, FIRST_JAN_1754, passwordHistories, passwordHistoryLength);
	}

	private void setPassword(Membership membership, String newPassword, Date changeDate,
			List<PasswordHistory> passwordHistories, int passwordHistoryLength) {
		if (passwordHistories == null) {
			passwordHistories = new ArrayList<>();
		}
		if (passwordHistories.size() > 0 && passwordHistories.size() > passwordHistoryLength) {
			for (int i = passwordHistories.size() - 1; i >= passwordHistoryLength - 1; i--) {
				em.remove(passwordHistories.get(i));
				passwordHistories.remove(i);
			}
		}
		String password = passwordEncoder.encode(newPassword);

		PasswordHistory passwordHistory = new PasswordHistory();

		passwordHistory.setChangeDate(changeDate);
		passwordHistory.setMembership(membership);
		passwordHistory.setPassword(password);
		passwordHistories.add(passwordHistory);

		em.persist(passwordHistory);
		membership.setPasswordHistories(passwordHistories);
		membership.setPassword(password);
		membership.setLastPasswordChangeDate(changeDate);
		em.merge(membership);
	}

	@Override
	public void changePassword(String oldPassword, String newPassword) throws AuthenticationException,
			InvalidPasswordException, PasswordHistoryException, MinimumPasswordAgeViolationException {

		if (oldPassword == null) {
			throw new IllegalArgumentException("The oldPassword argument cannot be null.");
		}
		if (newPassword == null) {
			throw new IllegalArgumentException("The newPassword argument cannot be null.");
		}

		Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();

		if (currentUser == null) {
			// This would indicate bad coding somewhere
			throw new AccessDeniedException(
					"Can't change password as no Authentication object found in context " + "for current user.");
		}

		String userName = currentUser.getName();

		em.clear();

		if (!new EntityManagerHelper(em).exists(Application.class, "name", applicationName)) {
			throw new ApplicationNotFoundException(applicationName);
		}

		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userName, oldPassword));
		} catch (CredentialsExpiredException e) {
			// We want it to be possible for a user whose credentials have
			// expired to be able to change his/her password.
			Logger.getLogger(this.getClass().getName()).log(Level.INFO,
					String.format("The user '%s', whose credentials have expired, is about to change his/her password.",
							userName),
					e);
		}
		Membership membership = getMembership(userName, applicationName);

		if (!isValidPassword(newPassword, membership.getApplication().getPasswordPolicy())) {
			throw new InvalidPasswordException();
		}
		if (!canChangePassword(userName)) {
			throw new MinimumPasswordAgeViolationException();
		}
		Application application = membership.getApplication();
		int passwordHistoryLength = application.getPasswordPolicy().getPasswordHistoryLength();

		List<PasswordHistory> passwordHistories = getPasswordHistories(userName, applicationName);
		if (passwordHistories != null) {
			for (PasswordHistory passwordHistory : passwordHistories) {

				if (passwordEncoder.matches(newPassword, passwordHistory.getPassword())) {
					throw new PasswordHistoryException(passwordHistoryLength);
				}
			}
		}
		setPassword(membership, newPassword, new Date(), passwordHistories, passwordHistoryLength);

		SecurityContextHolder.getContext().setAuthentication(createNewAuthentication(currentUser, newPassword));
	}

	protected Authentication createNewAuthentication(Authentication currentAuth, String newPassword) {

		UsernamePasswordAuthenticationToken newAuthentication = new UsernamePasswordAuthenticationToken(
				currentAuth.getPrincipal(), newPassword, currentAuth.getAuthorities());
		newAuthentication.setDetails(currentAuth.getDetails());

		return newAuthentication;
	}

	@Override
	public String resetPassword(String passwordQuestionAnswer)
			throws AuthenticationException, WrongPasswordQuestionAnswerException {
		if (passwordQuestionAnswer == null) {
			throw new IllegalArgumentException("The passwordQuestionAnswer argument cannot be null.");
		}

		Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();

		if (currentUser == null) {
			// This would indicate bad coding somewhere
			throw new AccessDeniedException(
					"Can't reset password as no Authentication object found in context " + "for current user.");
		}

		String userName = currentUser.getName();

		Application application = new EntityManagerHelper(em).find(Application.class, "name", applicationName);
		if (application == null) {
			throw new ApplicationNotFoundException(applicationName);
		}

		Membership membership = getMembership(userName, applicationName);
		if (membership == null) {
			throw new MembershipNotFoundException(userName, applicationName);
		}

		if (membership.isLockedOut()) {
			throw new LockedException(
					String.format("The user '%s' has been locked in the application '%s'.", userName, applicationName));
		}

		if (!membership.isEnabled()) {
			throw new DisabledException(String.format("The user '%s' has been disabled in the application '%s'.",
					userName, applicationName));
		}

		String hashedPasswordQuestionAnswer = passwordEncoder.encode(passwordQuestionAnswer.toUpperCase());

		updatePasswordQuestionAnswerAttempt(membership, hashedPasswordQuestionAnswer);

		if (!passwordEncoder.matches(passwordQuestionAnswer.toUpperCase(), membership.getPasswordQuestionAnswer())) {
			throw new WrongPasswordQuestionAnswerException(userName, applicationName);
		}

		// Since we could get a membership for this user name and this
		// application, it means the application's password
		// policies can be used to reset the password.
		PasswordPolicy passwordPolicy = application.getPasswordPolicy();

		PasswordGenerator passwordGenerator = new PasswordGenerator(passwordPolicy.getMinimumPasswordLength(),
				passwordPolicy.getMaximumPasswordLength(), passwordPolicy.getMinimumSpecialCharacters(),
				passwordPolicy.getMinimumLowercaseCharacters(), passwordPolicy.getMinimumUppercaseCharacters(),
				passwordPolicy.getMinimumNumeric());
		String newPassword = passwordGenerator.generate();

		setPassword(membership, newPassword, new Date(), getPasswordHistories(userName, applicationName),
				passwordPolicy.getPasswordHistoryLength());

		SecurityContextHolder.getContext().setAuthentication(createNewAuthentication(currentUser, newPassword));

		return newPassword;
	}

	private void updatePasswordQuestionAnswerAttempt(Membership membership, String passwordQuestionAnswer) {
		PasswordPolicy passwordPolicySettings = membership.getApplication().getPasswordPolicy();
		String persistentPasswordQuestionAnswer = membership.getPasswordQuestionAnswer();
		Date now = new Date();
		if (!passwordQuestionAnswer.equals(persistentPasswordQuestionAnswer)) {
			Calendar c = Calendar.getInstance();
			c.setTime(membership.getFailedPasswordAnswerAttemptWindowStart());
			c.add(Calendar.MINUTE, passwordPolicySettings.getPasswordAttemptWindow());
			if (now.getTime() > c.getTimeInMillis()) {
				membership.setFailedPasswordAnswerAttemptWindowStart(now);
				membership.setFailedPasswordQuestionAnswerAttemptCount(1);
			} else {
				membership.setFailedPasswordQuestionAnswerAttemptCount(
						membership.getFailedPasswordQuestionAnswerAttemptCount() + 1);
			}
			if (membership.getFailedPasswordQuestionAnswerAttemptCount() >= passwordPolicySettings
					.getMaximumInvalidPasswordAttempts()) {
				membership.setLockedOut(true);
				membership.setLastLockoutDate(now);
			}
		} else if (membership.getFailedPasswordQuestionAnswerAttemptCount() > 0) {
			membership.setFailedPasswordQuestionAnswerAttemptCount(0);
			membership.setFailedPasswordAnswerAttemptWindowStart(FIRST_JAN_1754);
		}
		User user = membership.getUser();
		user.setLastActivityDate(now);
		em.merge(membership);
		em.merge(user);
	}

	@Override
	public void changePasswordQuestionAndAnswer(String password, String newPasswordQuestion,
			String newPasswordQuestionAnswer) throws AuthenticationException {

		if (password == null) {
			throw new IllegalArgumentException("The password argument cannot be null.");
		}
		if (newPasswordQuestion == null) {
			throw new IllegalArgumentException("The newPasswordQuestion argument cannot be null.");
		}
		if (newPasswordQuestionAnswer == null) {
			throw new IllegalArgumentException("The newPasswordQuestionAnswer argument cannot be null.");
		}
		if (!new EntityManagerHelper(em).exists(Application.class, "name", applicationName)) {
			throw new ApplicationNotFoundException(applicationName);
		}

		Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();

		if (currentUser == null) {
			// This would indicate bad coding somewhere
			throw new AccessDeniedException(
					"Can't change password question and answer as no Authentication object found in context "
							+ "for current user.");
		}

		String userName = currentUser.getName();

		authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userName, password));

		Membership membership = getMembership(userName, applicationName);

		membership.setPasswordQuestion(newPasswordQuestion);
		membership.setPasswordQuestionAnswer(passwordEncoder.encode(newPasswordQuestionAnswer.toUpperCase()));
		User user = membership.getUser();
		user.setLastActivityDate(new Date());
		em.merge(membership);
		em.merge(user);
	}

	@Override
	public void unlock(String userName) throws ApplicationNotFoundException {
		if (userName == null) {
			throw new IllegalArgumentException("The userName argument cannot be null.");
		}

		if (!new EntityManagerHelper(em).exists(Application.class, "name", applicationName)) {
			throw new ApplicationNotFoundException(applicationName);
		}

		Membership membership = getMembership(userName, applicationName);

		if (membership == null) {
			throw new MembershipNotFoundException(userName, applicationName);
		}

		membership.setFailedPasswordAnswerAttemptWindowStart(FIRST_JAN_1754);
		membership.setFailedPasswordAttemptCount(0);
		membership.setFailedPasswordAttemptWindowStart(FIRST_JAN_1754);
		membership.setFailedPasswordQuestionAnswerAttemptCount(0);
		membership.setLockedOut(false);

		em.merge(membership);
	}

	@Override
	public void delete(String userName) throws AuthenticationException, ApplicationNotFoundException {
		if (userName == null) {
			throw new IllegalArgumentException("The userName argument cannot be null.");
		}
		if (applicationName == null) {
			throw new IllegalArgumentException("The applicationName argument cannot be null.");
		}
		EntityManagerHelper helper = new EntityManagerHelper(em);
		if (!helper.exists(Application.class, "name", applicationName)) {
			throw new ApplicationNotFoundException(applicationName);
		}

		User user = getUser(userName, applicationName, helper);

		em.remove(user);
	}

	private boolean userExists(String userName, String applicationName, EntityManagerHelper helper) {
		Map<String, Object> pathValuePairs = new HashMap<>();
		pathValuePairs.put("userName", userName);
		pathValuePairs.put("application.name", applicationName);
		return helper.exists(User.class, pathValuePairs);
	}

	private User getUser(String userName, String applicationName, EntityManagerHelper helper) {
		Map<String, Object> pathValuePairs = new HashMap<>();
		pathValuePairs.put("userName", userName);
		pathValuePairs.put("application.name", applicationName);
		User user = helper.find(User.class, pathValuePairs);

		if (user == null) {
			throw constructUsernameNotFoundException(userName);
		}
		return user;
	}

	private UsernameNotFoundException constructUsernameNotFoundException(String userName) {
		return new UsernameNotFoundException(String.format(
				"No user with user name '%s' was found in the application with name '%s'.", userName, applicationName));
	}

	@Override
	public boolean passwordExpired(String userName) throws AuthenticationException {
		if (userName == null) {
			throw new IllegalArgumentException("The userName argument cannot be null.");
		}
		if (!userExists(userName, applicationName, new EntityManagerHelper(em))) {
			throw constructUsernameNotFoundException(userName);
		}
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createTupleQuery();
		Root<Membership> rt = cq.from(Membership.class);
		cq.select(cb.tuple(rt.get("lastPasswordChangeDate"), rt.get("application"), rt.get("creationDate")));
		Predicate p1 = cb.equal(cb.upper(rt.join("user").get("userName")), userName.toUpperCase());
		Predicate p2 = cb.equal(cb.upper(rt.join("application").get("name")), applicationName.toUpperCase());
		cq.where(cb.and(p1, p2));
		TypedQuery<Tuple> tq = em.createQuery(cq);
		List<Tuple> tuples = tq.getResultList();
		if (!tuples.isEmpty()) {
			Tuple tuple = tuples.get(0);
			Application application = (Application) tuple.get(1);
			if (application != null) {
				PasswordPolicy passwordPolicy = application.getPasswordPolicy();
				if (passwordPolicy != null) {
					Date lastPasswordChangeDate = (Date) tuple.get(0);
					Date creationDate = (Date) tuple.get(2);
					Integer maximumPasswordAge = passwordPolicy.getMaximumPasswordAge();
					Integer minimumPasswordAge = passwordPolicy.getMinimumPasswordAge();
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
			}
		}
		return false;
	}
}
