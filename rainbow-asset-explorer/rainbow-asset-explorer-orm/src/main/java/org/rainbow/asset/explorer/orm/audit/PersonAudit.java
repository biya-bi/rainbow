package org.rainbow.asset.explorer.orm.audit;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.rainbow.asset.explorer.orm.entities.Gender;
import org.rainbow.asset.explorer.orm.entities.Person;
import org.rainbow.orm.audit.AbstractAuditableEntityAudit;
import org.rainbow.orm.audit.WriteOperation;

/**
 *
 * @author Biya-Bi
 * @param <T>
 */
@MappedSuperclass
public abstract class PersonAudit<T extends Person> extends AbstractAuditableEntityAudit<T, Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8445823416657042687L;
	private String lastName;
	private String firstName;
	private String middleName;
	private Gender gender;
	private Date birthDate;
	private String title;
	private String suffix;
	private boolean nameStyle;

	public PersonAudit() {
	}

	public PersonAudit(T person, WriteOperation writeOperation) {
		super(person, writeOperation);
		this.lastName = person.getLastName();
		this.firstName = person.getFirstName();
		this.middleName = person.getMiddleName();
		this.gender = person.getGender();
		this.birthDate = person.getBirthDate();
		this.title = person.getTitle();
		this.suffix = person.getSuffix();
		this.nameStyle = person.isNameStyle();
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

}
