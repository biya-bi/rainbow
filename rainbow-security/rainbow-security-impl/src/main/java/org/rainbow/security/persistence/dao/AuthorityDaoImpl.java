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

import org.rainbow.persistence.dao.DaoImpl;
import org.rainbow.persistence.dao.Pageable;
import org.rainbow.security.orm.entities.Application;
import org.rainbow.security.orm.entities.Authority;
import org.rainbow.security.orm.entities.Group;
import org.rainbow.security.orm.entities.User;
import org.rainbow.security.utilities.PersistenceSettings;
import org.rainbow.util.EntityManagerUtil;

/**
 *
 * @author Biya-Bi
 */
@Pageable(attributeName = "id")
public class AuthorityDaoImpl extends DaoImpl<Authority> implements AuthorityDao {

	@PersistenceContext(unitName = PersistenceSettings.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public AuthorityDaoImpl() {
		super(Authority.class);
	}

	@Override
	protected void onCreate(Authority authority) throws Exception {
		super.onCreate(authority);
		fixAssociations(authority);
	}

	@Override
	protected void onUpdate(Authority authority) throws Exception {
		super.onUpdate(authority);
		fixAssociations(authority);
	}

	private void fixAssociations(Authority authority) {
		authority.setApplication(
				EntityManagerUtil.find(this.getEntityManager(), Application.class, authority.getApplication()));
		
		fixGroups(authority);
		fixUsers(authority);
	}

	private void fixGroups(Authority authority) {
		List<Group> oldGroups = getGroupsForAuthority(authority.getId());
		List<Group> authorityGroups = authority.getGroups();
		if (authorityGroups != null) {
			List<Group> newGroups = getGroups(
					authorityGroups.stream().map(x -> x.getId()).collect(Collectors.toList()));
			for (Group group : oldGroups) {
				// If the group has been removed from the authority's groups, we
				// also
				// remove the authority from the group's authorities.
				if (!newGroups.contains(group)) {
					List<Authority> groupAuthorities = group.getAuthorities();
					if (groupAuthorities != null) {
						groupAuthorities.remove(authority);
						this.getEntityManager().merge(group);
					}
				}
				// If the old group still appears in the new groups list, then
				// we have nothing to do because the authority is already
				// granted to the
				// group.
			}
			for (Group group : newGroups) {
				List<Authority> groupAuthorities = group.getAuthorities();
				if (groupAuthorities == null) {
					groupAuthorities = new ArrayList<>();
					group.setAuthorities(groupAuthorities);
				}
				if (!groupAuthorities.contains(authority)) {
					groupAuthorities.add(authority);
					this.getEntityManager().merge(group);
				}
			}
		} else {
			for (Group group : oldGroups) {
				List<Authority> groupAuthorities = group.getAuthorities();
				if (groupAuthorities != null) {
					if (groupAuthorities.contains(authority)) {
						groupAuthorities.remove(authority);
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

	private List<Group> getGroupsForAuthority(Long authorityId) {
		CriteriaBuilder cb = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Group> cq = cb.createQuery(Group.class);
		Root<Group> rt = cq.from(Group.class);
		cq.where(cb.equal(rt.join("authorities").get("id"), authorityId));
		return this.getEntityManager().createQuery(cq).getResultList();
	}

	private void fixUsers(Authority authority) {
		List<User> oldUsers = getUsersForAuthority(authority.getId());
		List<User> authorityUsers = authority.getUsers();
		if (authorityUsers != null) {
			List<User> newUsers = getUsers(authorityUsers.stream().map(x -> x.getId()).collect(Collectors.toList()));
			for (User user : oldUsers) {
				// If the user has been removed from the authority's users, we
				// also
				// remove the authority from the user's authorities.
				if (!newUsers.contains(user)) {
					List<Authority> userAuthorities = user.getAuthorities();
					if (userAuthorities != null) {
						userAuthorities.remove(authority);
						this.getEntityManager().merge(user);
					}
				}
				// If the old user still appears in the new users list, then we
				// have
				// nothing to do because the user is already in the authority.
			}
			for (User user : newUsers) {
				List<Authority> userAuthorities = user.getAuthorities();
				if (userAuthorities == null) {
					userAuthorities = new ArrayList<>();
					user.setAuthorities(userAuthorities);
				}
				if (!userAuthorities.contains(authority)) {
					userAuthorities.add(authority);
					this.getEntityManager().merge(user);
				}
			}
		} else {
			for (User user : oldUsers) {
				List<Authority> userAuthorities = user.getAuthorities();
				if (userAuthorities != null) {
					if (userAuthorities.contains(authority)) {
						userAuthorities.remove(authority);
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

	private List<User> getUsersForAuthority(Long authorityId) {
		CriteriaBuilder cb = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<User> cq = cb.createQuery(User.class);
		Root<User> rt = cq.from(User.class);
		cq.where(cb.equal(rt.join("authorities").get("id"), authorityId));
		return this.getEntityManager().createQuery(cq).getResultList();
	}

}
