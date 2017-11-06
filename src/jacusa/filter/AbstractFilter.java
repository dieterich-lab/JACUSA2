package jacusa.filter;

import jacusa.data.AbstractData;
import jacusa.data.Result;
import jacusa.pileup.iterator.WindowedIterator;

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
	protected abstract boolean filter(final Result<T> result, final WindowedIterator<T> windowIterator);
	
	/**
	 * 
	 * @param result
	 * @param location
	 * @param windowIterator
	 * @return
	 */
	public boolean applyFilter(final Result<T> result, final WindowedIterator<T> windowIterator) {
		if (filter(result, windowIterator)) {
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
