/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.security.core.entities;

import java.io.Serializable;
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
public class PasswordPolicy implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 6898206236783268070L;
	private Application application;
    private Integer minimumPasswordAge = 0;
    private Integer maximumPasswordAge = 30;
    private Integer maximumInvalidPasswordAttempts = 3;
    private Integer passwordAttemptWindow = 10;
    private Integer minimumPasswordLength = 1;
    private Integer maximumPasswordLength = 128;
    private Integer minimumSpecialCharacters = 0;
    private Integer passwordHistoryLength = 10;
    private Integer minimumLowercaseCharacters = 0;
    private Integer minimumUppercaseCharacters = 0;
    private Integer minimumNumeric = 0;

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
    @Column(name = "MINIMUM_PASSWORD_AGE", nullable = false)
    public Integer getMinimumPasswordAge() {
        return minimumPasswordAge;
    }

    public void setMinimumPasswordAge(Integer minimumPasswordAge) {
        this.minimumPasswordAge = minimumPasswordAge;
    }

    @Min(0)
    @Max(999)
    @NotNull
    @Column(name = "MAXIMUM_PASSWORD_AGE", nullable = false)
    public Integer getMaximumPasswordAge() {
        return maximumPasswordAge;
    }

    public void setMaximumPasswordAge(Integer maximumPasswordAge) {
        this.maximumPasswordAge = maximumPasswordAge;
    }

    @Min(1)
    @Max(10)
    @NotNull
    @Column(name = "MAXIMUM_INVALID_PASSWORD_ATTEMPTS", nullable = false)
    public Integer getMaximumInvalidPasswordAttempts() {
        return maximumInvalidPasswordAttempts;
    }

    public void setMaximumInvalidPasswordAttempts(Integer maximumInvalidPasswordAttempts) {
        this.maximumInvalidPasswordAttempts = maximumInvalidPasswordAttempts;
    }

    @Min(0)
    @Max(60)
    @NotNull
    @Column(name = "PASSWORD_ATTEMPT_WINDOW", nullable = false)
    public Integer getPasswordAttemptWindow() {
        return passwordAttemptWindow;
    }

    public void setPasswordAttemptWindow(Integer passwordAttemptWindow) {
        this.passwordAttemptWindow = passwordAttemptWindow;
    }

    @Min(1)
    @Max(128)
    @NotNull
    @Column(name = "MINIMUM_PASSWORD_LENGTH", nullable = false)
    public Integer getMinimumPasswordLength() {
        return minimumPasswordLength;
    }

    public void setMinimumPasswordLength(Integer minimumPasswordLength) {
        this.minimumPasswordLength = minimumPasswordLength;
    }

    @Min(1)
    @Max(128)
    @NotNull
    @Column(name = "MAXIMUM_PASSWORD_LENGTH", nullable = false)
    public Integer getMaximumPasswordLength() {
        return maximumPasswordLength;
    }

    public void setMaximumPasswordLength(Integer maximumPasswordLength) {
        this.maximumPasswordLength = maximumPasswordLength;
    }

    @Min(0)
    @Max(128)
    @NotNull
    @Column(name = "MINIMUM_SPECIAL_CHARACTERS", nullable = false)
    public Integer getMinimumSpecialCharacters() {
        return minimumSpecialCharacters;
    }

    public void setMinimumSpecialCharacters(Integer minimumSpecialCharacters) {
        this.minimumSpecialCharacters = minimumSpecialCharacters;
    }

    @Min(0)
    @Max(48)
    @NotNull
    @Column(name = "PASSWORD_HISTORY_LENGTH", nullable = false)
    public Integer getPasswordHistoryLength() {
        return passwordHistoryLength;
    }

    public void setPasswordHistoryLength(Integer passwordHistoryLength) {
        this.passwordHistoryLength = passwordHistoryLength;
    }

    @Min(0)
    @Max(128)
    @NotNull
    @Column(name = "MINIMUM_LOWERCASE_CHARACTERS", nullable = false)
    public Integer getMinimumLowercaseCharacters() {
        return minimumLowercaseCharacters;
    }

    public void setMinimumLowercaseCharacters(Integer minimumLowercaseCharacters) {
        this.minimumLowercaseCharacters = minimumLowercaseCharacters;
    }

    @Min(0)
    @Max(128)
    @NotNull
    @Column(name = "MINIMUM_UPPERCASE_CHARACTERS", nullable = false)
    public Integer getMinimumUppercaseCharacters() {
        return minimumUppercaseCharacters;
    }

    public void setMinimumUppercaseCharacters(Integer minimumUppercaseCharacters) {
        this.minimumUppercaseCharacters = minimumUppercaseCharacters;
    }

    @Min(0)
    @Max(128)
    @NotNull
    @Column(name = "MINIMUM_NUMERIC", nullable = false)
    public Integer getMinimumNumeric() {
        return minimumNumeric;
    }

    public void setMinimumNumeric(Integer minimumNumeric) {
        this.minimumNumeric = minimumNumeric;
    }

}
