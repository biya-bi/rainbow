package org.rainbow.orm.listeners;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.persistence.config.DescriptorCustomizer;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.descriptors.DescriptorEventAdapter;
import org.eclipse.persistence.queries.InsertObjectQuery;
import org.rainbow.orm.audit.Auditable;
import org.rainbow.orm.audit.WriteOperation;

/**
 *
 * @author Biya-Bi
 */
public class WriteEventListener extends DescriptorEventAdapter implements DescriptorCustomizer {

	@Override
	public void customize(ClassDescriptor descriptor) {
		// descriptor.getEventManager().addListener(this);
	}

	/**
	 * This is where the real work of this class happens. For any INSERT,
	 * UPDATE or DELETE operation an INSERT of a new history object is forced.
	 */
	private void insertHistory(DescriptorEvent event, WriteOperation writeOperation) {
		Object source = event.getSource();
		Class<? extends Object> sourceClass = source.getClass();
		Auditable auditable = sourceClass.getAnnotation(Auditable.class);
		if (auditable != null && auditable.value() != null) {
			try {
				Constructor<?> constructor = auditable.value().getConstructor(sourceClass, WriteOperation.class);
				if (constructor != null) {
					Object audit = constructor.newInstance(source, writeOperation);
					InsertObjectQuery insertQuery = new InsertObjectQuery(audit);
					event.getSession().executeQuery(insertQuery);
				}
			} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
					| IllegalArgumentException | InvocationTargetException ex) {
				throw new RuntimeException(ex);
			}
		}
	}

	@Override
	public void postInsert(DescriptorEvent event) {
		insertHistory(event, WriteOperation.INSERT);
		super.postInsert(event);
	}

	@Override
	public void postUpdate(DescriptorEvent event) {
		insertHistory(event, WriteOperation.UPDATE);
		super.postUpdate(event);
	}

	@Override
	public void aboutToDelete(DescriptorEvent event) {
		insertHistory(event, WriteOperation.DELETE);
		super.aboutToDelete(event);
	}
}
