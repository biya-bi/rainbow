package org.rainbow.security.orm.audit;

import java.util.Objects;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.rainbow.orm.audit.AbstractAuditableEntityAudit;
import org.rainbow.orm.audit.WriteOperation;
import org.rainbow.security.orm.entities.RecoveryInformation;

/**
 *
 * @author Biya-Bi
 */
@Entity
@Table(name = "RECOVERY_INFORMATION_AUDIT")
public class RecoveryInformationAudit extends AbstractAuditableEntityAudit<RecoveryInformation, Long> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5232513892144484604L;
	private Long membershipId;
	private String question;
	private String answer;
	private boolean encrypted;

	public RecoveryInformationAudit() {
	}

	public RecoveryInformationAudit(RecoveryInformation recoveryInformation, WriteOperation writeOperation) {
		super(recoveryInformation, writeOperation);
		Objects.requireNonNull(recoveryInformation.getMembership());
		this.membershipId = recoveryInformation.getMembership().getId();
		this.question = recoveryInformation.getQuestion();
		this.answer = recoveryInformation.getAnswer();
		this.encrypted = recoveryInformation.isEncrypted();
	}

	@NotNull
	@Column(name = "MEMBERSHIP_ID", nullable = false)
	public Long getMembershipId() {
		return membershipId;
	}

	public void setMembershipId(Long membershipId) {
		this.membershipId = membershipId;
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
