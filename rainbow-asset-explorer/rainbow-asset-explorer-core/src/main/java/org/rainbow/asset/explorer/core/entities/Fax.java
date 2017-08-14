package org.rainbow.asset.explorer.core.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Fax extends Trackable<Long> {

    /**
	 * 
	 */
	private static final long serialVersionUID = -720967501977936363L;
	private String line;

    public Fax() {
    }

    public Fax(String line, Long id) {
        super(id);
        this.line = line;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Override
    public Long getId() {
        return super.getId();
    }

    @Override
    public void setId(Long id) {
        super.setId(id);
    }

    public String getLine() {
        return this.line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    @Override
    public String toString() {
        return "org.rainbow.asset.explorer.core.entities.Fax[ id=" + getId() + " ]";
    }
}
