package org.rainbow.asset.explorer.persistence.dao;

import java.util.List;

import javax.persistence.EntityManager;

import org.rainbow.asset.explorer.orm.entities.BusinessEntity;
import org.rainbow.asset.explorer.orm.entities.BusinessEntityAddress;
import org.rainbow.asset.explorer.orm.entities.BusinessEntityEmail;
import org.rainbow.asset.explorer.orm.entities.BusinessEntityFax;
import org.rainbow.asset.explorer.orm.entities.BusinessEntityPhone;
import org.rainbow.persistence.dao.DaoImpl;

/**
 *
 * @author Biya-Bi
 * @param <TEntity>
 */
public abstract class BusinessEntityDao<TEntity extends BusinessEntity> extends DaoImpl<TEntity> {

    public BusinessEntityDao(Class<TEntity> entityClass) {
        super(entityClass);
    }

    @Override
    public void create(TEntity entity) throws Exception {
        List<BusinessEntityAddress> addresses = entity.getAddresses();
        if (addresses != null) {
            for (BusinessEntityAddress address : addresses) {
                address.setBusinessEntity(entity);
            }
        }
        List<BusinessEntityPhone> phones = entity.getPhones();
        if (phones != null) {
            for (BusinessEntityPhone phone : phones) {
                phone.setBusinessEntity(entity);
            }
        }
        List<BusinessEntityFax> faxes = entity.getFaxes();
        if (faxes != null) {
            for (BusinessEntityFax fax : faxes) {
                fax.setBusinessEntity(entity);
            }
        }
        List<BusinessEntityEmail> emails = entity.getEmails();
        if (emails != null) {
            for (BusinessEntityEmail email : emails) {
                email.setBusinessEntity(entity);
            }
        }

        super.create(entity);
    }

    @Override
    public void update(TEntity entity) throws Exception {
        List<BusinessEntityAddress> addresses = entity.getAddresses();
        if (addresses != null) {
            for (BusinessEntityAddress address : addresses) {
                address.setBusinessEntity(entity);
            }
        }
        List<BusinessEntityPhone> phones = entity.getPhones();
        if (phones != null) {
            for (BusinessEntityPhone phone : phones) {
                phone.setBusinessEntity(entity);
            }
        }
        List<BusinessEntityFax> faxes = entity.getFaxes();
        if (faxes != null) {
            for (BusinessEntityFax fax : faxes) {
                fax.setBusinessEntity(entity);
            }
        }
        List<BusinessEntityEmail> emails = entity.getEmails();
        if (emails != null) {
            for (BusinessEntityEmail email : emails) {
                email.setBusinessEntity(entity);
            }
        }
        super.update(entity);
    }

    @Override
    protected void onDelete(TEntity entity, TEntity persistentEntity) {
        super.onDelete(entity, persistentEntity);
        EntityManager em = getEntityManager();
        List<BusinessEntityAddress> addresses = persistentEntity.getAddresses();
        if (addresses != null) {
            for (BusinessEntityAddress address : addresses) {
                em.remove(address);
            }
        }
        List<BusinessEntityPhone> phones = persistentEntity.getPhones();
        if (phones != null) {
            for (BusinessEntityPhone phone : phones) {
                em.remove(phone);
            }
        }
        List<BusinessEntityFax> faxes = persistentEntity.getFaxes();
        if (faxes != null) {
            for (BusinessEntityFax fax : faxes) {
                em.remove(fax);
            }
        }
        List<BusinessEntityEmail> emails = persistentEntity.getEmails();
        if (emails != null) {
            for (BusinessEntityEmail email : emails) {
                em.remove(email);
            }
        }
    }

}
