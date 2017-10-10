package org.rainbow.asset.explorer.service.services;

import java.util.List;

import org.rainbow.asset.explorer.orm.entities.Product;
import org.rainbow.asset.explorer.persistence.dao.ProductDao;
import org.rainbow.asset.explorer.service.exceptions.DuplicateProductNameException;
import org.rainbow.asset.explorer.service.exceptions.DuplicateProductNumberException;
import org.rainbow.service.ServiceImpl;
import org.rainbow.service.UpdateOperation;
import org.rainbow.util.DaoUtil;

public class ProductServiceImpl extends ServiceImpl<Product> implements ProductService {

	public ProductServiceImpl() {
	}

	@Override
	public List<Product> find(List<String> productNumbers) {
		return ((ProductDao) this.getDao()).find(productNumbers);
	}

	@Override
	protected void validate(Product product, UpdateOperation operation) throws Exception {
		switch (operation) {
		case CREATE:
		case UPDATE:
			if (DaoUtil.isDuplicate(this.getDao(), "number", product.getNumber(), product.getId(), operation)) {
				throw new DuplicateProductNumberException(product.getNumber());
			}
			if (DaoUtil.isDuplicate(this.getDao(), "name", product.getName(), product.getId(), operation)) {
				throw new DuplicateProductNameException(product.getName());
			}
			break;
		case DELETE:
			break;
		default:
			break;
		}
	}
}
