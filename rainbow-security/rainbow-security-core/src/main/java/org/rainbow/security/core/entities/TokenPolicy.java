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

/**
 *
 * @author Biya-Bi
 */
@Entity
@Table(name = "TOKEN_POLICIES")
public class TokenPolicy implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 973807831582580559L;
	private Application application;
    private Integer maximumAge = 30;

    @Id
    @OneToOne
    @JoinColumn(name = "APPLICATION_ID")
    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    @Min(0)
    @Max(2628000)
    @Column(name = "MAXIMUM_AGE", nullable = false)
    public Integer getMaximumAge() {
        return maximumAge;
    }

    public void setMaximumAge(Integer maximumAge) {
        this.maximumAge = maximumAge;
    }
}
