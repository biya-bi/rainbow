package org.rainbow.security.orm.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Biya-Bi
 */
@Entity
@Table(name = "PASSWORD_POLICIES")
public class PasswordPolicy extends AccountPolicy {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6898206236783268070L;
	private Application application;
	private Short minAge = 0;
	private Short maxAge = 30;
	private Short minLength = 1;
	private Short maxLength = 128;
	private Short minSpecialCharsCount = 0;
	private Short historyThreshold = 10;
	private Short minLowercaseCharsCount = 0;
	private Short minUppercaseCharsCount = 0;
	private Short minNumericCount = 0;

	public PasswordPolicy() {
	}

	@Id
	@OneToOne
	@JoinColumn(name = "APPLICATION_ID")
	public Application getApplication() {
		return application;
	}

	public void setApplication(Application application) {
		this.application = application;
	}

	@NotNull
	@Min(0)
	@Max(998)
	@Column(name = "MIN_AGE", nullable = false)
	public Short getMinAge() {
		return minAge;
	}

	public void setMinAge(Short minAge) {
		this.minAge = minAge;
	}

	@Min(0)
	@Max(999)
	@NotNull
	@Column(name = "MAX_AGE", nullable = false)
	public Short getMaxAge() {
		return maxAge;
	}

	public void setMaxAge(Short maxAge) {
		this.maxAge = maxAge;
	}

	@Min(1)
	@Max(128)
	@NotNull
	@Column(name = "MIN_LENGTH", nullable = false)
	public Short getMinLength() {
		return minLength;
	}

	public void setMinLength(Short minLength) {
		this.minLength = minLength;
	}

	@Min(1)
	@Max(128)
	@NotNull
	@Column(name = "MAX_LENGTH", nullable = false)
	public Short getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(Short maxLength) {
		this.maxLength = maxLength;
	}

	@Min(0)
	@Max(128)
	@NotNull
	@Column(name = "MIN_SPECIAL_CHARS_COUNT", nullable = false)
	public Short getMinSpecialCharsCount() {
		return minSpecialCharsCount;
	}

	public void setMinSpecialCharsCount(Short minSpecialCharsCount) {
		this.minSpecialCharsCount = minSpecialCharsCount;
	}

	@Min(0)
	@Max(48)
	@NotNull
	@Column(name = "HISTORY_THRESHOLD", nullable = false)
	public Short getHistoryThreshold() {
		return historyThreshold;
	}

	public void setHistoryThreshold(Short historyThreshold) {
		this.historyThreshold = historyThreshold;
	}

	@Min(0)
	@Max(128)
	@NotNull
	@Column(name = "MIN_LOWERCASE_CHARS_COUNT", nullable = false)
	public Short getMinLowercaseCharsCount() {
		return minLowercaseCharsCount;
	}

	public void setMinLowercaseCharsCount(Short minLowercaseCharsCount) {
		this.minLowercaseCharsCount = minLowercaseCharsCount;
	}

	@Min(0)
	@Max(128)
	@NotNull
	@Column(name = "MIN_UPPERCASE_CHARS_COUNT", nullable = false)
	public Short getMinUppercaseCharsCount() {
		return minUppercaseCharsCount;
	}

	public void setMinUppercaseCharsCount(Short minUppercaseCharsCount) {
		this.minUppercaseCharsCount = minUppercaseCharsCount;
	}

	@Min(0)
	@Max(128)
	@NotNull
	@Column(name = "MIN_NUMERIC_COUNT", nullable = false)
	public Short getMinNumericCount() {
		return minNumericCount;
	}

	public void setMinNumericCount(Short minNumericCount) {
		this.minNumericCount = minNumericCount;
	}

}
