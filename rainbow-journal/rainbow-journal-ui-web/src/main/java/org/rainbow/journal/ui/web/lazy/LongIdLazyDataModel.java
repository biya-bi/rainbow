/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.journal.ui.web.lazy;

/**
 *
 * @author Biya-Bi
 * @param <TEntity>
 */
public abstract class LongIdLazyDataModel<TEntity> extends AbstractLazyDataModel<TEntity, Long> {

    /**
	 * 
	 */
	private static final long serialVersionUID = 6075974495435093548L;

	@Override
    protected Long toModelId(String rowKey) {
        return Long.valueOf(rowKey);
    }
}
