/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.asset.explorer.core.audit;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.eclipse.persistence.config.DescriptorCustomizer;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.descriptors.DescriptorEventAdapter;
import org.eclipse.persistence.queries.InsertObjectQuery;

/**
 *
 * @author Biya-Bi
 */
public class AuditAdapter extends DescriptorEventAdapter implements DescriptorCustomizer {

    @Override
    public void customize(ClassDescriptor descriptor) {
        //descriptor.getEventManager().addListener(this);
    }

    /**
     * This is where the real work of this class happens. For any INSERT or
     * UPDATE operation an INSERT of a new history object is forced.
     */
    private void insertAudit(DescriptorEvent event, WriteOperation writeOperation) {
        Object source = event.getSource();
        Class<? extends Object> sourceClass = source.getClass();
        Auditable auditable = sourceClass.getAnnotation(Auditable.class);
        if (auditable != null && auditable.audit() != null) {
            try {
                Constructor<?> constructor = auditable.audit().getConstructor(sourceClass, WriteOperation.class);
                if (constructor != null) {
                    Object audit = constructor.newInstance(source, writeOperation);
                    InsertObjectQuery insertQuery = new InsertObjectQuery(audit);
                    event.getSession().executeQuery(insertQuery);
                }
            } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    @Override
    public void postInsert(DescriptorEvent event) {
        insertAudit(event, WriteOperation.INSERT);
        super.postInsert(event);
    }

    @Override
    public void postUpdate(DescriptorEvent event) {
        insertAudit(event, WriteOperation.UPDATE);
        super.postUpdate(event);
    }

    @Override
    public void aboutToDelete(DescriptorEvent event) {
        insertAudit(event, WriteOperation.DELETE);
        super.aboutToDelete(event);
    }
}
