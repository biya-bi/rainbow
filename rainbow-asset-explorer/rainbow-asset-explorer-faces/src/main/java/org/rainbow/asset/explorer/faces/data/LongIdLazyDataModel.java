package org.rainbow.asset.explorer.faces.data;

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
