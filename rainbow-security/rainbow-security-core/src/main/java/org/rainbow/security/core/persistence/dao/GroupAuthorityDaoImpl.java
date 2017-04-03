/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.security.core.persistence.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.rainbow.core.persistence.exceptions.NonexistentEntityException;
import org.rainbow.security.core.entities.Application;
import org.rainbow.security.core.entities.Authority;
import org.rainbow.security.core.entities.Group;
import org.rainbow.security.core.entities.User;
import org.rainbow.security.core.persistence.exceptions.AuthorityAlreadyGrantedToGroupException;
import org.rainbow.security.core.persistence.exceptions.AuthorityNotFoundException;
import org.rainbow.security.core.persistence.exceptions.AuthorityNotGrantedToGroupException;
import org.rainbow.security.core.persistence.exceptions.GroupNotFoundException;
import org.rainbow.security.core.utilities.EntityManagerHelper;
import org.rainbow.security.core.utilities.PersistenceSettings;

/**
 *
 * @author Biya-Bi
 */
public class GroupAuthorityDaoImpl implements GroupAuthorityDao {

	@PersistenceContext(unitName = PersistenceSettings.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	private List<Group> getGroups(List<Long> groupIds, Long applicationId) throws GroupNotFoundException {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Group> cq = cb.createQuery(Group.class);
		Root<Group> rt = cq.from(Group.class);
		cq.select(rt);

		Predicate p1 = rt.<Long>get("id").in(groupIds);
		Predicate p2 = cb.equal(rt.join("application").<Long>get("id"), applicationId);
		cq.where(cb.and(p1, p2));
		TypedQuery<Group> tq = em.createQuery(cq);
		List<Group> groups = tq.getResultList();
		for (Long groupId : groupIds) {
			if (!groups.contains(new Group(groupId))) {
				throw new GroupNotFoundException(groupId, applicationId);
			}
		}
		return groups;
	}

	private List<Authority> getAuthorities(List<Long> authorityIds, Long applicationId)
			throws AuthorityNotFoundException {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Authority> cq = cb.createQuery(Authority.class);
		Root<Authority> rt = cq.from(Authority.class);
		cq.select(rt);

		Predicate p1 = rt.<Long>get("id").in(authorityIds);
		Predicate p2 = cb.equal(rt.join("application").<Long>get("id"), applicationId);
		cq.where(cb.and(p1, p2));
		TypedQuery<Authority> tq = em.createQuery(cq);
		List<Authority> authorities = tq.getResultList();
		for (Long authorityId : authorityIds) {
			if (!authorities.contains(new Authority(authorityId))) {
				throw new AuthorityNotFoundException(authorityId, applicationId);
			}
		}
		return authorities;
	}

	@Override
	public void grantAuthoritiesToGroups(List<Long> authorityIds, List<Long> groupIds, Long applicationId)
			throws AuthorityNotFoundException, GroupNotFoundException, AuthorityAlreadyGrantedToGroupException,
			NonexistentEntityException {

		if (!new EntityManagerHelper(em).exists(Application.class, "id", applicationId)) {
			throw new NonexistentEntityException(String.format("No application with ID '%d' exists.", applicationId));
		}

		List<Group> groups = getGroups(groupIds, applicationId);
		List<Authority> authorities = getAuthorities(authorityIds, applicationId);

		List<Group> groupsToBeModified = new ArrayList<>();

		for (Authority authority : authorities) {

			List<Group> authorityGroups = authority.getGroups();
			if (authorityGroups == null) {
				authorityGroups = new ArrayList<>();
				authority.setGroups(authorityGroups);
			}

			for (Group group : groups) {
				List<Authority> groupAuthorities = group.getAuthorities();
				if (groupAuthorities == null) {
					groupAuthorities = new ArrayList<>();
					group.setAuthorities(groupAuthorities);
				}
				if (groupAuthorities.contains(authority)) {
					throw new AuthorityAlreadyGrantedToGroupException(authority.getId(), group.getId(), applicationId);
				}
				groupAuthorities.add(authority);

				if (!authorityGroups.contains(group)) {
					authorityGroups.add(group);
				}
				groupsToBeModified.add(group);
			}

			em.merge(authority);
		}

		for (Group group : groupsToBeModified)
			em.merge(group);
	}

	@Override
	public void revokeAuthoritiesFromGroups(List<Long> authorityIds, List<Long> groupIds, Long applicationId)
			throws GroupNotFoundException, AuthorityNotFoundException, AuthorityNotGrantedToGroupException,
			NonexistentEntityException {

		if (!new EntityManagerHelper(em).exists(Application.class, "id", applicationId)) {
			throw new NonexistentEntityException(String.format("No application with ID '%d' exists.", applicationId));
		}

		List<Group> groups = getGroups(groupIds, applicationId);
		List<Authority> authorities = getAuthorities(authorityIds, applicationId);

		List<Group> groupsToBeModified = new ArrayList<>();

		for (Authority authority : authorities) {

			List<Group> authorityGroups = authority.getGroups();
			if (authorityGroups == null) {
				authorityGroups = new ArrayList<>();
				authority.setGroups(authorityGroups);
			}

			for (Group group : groups) {
				List<Authority> groupAuthorities = group.getAuthorities();
				if (groupAuthorities == null) {
					groupAuthorities = new ArrayList<>();
					group.setAuthorities(groupAuthorities);
				}
				if (!groupAuthorities.contains(authority)) {
					throw new AuthorityNotGrantedToGroupException(authority.getId(), group.getId(), applicationId);
				}
				groupAuthorities.remove(authority);

				if (authorityGroups.contains(group)) {
					authorityGroups.remove(group);
				}

				groupsToBeModified.add(group);
			}

			em.merge(authority);
		}

		for (Group group : groupsToBeModified)
			em.merge(group);
	}

	@Override
	public boolean hasAuthorities(String userName, String applicationName, String... authorityNames) {
		List<String> upperAuthorityNames = new ArrayList<>();
		for (String authorityName : authorityNames) {
			if (!upperAuthorityNames.contains(authorityName.toUpperCase()))
				upperAuthorityNames.add(authorityName.toUpperCase());
		}
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<User> rt = cq.from(User.class);
		cq.select(cb.count(rt));
		Predicate p1 = cb.equal(cb.upper(rt.get("userName")), userName.toUpperCase());
		Predicate p2 = cb.equal(cb.upper(rt.join("application").get("name")), applicationName.toUpperCase());
		Predicate p3 = cb.upper(rt.join("groups").join("authorities").get("name")).in(upperAuthorityNames);
		cq.where(cb.and(p1, p2, p3));
		TypedQuery<Long> tq = em.createQuery(cq);
		Long count = tq.getSingleResult();
		return count > 0 && count >= upperAuthorityNames.size();
	}

	@Override
	public boolean hasAuthority(String userName, String applicationName, String authorityName) {
		return hasAuthorities(userName, applicationName, new String[] { authorityName });
	}

	@Override
	public boolean isInGroups(String userName, String applicationName, String... groupNames) {
		List<String> upperGroupNames = new ArrayList<>();
		for (String groupName : groupNames) {
			upperGroupNames.add(groupName.toUpperCase());
		}
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<User> rt = cq.from(User.class);
		cq.select(cb.count(rt));
		Predicate p1 = cb.equal(cb.upper(rt.get("userName")), userName.toUpperCase());
		Predicate p2 = cb.equal(cb.upper(rt.join("application").get("name")), applicationName.toUpperCase());
		Predicate p3 = cb.upper(rt.join("groups").get("name")).in(upperGroupNames);
		cq.where(cb.and(p1, p2, p3));
		TypedQuery<Long> tq = em.createQuery(cq);
		Long count = tq.getSingleResult();
		return count > 0 && count >= upperGroupNames.size();
	}

	@Override
	public boolean isInGroup(String userName, String applicationName, String groupName) {
		return isInGroups(userName, applicationName, new String[] { groupName });
	}
}
