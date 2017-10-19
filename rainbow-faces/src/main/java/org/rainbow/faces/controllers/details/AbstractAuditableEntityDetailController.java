package org.rainbow.faces.controllers.details;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.primefaces.event.ToggleEvent;
import org.primefaces.model.Visibility;
import org.rainbow.orm.entities.AbstractAuditableEntity;

/**
 *
 * @author Biya-Bi
 * @param <TEntity>
 */
public abstract class AbstractAuditableEntityDetailController<TEntity extends AbstractAuditableEntity<?>> extends AbstractDetailController<TEntity> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6917842044825541623L;
	private List<Boolean> auditColumnsStates;

	public AbstractAuditableEntityDetailController() {
	}

	public AbstractAuditableEntityDetailController(Class<TEntity> modelClass) {
		super(modelClass);
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