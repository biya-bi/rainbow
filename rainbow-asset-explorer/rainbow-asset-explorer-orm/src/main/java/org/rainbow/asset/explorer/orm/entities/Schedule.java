package org.rainbow.asset.explorer.orm.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.rainbow.asset.explorer.orm.adapters.AuditAdapter;
import org.rainbow.asset.explorer.orm.audit.Auditable;
import org.rainbow.asset.explorer.orm.audit.ScheduleAudit;
import org.rainbow.orm.entities.Trackable;

/**
 *
 * @author Biya-Bi
 */
@Entity
@EntityListeners(AuditAdapter.class)
@Auditable(audit = ScheduleAudit.class)
public class Schedule extends Trackable<Integer> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5212147866833418490L;
	private Byte second;
	private Byte minute;
	private Byte hour;
	private String dayOfMonth;
	private Month month;
	private DayOfWeek dayOfWeek;
	private String timezone;
	private String year;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Override
	public Integer getId() {
		return super.getId();
	}

	@Override
	public void setId(Integer id) {
		super.setId(id);
	}

	@Min(0)
	@Max(59)
	public Byte getSecond() {
		return second;
	}

	public void setSecond(Byte second) {
		this.second = second;
	}

	@Min(0)
	@Max(59)
	public Byte getMinute() {
		return minute;
	}

	public void setMinute(Byte minute) {
		this.minute = minute;
	}

	@Min(0)
	@Max(23)
	public Byte getHour() {
		return hour;
	}

	public void setHour(Byte hour) {
		this.hour = hour;
	}

	@Column(name = "DAY_OF_MONTH")
	public String getDayOfMonth() {
		return dayOfMonth;
	}

	public void setDayOfMonth(String dayOfMonth) {
		this.dayOfMonth = dayOfMonth;
	}

	@Enumerated(EnumType.STRING)
	public Month getMonth() {
		return month;
	}

	public void setMonth(Month month) {
		this.month = month;
	}

	@Column(name = "DAY_OF_WEEK")
	@Enumerated(EnumType.STRING)
	public DayOfWeek getDayOfWeek() {
		return dayOfWeek;
	}

	public void setDayOfWeek(DayOfWeek dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}

	@Column(name = "TIME_ZONE")
	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

}
