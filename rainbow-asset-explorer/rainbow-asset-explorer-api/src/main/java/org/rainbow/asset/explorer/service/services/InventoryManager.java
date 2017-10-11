package org.rainbow.asset.explorer.service.services;

import java.util.Map;

public interface InventoryManager {
	void add(Long locationId, Map<Long, Short> productIdsQuantities) throws Exception;

	void substract(Long locationId, Map<Long, Short> productIdsQuantities) throws Exception;
}