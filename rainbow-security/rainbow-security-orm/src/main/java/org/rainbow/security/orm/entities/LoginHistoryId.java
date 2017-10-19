package org.rainbow.security.orm.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class LoginHistoryId implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8186621462023851884L;
	private Long membershipId;
	private Integer historyId;

	@Column(name = "MEMBERSHIP_ID")
	public Long getMembershipId() {
		return membershipId;
	}

	public void setMembershipId(Long userId) {
		this.membershipId = userId;
	}

	@Column(name = "HISTORY_ID")
	public Integer getHistoryId() {
		return historyId;
	}

	public void setHistoryId(Integer loginId) {
		this.historyId = loginId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((historyId == null) ? 0 : historyId.hashCode());
		result = prime * result + ((membershipId == null) ? 0 : membershipId.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof LoginHistoryId)) {
			return false;
		}
		LoginHistoryId other = (LoginHistoryId) obj;
		if (historyId == null) {
			if (other.historyId != null) {
				return false;
			}
		} else if (!historyId.equals(other.historyId)) {
			return false;
		}
		if (membershipId == null) {
			if (other.membershipId != null) {
				return false;
			}
		} else if (!membershipId.equals(other.membershipId)) {
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.getClass().getName() + "[ membershipId=" + getMembershipId() + ", historyId=" + getHistoryId() + " ]";
	}

}
