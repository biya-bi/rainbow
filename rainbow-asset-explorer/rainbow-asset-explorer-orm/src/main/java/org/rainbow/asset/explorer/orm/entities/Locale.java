package org.rainbow.asset.explorer.orm.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

import org.rainbow.asset.explorer.orm.audit.LocaleAudit;
import org.rainbow.orm.audit.Auditable;
import org.rainbow.orm.entities.AbstractNumericIdAuditableEntity;

/**
 *
 * @author Biya-Bi
 */
@Entity

@Auditable(LocaleAudit.class)
public class Locale extends AbstractNumericIdAuditableEntity<Integer> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8550765035253593065L;
	private String name;
	private String languageCode;
	private String lcid;

	public Locale() {
	}

	public Locale(Integer id) {
		super(id);
	}

	public Locale(String name) {
		this.name = name;
	}

	public Locale(String name, String languageCode, String lcid) {
		this.name = name;
		this.languageCode = languageCode;
		this.lcid = lcid;
	}

	public Locale(String name, String languageCode, String lcid, String createdBy, String updatedBy, Date creationDate,
			Date lastUpdateDate, long version, Integer id) {
		super(createdBy, updatedBy, creationDate, lastUpdateDate, version, id);
		this.name = name;
		this.languageCode = languageCode;
		this.lcid = lcid;
	}

	@NotNull
	@Column(nullable = false, unique = true)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "LANGUAGE_CODE")
	public String getLanguageCode() {
		return languageCode;
	}

	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}

	@Column
	public String getLcid() {
		return lcid;
	}

	public void setLcid(String lcid) {
		this.lcid = lcid;
	}

}
