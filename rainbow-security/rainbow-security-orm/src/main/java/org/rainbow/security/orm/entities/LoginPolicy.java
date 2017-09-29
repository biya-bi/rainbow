package org.rainbow.security.orm.entities;

import java.io.Serializable;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "LOGIN_POLICIES")
@Access(value = AccessType.PROPERTY)
public class LoginPolicy implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1428217396677807040L;
	private Application application;
	private Short threshold;

	@Id
	@OneToOne
	@JoinColumn(name = "APPLICATION_ID")
	public Application getApplication() {
		return application;
	}

	public void setApplication(Application application) {
		this.application = application;
	}

	@Min(1)
	@Max(999)
	@NotNull
	@Column(nullable = false)
	public Short getThreshold() {
		return threshold;
	}

	public void setThreshold(Short threshold) {
		this.threshold = threshold;
	}

}
