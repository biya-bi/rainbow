package org.rainbow.shopping.cart.ui.web.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

import org.rainbow.service.api.IService;
import org.rainbow.shopping.cart.model.User;
import org.springframework.dao.DataAccessException;

@ManagedBean(name = "userMB")
@RequestScoped
public class UserManagedBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -554779256998644101L;
	private static final String SUCCESS = "success";
	private static final String ERROR = "error";

	// Spring User Service is injected...
	@ManagedProperty(value = "#{userService}")
	private IService<User> userService;

	private List<User> userList;

	private int id;
	private String name;
	private String surname;

	/**
	 * Add User
	 *
	 * @return String - Response Message
	 * @throws Exception 
	 */
	public String addUser() throws Exception {
		try {
			User user = new User();
			user.setId(getId());
			user.setName(getName());
			user.setSurname(getSurname());
			getUserService().create(user);
			return SUCCESS;
		} catch (DataAccessException e) {
			e.printStackTrace();
		}

		return ERROR;
	}

	/**
	 * Reset Fields
	 *
	 */
	public void reset() {
		this.setId(0);
		this.setName("");
		this.setSurname("");
	}

	/**
	 * Get User List
	 *
	 * @return List - User List
	 * @throws Exception 
	 */
	public List<User> getUserList() throws Exception {
		userList = new ArrayList<User>();
		userList.addAll(getUserService().findAll());
		return userList;
	}

	/**
	 * Get User Service
	 *
	 * @return IUserService - User Service
	 */
	public IService<User> getUserService() {
		return userService;
	}

	/**
	 * Set User Service
	 *
	 * @param IUserService
	 *            - User Service
	 */
	public void setUserService(IService<User> userService) {
		this.userService = userService;
	}

	/**
	 * Set User List
	 *
	 * @param List
	 *            - User List
	 */
	public void setUserList(List<User> userList) {
		this.userList = userList;
	}

	/**
	 * Get User Id
	 *
	 * @return int - User Id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Set User Id
	 *
	 * @param int
	 *            - User Id
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Get User Name
	 *
	 * @return String - User Name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set User Name
	 *
	 * @param String
	 *            - User Name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get User Surname
	 *
	 * @return String - User Surname
	 */
	public String getSurname() {
		return surname;
	}

	/**
	 * Set User Surname
	 *
	 * @param String
	 *            - User Surname
	 */
	public void setSurname(String surname) {
		this.surname = surname;
	}

}