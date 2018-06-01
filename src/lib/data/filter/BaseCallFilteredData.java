package lib.data.filter;

import lib.data.AbstractFilteredData;
import lib.data.count.BaseCallCount;

public class BaseCallFilteredData extends AbstractFilteredData<BaseCallCount> {

	public BaseCallFilteredData() {
		super();
	}
	
	public BaseCallFilteredData(final BaseCallFilteredData src) {
		super(src);
	}
	
	@Override
	public void merge(final AbstractFilteredData<BaseCallCount> src) {
		for (final char c : src.getFilters()) {
			if (! contains(c)) {
				add(c, src.get(c).copy());
			} else {
				get(c).add(src.get(c));
			}
		}
	}
	
	@Override
	public AbstractFilteredData<BaseCallCount> copy() {
		return new BaseCallFilteredData(this);
	}
	
	@Override
	protected BaseCallCount copy(final BaseCallCount data) {
		return data.copy();
	}
	
}
