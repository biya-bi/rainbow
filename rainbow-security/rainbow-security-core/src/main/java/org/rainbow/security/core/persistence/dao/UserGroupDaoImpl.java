/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.security.core.persistence.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.rainbow.core.persistence.exceptions.NonexistentEntityException;
import org.rainbow.security.core.entities.Application;
import org.rainbow.security.core.entities.Group;
import org.rainbow.security.core.entities.Membership;
import org.rainbow.security.core.entities.User;
import org.rainbow.security.core.persistence.exceptions.ApplicationNotFoundException;
import org.rainbow.security.core.persistence.exceptions.GroupNotFoundException;
import org.rainbow.security.core.persistence.exceptions.UserAlreadyInGroupException;
import org.rainbow.security.core.persistence.exceptions.UserNotFoundException;
import org.rainbow.security.core.persistence.exceptions.UserNotFoundNameException;
import org.rainbow.security.core.persistence.exceptions.UserNotInGroupException;
import org.rainbow.security.core.utilities.EntityManagerHelper;
import org.rainbow.security.core.utilities.PersistenceSettings;

/**
 *
 * @author Biya-Bi
 */
public class UserGroupDaoImpl implements UserGroupDao {

	@PersistenceContext(unitName = PersistenceSettings.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	private List<User> getUsers(List<Long> userIds, Long applicationId) throws UserNotFoundException {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<User> cq = cb.createQuery(User.class);
		Root<User> rt = cq.from(User.class);
		cq.select(rt);

		Predicate p1 = rt.<Long>get("id").in(userIds);
		Predicate p2 = cb.equal(rt.join("application").<Long>get("id"), applicationId);
		cq.where(cb.and(p1, p2));
		TypedQuery<User> tq = em.createQuery(cq);
		List<User> users = tq.getResultList();
		for (Long userId : userIds) {
			if (!users.contains(new User(userId))) {
				throw new UserNotFoundException(userId, applicationId);
			}
		}
		return users;
	}

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

	@Override
	public void addUsersToGroups(List<Long> userIds, List<Long> groupIds, Long applicationId)
			throws UserNotFoundException, GroupNotFoundException, UserAlreadyInGroupException,
			NonexistentEntityException {

		if (!new EntityManagerHelper(em).exists(Application.class, "id", applicationId)) {
			throw new NonexistentEntityException(String.format("No application with ID %s exists.", applicationId));
		}

		List<User> users = getUsers(userIds, applicationId);
		List<Group> groups = getGroups(groupIds, applicationId);
		
		List<Group> groupsToBeModified = new ArrayList<>();
		
		for (User user : users) {

			List<Group> userGroups = user.getGroups();
			if (userGroups == null) {
				userGroups = new ArrayList<>();
				user.setGroups(userGroups);
			}

			for (Group group : groups) {
				List<User> groupUsers = group.getUsers();
				if (groupUsers == null) {
					groupUsers = new ArrayList<>();
					group.setUsers(groupUsers);
				}
				if (groupUsers.contains(user)) {
					throw new UserAlreadyInGroupException(user.getId(), group.getId(), applicationId);
				}
				groupUsers.add(user);
			
				if (!userGroups.contains(group)) {
					userGroups.add(group);
				}
				
				groupsToBeModified.add(group);
			}

			em.merge(user);
		}
		for (Group group : groupsToBeModified)
			em.merge(group);
	}

	@Override
	public void removeUsersFromGroups(List<Long> userIds, List<Long> groupIds, Long applicationId)
			throws UserNotFoundException, GroupNotFoundException, UserNotInGroupException, NonexistentEntityException {

		if (!new EntityManagerHelper(em).exists(Application.class, "id", applicationId)) {
			throw new NonexistentEntityException(String.format("No application with ID %s exists.", applicationId));
		}
		List<User> users = getUsers(userIds, applicationId);
		List<Group> groups = getGroups(groupIds, applicationId);

		List<Group> groupsToBeModified = new ArrayList<>();

		for (User user : users) {

			List<Group> userGroups = user.getGroups();
			if (userGroups == null) {
				userGroups = new ArrayList<>();
				user.setGroups(userGroups);
			}

			for (Group group : groups) {
				List<User> groupUsers = group.getUsers();
				if (groupUsers == null) {
					groupUsers = new ArrayList<>();
					group.setUsers(groupUsers);
				}
				if (!groupUsers.contains(user)) {
					throw new UserNotInGroupException(user.getId(), group.getId(), applicationId);
				}
				groupUsers.remove(user);

				if (userGroups.contains(group)) {
					userGroups.remove(group);
				}
				
				groupsToBeModified.add(group);
			}

			em.merge(user);
		}

		for (Group group : groupsToBeModified)
			em.merge(group);
	}

	@Override
	public List<String> getGroups(String userName, String applicationName)
			throws UserNotFoundNameException, ApplicationNotFoundException {
		if (userName == null) {
			throw new IllegalArgumentException("The userName argument cannot be null.");
		}
		if (applicationName == null) {
			throw new IllegalArgumentException("The applicationName argument cannot be null.");
		}

		EntityManagerHelper helper = new EntityManagerHelper(em);
		if (!helper.exists(Application.class, "name", applicationName))
			throw new ApplicationNotFoundException(applicationName);
		if (!userExists(userName, applicationName, helper)) {
			throw new UserNotFoundNameException(userName, applicationName);
		}
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createTupleQuery();
		Root<Membership> rt = cq.from(Membership.class);

		cq.select(cb.tuple(rt.join("user").join("groups").get("name")));
		Predicate p1 = cb.equal(cb.upper(rt.join("user").get("userName")), userName.toUpperCase());
		Predicate p2 = cb.equal(cb.upper(rt.join("application").get("name")), applicationName.toUpperCase());
		cq.where(cb.and(p1, p2));
		TypedQuery<Tuple> tq = em.createQuery(cq);
		List<Tuple> tuples = tq.getResultList();
		List<String> groups = new ArrayList<>();
		for (Tuple tuple : tuples) {
			String group = (String) tuple.get(0);
			if (!groups.contains(group)) {
				groups.add(group);
			}
		}
		return groups;
	}

	@Override
	public List<String> getAuthorities(String userName, String applicationName)
			throws UserNotFoundNameException, ApplicationNotFoundException {
		if (userName == null) {
			throw new IllegalArgumentException("The userName argument cannot be null.");
		}
		if (applicationName == null) {
			throw new IllegalArgumentException("The applicationName argument cannot be null.");
		}
		EntityManagerHelper helper = new EntityManagerHelper(em);
		if (!helper.exists(Application.class, "name", applicationName))
			throw new ApplicationNotFoundException(applicationName);
		if (!userExists(userName, applicationName, helper)) {
			throw new UserNotFoundNameException(userName, applicationName);
		}
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createTupleQuery();
		Root<Membership> rt = cq.from(Membership.class);

		cq.select(cb.tuple(rt.join("user").join("groups").join("authorities").get("name")));
		Predicate p1 = cb.equal(cb.upper(rt.join("user").get("userName")), userName.toUpperCase());
		Predicate p2 = cb.equal(cb.upper(rt.join("application").get("name")), applicationName.toUpperCase());
		cq.where(cb.and(p1, p2));
		TypedQuery<Tuple> tq = em.createQuery(cq);
		List<Tuple> tuples = tq.getResultList();
		List<String> authorities = new ArrayList<>();
		for (Tuple tuple : tuples) {
			String authority = (String) tuple.get(0);
			if (!authorities.contains(authority)) {
				authorities.add(authority);
			}
		}
		return authorities;
	}

	private boolean userExists(String userName, String applicationName, EntityManagerHelper helper) {
		Map<String, Object> pathValuePairs = new HashMap<>();
		pathValuePairs.put("userName", userName);
		pathValuePairs.put("application.name", applicationName);
		return helper.exists(User.class, pathValuePairs);
	}

}
