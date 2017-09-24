package org.rainbow.asset.explorer.faces.data;

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
