package org.rainbow.journal.server.dto;

import java.io.Serializable;
import java.util.Date;

public class SignupDto implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9118029953557897875L;
	private String userName;
	private String password;
	private String email;
	private String phone;
	private String firstName;
	private String lastName;
	private Date birthDate;
	private String passwordQuestion;
	private String passwordQuestionAnswer;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public String getPasswordQuestion() {
		return passwordQuestion;
	}

	public void setPasswordQuestion(String passwordQuestion) {
		this.passwordQuestion = passwordQuestion;
	}

	public String getPasswordQuestionAnswer() {
		return passwordQuestionAnswer;
	}

	public void setPasswordQuestionAnswer(String passwordQuestionAnswer) {
		this.passwordQuestionAnswer = passwordQuestionAnswer;
	}

}
