package org.rainbow.asset.explorer.service.services;

import org.rainbow.asset.explorer.orm.entities.Currency;
import org.rainbow.asset.explorer.service.exceptions.DuplicateCurrencyNameException;
import org.rainbow.service.ServiceImpl;
import org.rainbow.service.UpdateOperation;
import org.rainbow.utilities.DaoUtil;

public class CurrencyServiceImpl extends ServiceImpl<Currency> implements CurrencyService {

	public CurrencyServiceImpl() {
	}

	@Override
	protected void validate(Currency currency, UpdateOperation operation) throws Exception {
		switch (operation) {
		case CREATE:
		case UPDATE:
			if (DaoUtil.isDuplicate(this.getDao(), "name", currency.getName(), currency.getId(), operation)) {
				throw new DuplicateCurrencyNameException(currency.getName());
			}
			break;
		case DELETE:
			break;
		default:
			break;
		}
	}
}
