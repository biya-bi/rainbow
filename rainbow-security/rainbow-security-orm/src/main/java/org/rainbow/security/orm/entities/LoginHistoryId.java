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
	private Long userId;
	private Integer historyId;

	@Column(name = "USER_ID")
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
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
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
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
		if (userId == null) {
			if (other.userId != null) {
				return false;
			}
		} else if (!userId.equals(other.userId)) {
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
		return this.getClass().getName() + "[ userId=" + getUserId() + ", historyId=" + getHistoryId() + " ]";
	}

}
