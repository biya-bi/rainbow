package org.rainbow.security.persistence.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.rainbow.persistence.DaoImpl;
import org.rainbow.persistence.Pageable;
import org.rainbow.security.orm.entities.Application;
import org.rainbow.security.orm.entities.Authority;
import org.rainbow.security.orm.entities.Group;
import org.rainbow.security.orm.entities.User;
import org.rainbow.security.utilities.PersistenceSettings;
import org.rainbow.utilities.EntityManagerUtil;

/**
 *
 * @author Biya-Bi
 */
@Pageable(attributeName = "id")
public class GroupDaoImpl extends DaoImpl<Group, Long> {

	@PersistenceContext(unitName = PersistenceSettings.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public GroupDaoImpl() {
		super(Group.class);
	}

	@Override
	protected void onCreate(Group group) throws Exception {
		super.onCreate(group);
		fixAssociations(group);
	}

	@Override
	protected void onUpdate(Group group) throws Exception {
		super.onUpdate(group);
		fixAssociations(group);
	}

	private void fixAssociations(Group group) {
		group.setApplication(
				EntityManagerUtil.find(this.getEntityManager(), Application.class, group.getApplication()));

		fixUsers(group);
		fixAuthorities(group);
	}

	private void fixUsers(Group group) {
		List<User> oldUsers = getUsersForGroup(group.getId());
		List<User> groupUsers = group.getUsers();
		if (groupUsers != null) {
			List<User> newUsers = getUsers(groupUsers.stream().map(x -> x.getId()).collect(Collectors.toList()));
			for (User user : oldUsers) {
				// If the user has been removed from the group's users, we also
				// remove the group from the user's groups.
				if (!newUsers.contains(user)) {
					List<Group> userGroups = user.getGroups();
					if (userGroups != null) {
						userGroups.remove(group);
						this.getEntityManager().merge(user);
					}
				}
				// If the old user still appears in the new users list, then we
				// have
				// nothing to do because the user is already in the group.
			}
			for (User user : newUsers) {
				List<Group> userGroups = user.getGroups();
				if (userGroups == null) {
					userGroups = new ArrayList<>();
					user.setGroups(userGroups);
				}
				if (!userGroups.contains(group)) {
					userGroups.add(group);
					this.getEntityManager().merge(user);
				}
			}
		} else {
			for (User user : oldUsers) {
				List<Group> userGroups = user.getGroups();
				if (userGroups != null) {
					if (userGroups.contains(group)) {
						userGroups.remove(group);
						this.getEntityManager().merge(user);
					}
				}
			}
		}
	}

	private List<User> getUsers(List<Long> userIds) {
		if (userIds.isEmpty()) {
			return Collections.emptyList();
		}
		CriteriaBuilder cb = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<User> cq = cb.createQuery(User.class);
		Root<User> rt = cq.from(User.class);
		cq.where(rt.get("id").in(userIds));
		return this.getEntityManager().createQuery(cq).getResultList();
	}

	private List<User> getUsersForGroup(Long groupId) {
		CriteriaBuilder cb = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<User> cq = cb.createQuery(User.class);
		Root<User> rt = cq.from(User.class);
		cq.where(cb.equal(rt.join("groups").get("id"), groupId));
		return this.getEntityManager().createQuery(cq).getResultList();
	}

	private void fixAuthorities(Group group) {
		List<Authority> oldAuthorities = getAuthoritiesForGroup(group.getId());
		List<Authority> groupAuthorities = group.getAuthorities();
		if (groupAuthorities != null) {
			List<Authority> newAuthorities = getAuthorities(
					groupAuthorities.stream().map(x -> x.getId()).collect(Collectors.toList()));
			for (Authority authority : oldAuthorities) {
				// If the authority has been removed from the group's
				// authorities, we also
				// remove the group from the authority's groups.
				if (!newAuthorities.contains(authority)) {
					List<Group> authorityGroups = authority.getGroups();
					if (authorityGroups != null) {
						authorityGroups.remove(group);
						this.getEntityManager().merge(authority);
					}
				}
				// If the old authority still appears in the new authorities
				// list, then we
				// have
				// nothing to do because the authority is already in the group.
			}
			for (Authority authority : newAuthorities) {
				List<Group> authorityGroups = authority.getGroups();
				if (authorityGroups == null) {
					authorityGroups = new ArrayList<>();
					authority.setGroups(authorityGroups);
				}
				if (!authorityGroups.contains(group)) {
					authorityGroups.add(group);
					this.getEntityManager().merge(authority);
				}
			}
		} else {
			for (Authority authority : oldAuthorities) {
				List<Group> authorityGroups = authority.getGroups();
				if (authorityGroups != null) {
					if (authorityGroups.contains(group)) {
						authorityGroups.remove(group);
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

	private List<Authority> getAuthoritiesForGroup(Long groupId) {
		CriteriaBuilder cb = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Authority> cq = cb.createQuery(Authority.class);
		Root<Authority> rt = cq.from(Authority.class);
		cq.where(cb.equal(rt.join("groups").get("id"), groupId));
		return this.getEntityManager().createQuery(cq).getResultList();
	}

}
