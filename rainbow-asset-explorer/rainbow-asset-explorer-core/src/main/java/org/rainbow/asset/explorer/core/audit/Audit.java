/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.asset.explorer.core.audit;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Biya-Bi
 */
@MappedSuperclass
public abstract class Audit implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 5735060810921729766L;
	private Long auditId;
    private WriteOperation writeOperation;

    public Audit() {
    }

    public Audit(WriteOperation writeOperation) {
        this.writeOperation = writeOperation;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "AUDIT_ID")
    public Long getAuditId() {
        return auditId;
    }

    public void setAuditId(Long auditId) {
        this.auditId = auditId;
    }

    @NotNull
    @Column(name = "WRITE_OPERATION", nullable = false)
    @Enumerated(EnumType.STRING)
    public WriteOperation getWriteOperation() {
        return writeOperation;
    }

    public void setWriteOperation(WriteOperation writeOperation) {
        this.writeOperation = writeOperation;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.auditId);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Audit other = (Audit) obj;
        if (!Objects.equals(this.auditId, other.auditId)) {
            return false;
        }
        return true;
    }

}
