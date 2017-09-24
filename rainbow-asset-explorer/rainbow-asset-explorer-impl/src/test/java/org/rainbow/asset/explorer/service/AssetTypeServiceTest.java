package org.rainbow.asset.explorer.service;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

import org.junit.Assert;
import org.junit.Test;
import org.rainbow.asset.explorer.orm.entities.AssetType;
import org.rainbow.asset.explorer.service.exceptions.DuplicateAssetTypeNameException;
import org.rainbow.asset.explorer.utilities.PersistenceSettings;
import org.rainbow.common.test.DatabaseInitialize;
import org.rainbow.persistence.SearchOptions;
import org.rainbow.persistence.exceptions.NonexistentEntityException;
import org.rainbow.service.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 *
 * @author Biya-Bi
 */
@DatabaseInitialize("src/test/resources/SQL/AssetTypeServiceTestSetup.sql")
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
