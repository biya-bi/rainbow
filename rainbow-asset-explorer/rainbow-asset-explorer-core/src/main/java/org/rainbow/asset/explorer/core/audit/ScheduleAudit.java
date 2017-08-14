/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.asset.explorer.core.audit;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.rainbow.asset.explorer.core.entities.DayOfWeek;
import org.rainbow.asset.explorer.core.entities.Month;
import org.rainbow.asset.explorer.core.entities.Schedule;

/**
 *
 * @author Biya-Bi
 */
@Entity
@Table(name = "SCHEDULE_AUDIT")
public class ScheduleAudit extends TrackableAudit<Schedule, Integer> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3781270396524470338L;
	private Byte second;
	private Byte minute;
	private Byte hour;
	private String dayOfMonth;
	private Month month;
	private DayOfWeek dayOfWeek;
	private String timezone;
	private String year;

	public ScheduleAudit() {
	}

	public ScheduleAudit(Schedule schedule, WriteOperation writeOperation) {
		super(schedule, writeOperation);
		this.second = schedule.getSecond();
		this.minute = schedule.getMinute();
		this.hour = schedule.getHour();
		this.dayOfMonth = schedule.getDayOfMonth();
		this.month = schedule.getMonth();
		this.dayOfWeek = schedule.getDayOfWeek();
		this.timezone = schedule.getTimezone();
		this.year = schedule.getYear();
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

	@Override
	public String toString() {
		return "org.rainbow.asset.explorer.core.audit.ScheduleAudit[ auditId=" + getAuditId() + " ]";
	}
}
