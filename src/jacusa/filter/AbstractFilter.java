package jacusa.filter;

import lib.data.AbstractData;
import lib.data.ParallelData;
import lib.data.result.Result;

public abstract class AbstractFilter<T extends AbstractData> {

	private final char c;
	private final int overhang;
	
	protected AbstractFilter(final char c) {
		this(c, 0);
	}
	
	protected AbstractFilter(final char c, final int overhang) {
		this.c = c;
		this.overhang = overhang;
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
	 * @param parallelData
	 * @param conditionContainer
	 * @return
	 */
	protected abstract boolean filter(final ParallelData<T> parallelData);

	/**
	 * 
	 * @return
	 */
	public int getOverhang() {
		return overhang;
	}
	
	/**
	 * 
	 * @param result
	 * @param conditonContainer
	 * @return
	 */
	public boolean applyFilter(final Result<T> result) {
		final ParallelData<T> parallelData = result.getParellelData();
		if (filter(parallelData)) {
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

}
