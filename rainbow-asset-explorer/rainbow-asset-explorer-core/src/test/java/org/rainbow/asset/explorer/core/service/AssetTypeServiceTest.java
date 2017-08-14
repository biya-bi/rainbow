/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.asset.explorer.core.service;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

import org.junit.Assert;
import org.junit.Test;
import org.rainbow.asset.explorer.core.entities.AssetType;
import org.rainbow.asset.explorer.core.persistence.exceptions.DuplicateAssetTypeNameException;
import org.rainbow.asset.explorer.core.utilities.PersistenceSettings;
import org.rainbow.common.test.DatabaseInitialize;
import org.rainbow.core.persistence.SearchOptions;
import org.rainbow.core.persistence.exceptions.NonexistentEntityException;
import org.rainbow.core.service.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 *
 * @author Biya-Bi
 */
@DatabaseInitialize("src/test/resources/AssetTypeServiceTestSetup.sql")
public class AssetTypeServiceTest extends AbstractServiceTest {

	private static final long NON_EXISTENT_LONG_ID = 5000000L;

	@Autowired
	@Qualifier("assetTypeService")
	private Service<AssetType, Long, SearchOptions> assetTypeService;

	@PersistenceContext(unitName = PersistenceSettings.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

		@Test
	public void create_AssetTypeIsValid_AssetTypeCreated() throws Exception {
		AssetType expected = new AssetType("NEW-ASSET-TYPE");

		assetTypeService.create(expected);

		AssetType actual = em.getReference(AssetType.class, expected.getId());
		actual.getId(); // If no exception is thrown, then the create was
						// successful.
	}

	@Test(expected = DuplicateAssetTypeNameException.class)
	public void create_NameAlreadyExists_ThrowDuplicateAssetTypeNameException() throws Exception {
		AssetType assetType = new AssetType("AssetType 3001");

		try {
			assetTypeService.create(assetType);
		} catch (DuplicateAssetTypeNameException e) {
			Assert.assertEquals(assetType.getName(), e.getName());
			throw e;
		}
	}

	@Test
	public void delete_AssetTypeExists_AssetTypeDeleted() throws Exception {
		final AssetType assetType = new AssetType(3002L);
		boolean deleted = false;

		assetTypeService.delete(assetType);

		try {
			em.getReference(AssetType.class, assetType.getId());
		} catch (EntityNotFoundException e) {
			deleted = true;
		}
		Assert.assertTrue(deleted);
	}

	@Test(expected = NonexistentEntityException.class)
	public void delete_AssetTypeDoesNotExist_ThrowNonexistentEntityException() throws Exception {
		final AssetType assetType = new AssetType(NON_EXISTENT_LONG_ID);
		assetTypeService.delete(assetType);
	}

	@Test
	public void update_AssetTypeIsValid_AssetTypeEdited() throws Exception {
		AssetType expected = em.getReference(AssetType.class, 3003L);
		expected.setName(expected.getName() + " Renamed");

		em.clear();

		assetTypeService.update(expected);

		AssetType actual = em.getReference(AssetType.class, expected.getId());
		Assert.assertEquals(expected.getName(), actual.getName());
	}

	@Test(expected = DuplicateAssetTypeNameException.class)
	public void update_NameAlreadyExists_ThrowDuplicateAssetTypeNameException() throws Exception {
		AssetType assetType = em.getReference(AssetType.class, 3005L);
		assetType.setName("AssetType 3004");

		try {
			assetTypeService.update(assetType);
		} catch (DuplicateAssetTypeNameException e) {
			Assert.assertEquals(assetType.getName(), e.getName());
			throw e;
		}
	}
}
