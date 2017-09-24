package org.rainbow.security.orm.entities;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.rainbow.orm.entities.Trackable;

@Entity
@Table(name = "RECOVERY_INFORMATION", uniqueConstraints = @UniqueConstraint(columnNames = { "USER_ID",
		"QUESTION" }))
public class RecoveryInformation extends Trackable<Long> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8720981222794699494L;
	private Membership membership;
	private String question;
	private String answer;
	private boolean encrypted;

	public RecoveryInformation() {
		super();
	}

	public RecoveryInformation(Long id) {
		super(id);
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Override
	public Long getId() {
		return super.getId();
	}

	@Override
	public void setId(Long id) {
		super.setId(id);
	}

	@NotNull
	@JoinColumn(name = "USER_ID", referencedColumnName = "USER_ID", nullable = false)
	@ManyToOne(optional = false)
	public Membership getMembership() {
		return membership;
	}

	public void setMembership(Membership membership) {
		this.membership = membership;
	}

	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 255)
	@Column(nullable = false)
	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 255)
	@Column(nullable = false)
	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public boolean isEncrypted() {
		return encrypted;
	}

	public void setEncrypted(boolean encrypted) {
		this.encrypted = encrypted;
	}

}
