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
	private Integer minAge = 0;
	private Integer maxAge = 30;
	private Integer minLength = 1;
	private Integer maxLength = 128;
	private Integer minSpecialCharsCount = 0;
	private Integer historyThreshold = 10;
	private Integer minLowercaseCharsCount = 0;
	private Integer minUppercaseCharsCount = 0;
	private Integer minNumericCount = 0;

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
	public Integer getMinAge() {
		return minAge;
	}

	public void setMinAge(Integer minAge) {
		this.minAge = minAge;
	}

	@Min(0)
	@Max(999)
	@NotNull
	@Column(name = "MAX_AGE", nullable = false)
	public Integer getMaxAge() {
		return maxAge;
	}

	public void setMaxAge(Integer maxAge) {
		this.maxAge = maxAge;
	}

	@Min(1)
	@Max(128)
	@NotNull
	@Column(name = "MIN_LENGTH", nullable = false)
	public Integer getMinLength() {
		return minLength;
	}

	public void setMinLength(Integer minLength) {
		this.minLength = minLength;
	}

	@Min(1)
	@Max(128)
	@NotNull
	@Column(name = "MAX_LENGTH", nullable = false)
	public Integer getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(Integer maxLength) {
		this.maxLength = maxLength;
	}

	@Min(0)
	@Max(128)
	@NotNull
	@Column(name = "MIN_SPECIAL_CHARS_COUNT", nullable = false)
	public Integer getMinSpecialCharsCount() {
		return minSpecialCharsCount;
	}

	public void setMinSpecialCharsCount(Integer minSpecialCharsCount) {
		this.minSpecialCharsCount = minSpecialCharsCount;
	}

	@Min(0)
	@Max(48)
	@NotNull
	@Column(name = "HISTORY_THRESHOLD", nullable = false)
	public Integer getHistoryThreshold() {
		return historyThreshold;
	}

	public void setHistoryThreshold(Integer historyThreshold) {
		this.historyThreshold = historyThreshold;
	}

	@Min(0)
	@Max(128)
	@NotNull
	@Column(name = "MIN_LOWERCASE_CHARS_COUNT", nullable = false)
	public Integer getMinLowercaseCharsCount() {
		return minLowercaseCharsCount;
	}

	public void setMinLowercaseCharsCount(Integer minLowercaseCharsCount) {
		this.minLowercaseCharsCount = minLowercaseCharsCount;
	}

	@Min(0)
	@Max(128)
	@NotNull
	@Column(name = "MIN_UPPERCASE_CHARS_COUNT", nullable = false)
	public Integer getMinUppercaseCharsCount() {
		return minUppercaseCharsCount;
	}

	public void setMinUppercaseCharsCount(Integer minUppercaseCharsCount) {
		this.minUppercaseCharsCount = minUppercaseCharsCount;
	}

	@Min(0)
	@Max(128)
	@NotNull
	@Column(name = "MIN_NUMERIC_COUNT", nullable = false)
	public Integer getMinNumericCount() {
		return minNumericCount;
	}

	public void setMinNumericCount(Integer minNumericCount) {
		this.minNumericCount = minNumericCount;
	}

}
