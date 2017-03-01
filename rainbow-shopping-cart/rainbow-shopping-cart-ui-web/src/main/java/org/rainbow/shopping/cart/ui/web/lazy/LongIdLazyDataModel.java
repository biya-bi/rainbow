/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.shopping.cart.ui.web.lazy;

/**
 *
 * @author Biya-Bi
 * @param <TModel>
 */
public abstract class LongIdLazyDataModel<TModel> extends AbstractLazyDataModel<TModel, Long> {

    /**
	 * 
	 */
	private static final long serialVersionUID = 6075974495435093548L;

	@Override
    protected Long toModelId(String rowKey) {
        return Long.valueOf(rowKey);
    }
}
