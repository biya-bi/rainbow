package org.rainbow.asset.explorer.persistence.dao;

import java.util.List;

import javax.persistence.EntityManager;

import org.rainbow.asset.explorer.orm.entities.BusinessEntity;
import org.rainbow.asset.explorer.orm.entities.BusinessEntityAddress;
import org.rainbow.asset.explorer.orm.entities.BusinessEntityEmail;
import org.rainbow.asset.explorer.orm.entities.BusinessEntityFax;
import org.rainbow.asset.explorer.orm.entities.BusinessEntityPhone;

/**
 *
 * @author Biya-Bi
 * @param <TEntity>
 */
public abstract class BusinessEntityDao<TEntity extends BusinessEntity> extends TrackableDaoImpl<TEntity> {

    public BusinessEntityDao(Class<TEntity> entityClass) {
        super(entityClass);
    }

    @Override
    public void create(TEntity entity) throws Exception {
        List<BusinessEntityAddress> addresses = entity.getAddresses();
        if (addresses != null) {
            for (BusinessEntityAddress address : addresses) {
                address.setBusinessEntity(entity);

                address.setCreator(entity.getCreator());
                address.setUpdater(entity.getUpdater());
            }
        }
        List<BusinessEntityPhone> phones = entity.getPhones();
        if (phones != null) {
            for (BusinessEntityPhone phone : phones) {
                phone.setBusinessEntity(entity);

                phone.setCreator(entity.getCreator());
                phone.setUpdater(entity.getUpdater());
            }
        }
        List<BusinessEntityFax> faxes = entity.getFaxes();
        if (faxes != null) {
            for (BusinessEntityFax fax : faxes) {
                fax.setBusinessEntity(entity);

                fax.setCreator(entity.getCreator());
                fax.setUpdater(entity.getUpdater());
            }
        }
        List<BusinessEntityEmail> emails = entity.getEmails();
        if (emails != null) {
            for (BusinessEntityEmail email : emails) {
                email.setBusinessEntity(entity);

                email.setCreator(entity.getCreator());
                email.setUpdater(entity.getUpdater());
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

                address.setUpdater(entity.getUpdater());
            }
        }
        List<BusinessEntityPhone> phones = entity.getPhones();
        if (phones != null) {
            for (BusinessEntityPhone phone : phones) {
                phone.setBusinessEntity(entity);

                phone.setUpdater(entity.getUpdater());
            }
        }
        List<BusinessEntityFax> faxes = entity.getFaxes();
        if (faxes != null) {
            for (BusinessEntityFax fax : faxes) {
                fax.setBusinessEntity(entity);

                fax.setUpdater(entity.getUpdater());
            }
        }
        List<BusinessEntityEmail> emails = entity.getEmails();
        if (emails != null) {
            for (BusinessEntityEmail email : emails) {
                email.setBusinessEntity(entity);

                email.setUpdater(entity.getUpdater());
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
                address.setUpdater(entity.getUpdater());
                em.remove(address);
            }
        }
        List<BusinessEntityPhone> phones = persistentEntity.getPhones();
        if (phones != null) {
            for (BusinessEntityPhone phone : phones) {
                phone.setUpdater(entity.getUpdater());
                em.remove(phone);
            }
        }
        List<BusinessEntityFax> faxes = persistentEntity.getFaxes();
        if (faxes != null) {
            for (BusinessEntityFax fax : faxes) {
                fax.setUpdater(entity.getUpdater());
                em.remove(fax);
            }
        }
        List<BusinessEntityEmail> emails = persistentEntity.getEmails();
        if (emails != null) {
            for (BusinessEntityEmail email : emails) {
                email.setUpdater(entity.getUpdater());
                em.remove(email);
            }
        }
    }

}
