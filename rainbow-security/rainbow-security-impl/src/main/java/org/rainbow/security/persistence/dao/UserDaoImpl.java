package org.rainbow.security.persistence.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;

import org.rainbow.persistence.dao.DaoImpl;
import org.rainbow.persistence.dao.Pageable;
import org.rainbow.security.orm.entities.Application;
import org.rainbow.security.orm.entities.Authority;
import org.rainbow.security.orm.entities.Group;
import org.rainbow.security.orm.entities.Membership;
import org.rainbow.security.orm.entities.PasswordHistory;
import org.rainbow.security.orm.entities.RecoveryInformation;
import org.rainbow.security.orm.entities.User;
import org.rainbow.security.utilities.DateUtil;
import org.rainbow.security.utilities.PersistenceSettings;
import org.rainbow.utilities.EntityManagerUtil;
import org.springframework.security.crypto.password.PasswordEncoder;

@Pageable(attributeName = "id")
public class UserDaoImpl extends DaoImpl<User> implements UserDao {

	@PersistenceContext(unitName = PersistenceSettings.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	private PasswordEncoder passwordEncoder;

	private final static Date FIRST_JAN_1754 = DateUtil.toDate("1754-01-01");

	public UserDaoImpl() {
		super(User.class);
	}

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public PasswordEncoder getPasswordEncoder() {
		return passwordEncoder;
	}

	public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	protected void onCreate(User user) throws Exception {
		super.onCreate(user);
		fixAssociations(user);
		Membership membership = user.getMembership();
		if (membership != null && !user.equals(membership.getUser())) {
			membership.setUser(user);
		}

		Application persistentApplication = EntityManagerUtil.find(this.getEntityManager(), Application.class,
				user.getApplication());

		user.setApplication(persistentApplication);

		user.setLastActivityDate(FIRST_JAN_1754);

		// Membership membership = user.getMembership();
		// It can happen that the membership of the user is null. This can occur
		// particularly if we want an external system such as Active Directory
		// to perform the authentication.
		// In this case, it should still be possible to create a user without a
		// membership.
		if (membership != null) {
			Date now = new Date();

			membership.setCreationDate(now);
			membership.setFailedRecoveryAttemptsWindowStart(FIRST_JAN_1754);
			membership.setFailedPasswordAttemptWindowStart(FIRST_JAN_1754);
			if (membership.isLocked()) {
				membership.setLastLockoutDate(now);
			} else {
				membership.setLastLockoutDate(FIRST_JAN_1754);
			}
			membership.setLastPasswordChangeDate(FIRST_JAN_1754);

			membership.setPassword(getPasswordEncoder().encode(membership.getPassword()));
			membership.setEncrypted(true);

			final List<RecoveryInformation> ri = membership.getRecoveryInformation();
			if (ri != null && !ri.isEmpty()) {
				for (RecoveryInformation recoveryInformation : ri) {
					final String answer = recoveryInformation.getAnswer();
					if (answer != null) {
						recoveryInformation.setAnswer(getPasswordEncoder().encode(answer.toUpperCase()));
						recoveryInformation.setEncrypted(true);
					}
				}
			}
			List<PasswordHistory> passwordHistories = new ArrayList<>();
			PasswordHistory passwordHistory = new PasswordHistory();
			passwordHistory.setChangeDate(now);
			passwordHistory.setMembership(membership);
			passwordHistory.setPassword(membership.getPassword());
			passwordHistories.add(passwordHistory);

			membership.setPasswordHistories(passwordHistories);

			this.getEntityManager().persist(passwordHistory);

		}
	}

	private List<RecoveryInformation> getOldRecoveryInformation(User user) {
		CriteriaBuilder cb = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<RecoveryInformation> cq = cb.createQuery(RecoveryInformation.class);
		Root<RecoveryInformation> rt = cq.from(RecoveryInformation.class);

		Expression<Boolean> exp = cb.equal(rt.join("membership").join("user").get("id"), user.getId());

		cq = cq.select(rt).where(exp);

		TypedQuery<RecoveryInformation> query = this.getEntityManager().createQuery(cq);

		return query.getResultList();
	}

	@Override
	protected void onUpdate(User user) throws Exception {
		Membership membership = user.getMembership();
		if (membership != null) {
			List<RecoveryInformation> newRecoveryInformation = membership.getRecoveryInformation();
			List<RecoveryInformation> oldRecoveryInformation = getOldRecoveryInformation(user);
			if (newRecoveryInformation != null && !newRecoveryInformation.isEmpty()) {
				List<RecoveryInformation> createdRecoveryInformation = newRecoveryInformation.stream()
						.filter(x -> x.getId() == null || !oldRecoveryInformation.contains(x))
						.collect(Collectors.toList());

				List<RecoveryInformation> updatedRecoveryInformation = newRecoveryInformation.stream()
						.filter(x -> x.getId() != null && oldRecoveryInformation.contains(x))
						.collect(Collectors.toList());

				List<RecoveryInformation> deletedRecoveryInformation = oldRecoveryInformation.stream()
						.filter(x -> !newRecoveryInformation.contains(x)).collect(Collectors.toList());

				List<String> newQuestions = createdRecoveryInformation.stream().map(x -> x.getQuestion())
						.collect(Collectors.toList());

				deletedRecoveryInformation.stream().forEach(x -> this.getEntityManager().remove(x));
				// If there are any old questions that appears in the new
				// questions, we first flush the entity manager so as to avoid
				// duplicate exception while persisting new questions.
				if (deletedRecoveryInformation.stream().filter(x -> newQuestions.contains(x.getQuestion())).findFirst()
						.isPresent()) {
					this.getEntityManager().flush();
				}
				createdRecoveryInformation.stream().forEach(x -> this.getEntityManager().persist(x));
				updatedRecoveryInformation.stream().forEach(x -> this.getEntityManager().merge(x));
			} else {
				oldRecoveryInformation.stream().forEach(x -> this.getEntityManager().remove(x));
			}
		}
		super.onUpdate(user);
		fixAssociations(user);
	}

	private void fixAssociations(User user) {
		user.setApplication(EntityManagerUtil.find(this.getEntityManager(), Application.class, user.getApplication()));
		
		fixGroups(user);
		fixAuthorities(user);
	}

	private void fixGroups(User user) {
		List<Group> oldGroups = getGroupsForUser(user.getId());
		List<Group> userGroups = user.getGroups();
		if (userGroups != null) {
			List<Group> newGroups = getGroups(userGroups.stream().map(x -> x.getId()).collect(Collectors.toList()));
			for (Group group : oldGroups) {
				// If the group has been removed from the user's group, we also
				// remove the user from the group's users.
				if (!newGroups.contains(group)) {
					List<User> groupUsers = group.getUsers();
					if (groupUsers != null) {
						groupUsers.remove(user);
						this.getEntityManager().merge(group);
					}
				}
				// If the old group still appears in the new groups list, then
				// we have nothing to do because the user is already in the
				// group.
			}
			for (Group group : newGroups) {
				List<User> groupUsers = group.getUsers();
				if (groupUsers == null) {
					groupUsers = new ArrayList<>();
					group.setUsers(groupUsers);
				}
				if (!groupUsers.contains(user)) {
					groupUsers.add(user);
					this.getEntityManager().merge(group);
				}
			}
		} else {
			for (Group group : oldGroups) {
				List<User> groupUsers = group.getUsers();
				if (groupUsers != null) {
					if (groupUsers.contains(user)) {
						groupUsers.remove(user);
						this.getEntityManager().merge(group);
					}
				}
			}
		}
	}

	private List<Group> getGroups(List<Long> groupIds) {
		if (groupIds.isEmpty()) {
			return Collections.emptyList();
		}
		CriteriaBuilder cb = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Group> cq = cb.createQuery(Group.class);
		Root<Group> rt = cq.from(Group.class);
		cq.where(rt.get("id").in(groupIds));
		return this.getEntityManager().createQuery(cq).getResultList();
	}

	private List<Group> getGroupsForUser(Long userId) {
		CriteriaBuilder cb = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Group> cq = cb.createQuery(Group.class);
		Root<Group> rt = cq.from(Group.class);
		cq.where(cb.equal(rt.join("users").get("id"), userId));
		return this.getEntityManager().createQuery(cq).getResultList();
	}

	private void fixAuthorities(User user) {
		List<Authority> oldAuthorities = getAuthoritiesForUser(user.getId());
		List<Authority> userAuthorities = user.getAuthorities();
		if (userAuthorities != null) {
			List<Authority> newAuthorities = getAuthorities(
					userAuthorities.stream().map(x -> x.getId()).collect(Collectors.toList()));
			for (Authority authority : oldAuthorities) {
				// If the authority has been removed from the user's
				// authorities, we also
				// remove the user from the authority's users.
				if (!newAuthorities.contains(authority)) {
					List<User> authorityUsers = authority.getUsers();
					if (authorityUsers != null) {
						authorityUsers.remove(user);
						this.getEntityManager().merge(authority);
					}
				}
				// If the old authority still appears in the new authorities
				// list, then we
				// have
				// nothing to do because the authority is already in the user.
			}
			for (Authority authority : newAuthorities) {
				List<User> authorityUsers = authority.getUsers();
				if (authorityUsers == null) {
					authorityUsers = new ArrayList<>();
					authority.setUsers(authorityUsers);
				}
				if (!authorityUsers.contains(user)) {
					authorityUsers.add(user);
					this.getEntityManager().merge(authority);
				}
			}
		} else {
			for (Authority authority : oldAuthorities) {
				List<User> authorityUsers = authority.getUsers();
				if (authorityUsers != null) {
					if (authorityUsers.contains(user)) {
						authorityUsers.remove(user);
						this.getEntityManager().merge(authority);
					}
				}
			}
		}
	}

	private List<Authority> getAuthorities(List<Long> authorityIds) {
		if (authorityIds.isEmpty()) {
			return Collections.emptyList();
		}
		CriteriaBuilder cb = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Authority> cq = cb.createQuery(Authority.class);
		Root<Authority> rt = cq.from(Authority.class);
		cq.where(rt.get("id").in(authorityIds));
		return this.getEntityManager().createQuery(cq).getResultList();
	}

	private List<Authority> getAuthoritiesForUser(Long userId) {
		CriteriaBuilder cb = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Authority> cq = cb.createQuery(Authority.class);
		Root<Authority> rt = cq.from(Authority.class);
		cq.where(cb.equal(rt.join("users").get("id"), userId));
		return this.getEntityManager().createQuery(cq).getResultList();
	}

}
