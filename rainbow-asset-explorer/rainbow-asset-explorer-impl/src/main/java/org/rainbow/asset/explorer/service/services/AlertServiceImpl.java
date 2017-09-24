package org.rainbow.asset.explorer.service.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.quartz.SchedulerException;
import org.rainbow.asset.explorer.orm.entities.Alert;
import org.rainbow.asset.explorer.orm.entities.Schedule;
import org.rainbow.asset.explorer.scheduling.AlertScheduler;
import org.rainbow.asset.explorer.service.exceptions.DuplicateAlertException;
import org.rainbow.persistence.SearchOptions;
import org.rainbow.service.ServiceImpl;
import org.rainbow.service.UpdateOperation;
import org.rainbow.utilities.DaoUtil;

public class AlertServiceImpl extends ServiceImpl<Alert, Integer, SearchOptions> {

	private AlertScheduler alertScheduler;

	public AlertServiceImpl() {
	}

	public AlertScheduler getAlertScheduler() {
		return alertScheduler;
	}

	public void setAlertScheduler(AlertScheduler alertScheduler) {
		this.alertScheduler = alertScheduler;
	}

	@Override
	protected void validate(Alert alert, UpdateOperation operation) throws Exception {
		switch (operation) {
		case CREATE:
		case UPDATE:
			final Map<String, Comparable<?>> filters = new HashMap<>();
			filters.put("alertType", alert.getAlertType());
			filters.put("alertCategory", alert.getAlertCategory());
			if (DaoUtil.isDuplicate(this.getDao(), filters, alert.getId(), operation)) {
				throw new DuplicateAlertException(alert.getAlertType(), alert.getAlertCategory());
			}
			break;
		case DELETE:
			break;
		default:
			break;
		}
	}

	@Override
	public void create(Alert alert) throws Exception {
		checkDependencies();
		super.create(alert);
		onCreated(alert);
	}

	@Override
	public void create(List<Alert> alerts) throws Exception {
		checkDependencies();
		super.create(alerts);
		for (Alert alert : alerts) {
			onCreated(alert);
		}
	}

	@Override
	public void update(Alert alert) throws Exception {
		checkDependencies();
		super.update(alert);
		onUpdated(alert);
	}

	@Override
	public void update(List<Alert> alerts) throws Exception {
		checkDependencies();
		super.update(alerts);
		for (Alert alert : alerts) {
			onUpdated(alert);
		}
	}

	@Override
	public void delete(Alert alert) throws Exception {
		checkDependencies();
		super.delete(alert);
		onDeleted(alert);
	}

	@Override
	public void delete(List<Alert> alerts) throws Exception {
		checkDependencies();
		super.delete(alerts);
		for (Alert alert : alerts) {
			onDeleted(alert);
		}
	}

	@Override
	protected void checkDependencies() {
		super.checkDependencies();
		if (this.getAlertScheduler() == null) {
			throw new IllegalStateException("The alert scheduler cannot be null.");
		}
	}

	private void onCreated(Alert alert) throws SchedulerException {
		if (alert.isEnabled()) {
			Schedule schedule = alert.getSchedule();
			if (schedule != null) {
				this.getAlertScheduler().schedule(alert);
			}
		}
	}

	private void onUpdated(Alert alert) throws SchedulerException {
		// We need to unschedule the old job and schedule a new one.
		this.getAlertScheduler().unschedule(alert);
		this.getAlertScheduler().schedule(alert);
	}

	private void onDeleted(Alert alert) throws SchedulerException {
		// We need to unschedule the old job.
		this.getAlertScheduler().unschedule(alert);
	}
}
