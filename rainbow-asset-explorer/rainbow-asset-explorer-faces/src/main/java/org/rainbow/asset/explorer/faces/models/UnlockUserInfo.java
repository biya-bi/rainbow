/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.asset.explorer.faces.models;

import java.io.Serializable;

/**
 *
 * @author Biya-Bi
 */
public class UnlockUserInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8072991420137454940L;
	private String userName;
	private String question;
	private String answer;

	public UnlockUserInfo() {
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}
}
