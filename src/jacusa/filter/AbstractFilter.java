package jacusa.filter;

import lib.data.AbstractData;
import lib.data.Result;
import lib.data.builder.ConditionContainer;

/**
 * 
 * @author Michael Piechotta
 *
 */
public abstract class AbstractFilter<T extends AbstractData> {

	private final char c;
	
	public AbstractFilter(final char c) {
		this.c = c;
	}

	/**
	 * 
	 * @return
	 */
	public final char getC() {
		return c;
	}
	
	/**
	 * 
	 * @param result
	 * @param location
	 * @param windowIterator
	 * @return
	 */
	// TODO replace conditionContainer with ParallelData
	protected abstract boolean filter(final Result<T> result, final ConditionContainer<T> conditionContainer);
	
	/**
	 * 
	 * @param result
	 * @param location
	 * @param windowIterator
	 * @return
	 */
	public boolean applyFilter(final Result<T> result, final ConditionContainer<T> conditonContainer) {
		if (filter(result, conditonContainer)) {
			addFilterInfo(result);
			return true;
		}

		return false;
	}

	/**
	 * 
	 * @param result
	 */
	public void addFilterInfo(Result<T> result) {
		result.getFilterInfo().add(Character.toString(getC()));
	}

	public abstract int getOverhang();

}
