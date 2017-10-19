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
import org.rainbow.security.orm.entities.Group;

/**
 *
 * @author Biya-Bi
 */
@Entity
@Table(name = "GROUPS_AUDIT")
public class GroupAudit extends AbstractAuditableEntityAudit<Group, Long> {
	/**
	* 
	*/
	private static final long serialVersionUID = 3826125289956868064L;
	private String name;
	private String description;
	private Long applicationId;

	public GroupAudit() {
	}

	public GroupAudit(Group group, WriteOperation writeOperation) {
		super(group, writeOperation);
		Objects.requireNonNull(group.getApplication());
		this.name = group.getName();
		this.description = group.getDescription();
		this.applicationId = group.getApplication().getId();
	}

	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 256)
	@Column(name = "NAME", nullable = false)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Size(max = 256)
	@Column(name = "DESCRIPTION")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@NotNull
	@Column(name = "APPLICATION_ID", nullable = false)
	public Long getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(Long applicationId) {
		this.applicationId = applicationId;
	}

}
