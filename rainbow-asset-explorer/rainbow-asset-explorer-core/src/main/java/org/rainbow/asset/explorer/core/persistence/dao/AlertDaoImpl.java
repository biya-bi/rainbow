/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.asset.explorer.core.persistence.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.quartz.SchedulerException;
import org.rainbow.asset.explorer.core.entities.Alert;
import org.rainbow.asset.explorer.core.entities.Schedule;
import org.rainbow.asset.explorer.core.persistence.exceptions.DuplicateAlertException;
import org.rainbow.asset.explorer.core.persistence.exceptions.RainbowAssetExplorerException;
import org.rainbow.asset.explorer.core.scheduling.AlertScheduler;
import org.rainbow.asset.explorer.core.utilities.PersistenceSettings;
import org.rainbow.core.persistence.Pageable;
import org.rainbow.core.persistence.UpdateOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

/**
 *
 * @author Biya-Bi
 */
@Pageable(attributeName = "id")
public class AlertDaoImpl extends TrackableDaoImpl<Alert, Long> {

	@PersistenceContext(unitName = PersistenceSettings.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@Autowired
	@Qualifier("schedulerFactoryBean")
	private SchedulerFactoryBean schedulerFactoryBean;

	@Autowired
	@Qualifier("alertScheduler")
	private AlertScheduler alertScheduler;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public AlertDaoImpl() {
		super(Alert.class);
	}

	private boolean isDuplicate(Alert alert, Integer id) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Alert> rt = cq.from(Alert.class);
		cq.select(cb.count(rt));
		Predicate p1 = cb.equal(rt.get("alertType"), alert.getAlertType());
		Predicate p2 = cb.equal(rt.get("alertCategory"), alert.getAlertCategory());
		Predicate p3 = null;
		if (id != null) {
			p3 = cb.notEqual(rt.get("id"), id);
		}
		Predicate p = p3 == null ? cb.and(p1, p2) : cb.and(p1, p2, p3);
		cq.where(p);
		TypedQuery<Long> tq = em.createQuery(cq);
		return tq.getSingleResult() > 0;
	}

	@Override
	protected void validate(Alert alert, UpdateOperation operation) throws Exception {
		switch (operation) {
		case CREATE:
			if (isDuplicate(alert, null)) {
				throw new DuplicateAlertException(alert.getAlertType(), alert.getAlertCategory());
			}
			break;
		case UPDATE:
			if (isDuplicate(alert, alert.getId())) {
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
		Schedule schedule = alert.getSchedule();

		schedule.setCreator(alert.getCreator());
		schedule.setUpdater(alert.getUpdater());

		super.create(alert);
		
		em.flush();
		
		if (alert.isEnabled()) {
			if (schedule != null) {
				try {
					alertScheduler.schedule(alert);
				} catch (SchedulerException e) {
					throw new RainbowAssetExplorerException(e);
				}
			}
		}
	}

	@Override
	public void update(Alert alert) throws Exception {
		alert.getSchedule().setUpdater(alert.getUpdater());

		super.update(alert);

		try {
			// We need to unschedule the old job and schedule a new one.
			alertScheduler.unschedule(alert);
			alertScheduler.schedule(alert);
		} catch (SchedulerException e) {
			throw new RainbowAssetExplorerException(e);
		}
	}

	@Override
	public void delete(Alert alert) throws Exception {
		Schedule persistentSchedule = getPersistent(alert).getSchedule();

		super.delete(alert);

		persistentSchedule.setUpdater(alert.getUpdater());
		em.remove(persistentSchedule);

		// We need to unschedule the old job.
		try {
			alertScheduler.unschedule(alert);
		} catch (SchedulerException e) {
			throw new RainbowAssetExplorerException(e);
		}

	}
}
