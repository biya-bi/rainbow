package org.rainbow.security.faces.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.primefaces.event.ToggleEvent;
import org.primefaces.model.Visibility;
import org.rainbow.orm.entities.Trackable;

/**
 *
 * @author Biya-Bi
 * @param <TEntity>
 */
public abstract class AuditableController<TEntity extends Trackable<?>>
		extends Controller<TEntity> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5687105628085910454L;
	private List<Boolean> auditColumnsStates;

	public AuditableController() {
	}

	public AuditableController(Class<TEntity> entityClass) {
		super(entityClass);
	}

	@PostConstruct
	public void init() {
		auditColumnsStates = new ArrayList<>();
	}

	public List<Boolean> getAuditColumnsStates() {
		return auditColumnsStates;
	}

	public void onToggle(ToggleEvent e) {
		Integer index = (Integer) e.getData();
		int size = auditColumnsStates.size();
		if (auditColumnsStates.size() <= index) {
			for (int i = 0; i < index - size + 1; i++) {
				auditColumnsStates.add(false);
			}
		}
		auditColumnsStates.set(index, e.getVisibility() == Visibility.VISIBLE);
	}
}
