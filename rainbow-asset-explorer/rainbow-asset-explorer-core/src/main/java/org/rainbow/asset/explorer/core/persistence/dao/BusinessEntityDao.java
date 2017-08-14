/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.asset.explorer.core.persistence.dao;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;

import org.rainbow.asset.explorer.core.entities.BusinessEntity;
import org.rainbow.asset.explorer.core.entities.BusinessEntityAddress;
import org.rainbow.asset.explorer.core.entities.BusinessEntityEmail;
import org.rainbow.asset.explorer.core.entities.BusinessEntityFax;
import org.rainbow.asset.explorer.core.entities.BusinessEntityPhone;

/**
 *
 * @author Biya-Bi
 * @param <TEntity>
 */
public abstract class BusinessEntityDao<TEntity extends BusinessEntity, TKey extends Serializable> extends TrackableDaoImpl<TEntity,TKey> {

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
