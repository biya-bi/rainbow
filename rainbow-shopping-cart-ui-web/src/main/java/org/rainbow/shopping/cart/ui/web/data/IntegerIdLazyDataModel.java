/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.shopping.cart.ui.web.data;

/**
 *
 * @author Biya-Bi
 * @param <TModel>
 */
public abstract class IntegerIdLazyDataModel<TModel> extends AbstractLazyDataModel<TModel, Integer> {

    /**
	 * 
	 */
	private static final long serialVersionUID = 4920554289737788307L;

	@Override
    protected Integer toModelId(String rowKey) {
        return Integer.valueOf(rowKey);
    }
}
