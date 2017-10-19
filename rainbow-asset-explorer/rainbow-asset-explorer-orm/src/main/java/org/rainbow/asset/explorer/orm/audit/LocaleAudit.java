package org.rainbow.asset.explorer.orm.audit;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.rainbow.asset.explorer.orm.entities.Locale;
import org.rainbow.orm.audit.AbstractAuditableEntityAudit;
import org.rainbow.orm.audit.WriteOperation;

/**
 *
 * @author Biya-Bi
 */
@Entity
@Table(name = "LOCALE_AUDIT")
public class LocaleAudit extends AbstractAuditableEntityAudit<Locale, Integer> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7688242870342268967L;
	private String name;
	private String languageCode;
	private String lcid;

	public LocaleAudit() {
	}

	public LocaleAudit(Locale locale, WriteOperation writeOperation) {
		super(locale, writeOperation);
		this.name = locale.getName();
		this.languageCode = locale.getLanguageCode();
		this.lcid = locale.getLcid();
	}

	@NotNull
	@Size(min = 1)
	@Column(name = "NAME", nullable = false)
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
