package test.jacusa.filter;

import jacusa.filter.Filter;
import lib.data.ParallelData;
import lib.data.result.Result;

abstract class AbstractFilterWrapper<T extends Filter> implements Filter {

	private final T filter;

	protected AbstractFilterWrapper(final T filter) {
		this.filter = filter;
	}

	@Override
	public boolean filter(ParallelData parallelData) {
		return filter.filter(parallelData);
	}
	
	@Override
	public void addInfo(int valueIndex, Result result) {
		filter.addInfo(valueIndex, result);
	}
	
	@Override
	public boolean applyFilter(Result result) {
		return filter.applyFilter(result);
	}
	
	@Override
	public int getOverhang() {
		return filter.getOverhang();
	}
	
	@Override
	public char getC() {
		return filter.getC();
	}
	
	public T getFilter() {
		return filter;
	}
	
	public abstract String toString();

}
