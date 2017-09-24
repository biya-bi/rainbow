package org.rainbow.asset.explorer.scheduling;

import java.util.List;

import org.quartz.SchedulerException;
import org.rainbow.asset.explorer.orm.entities.Alert;
import org.rainbow.asset.explorer.orm.entities.AlertType;

public interface AlertScheduler {
	void schedule(Alert alert) throws SchedulerException;

	void unschedule(Alert alert) throws SchedulerException;

	void schedule(AlertType alertType, Long locationId, List<Long> productIds) throws SchedulerException;
}