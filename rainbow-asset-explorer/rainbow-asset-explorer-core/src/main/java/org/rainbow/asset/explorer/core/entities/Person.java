package org.rainbow.asset.explorer.core.entities;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
public abstract class Person extends BusinessEntity {

    /**
	 * 
	 */
	private static final long serialVersionUID = 7377865974183954126L;
	private String lastName;
    private String firstName;
    private String middleName;
    private Gender gender;
    private Date birthDate;
    private String title;
    private String suffix;
    private boolean nameStyle;

    public Person() {
    }

    public Person(Long id) {
        super(id);
    }

    public Person(String lastName, String firstName, String middleName, Gender gender, Date birthDate, String title, String suffix, boolean nameStyle, Date creationDate, Date lastUpdateDate) {
        super(creationDate, lastUpdateDate);
        this.lastName = lastName;
        this.firstName = firstName;
        this.middleName = middleName;
        this.gender = gender;
        this.birthDate = birthDate;
        this.title = title;
        this.suffix = suffix;
        this.nameStyle = nameStyle;
    }

    public Person(String lastName, String firstName, String middleName, Gender gender, Date birthDate, String title, String suffix, boolean nameStyle, Date creationDate, Date lastUpdateDate, long version, Long id) {
        super(creationDate, lastUpdateDate, version, id);
        this.lastName = lastName;
        this.firstName = firstName;
        this.middleName = middleName;
        this.gender = gender;
        this.birthDate = birthDate;
        this.title = title;
        this.suffix = suffix;
        this.nameStyle = nameStyle;
    }

    @NotNull
    @Size(min = 1)
    @Column(name = "LAST_NAME", nullable = false)
    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Column(name = "FIRST_NAME")
    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Column(name = "MIDDLE_NAME")
    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "BIRTH_DATE")
    public Date getBirthDate() {
        return this.birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSuffix() {
        return this.suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    @Column(name = "NAME_STYLE", nullable = false)
    public boolean isNameStyle() {
        return nameStyle;
    }

    public void setNameStyle(boolean nameStyle) {
        this.nameStyle = nameStyle;
    }

    @Override
    public String toString() {
        return "org.rainbow.asset.explorer.core.entities.Person[ id=" + getId() + " ]";
    }
}
