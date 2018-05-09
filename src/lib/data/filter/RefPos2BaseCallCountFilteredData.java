package lib.data.filter;

import lib.data.AbstractFilteredData;
import lib.data.cache.lrtarrest.RefPos2BaseCallCount;

public class RefPos2BaseCallCountFilteredData extends AbstractFilteredData<RefPos2BaseCallCount> {

	public RefPos2BaseCallCountFilteredData() {
		super();
	}
	
	public RefPos2BaseCallCountFilteredData(final RefPos2BaseCallCountFilteredData src) {
		super(src);
	}
	
	@Override
	public void merge(final AbstractFilteredData<RefPos2BaseCallCount> src) {
		for (final char c : src.getFilters()) {
			RefPos2BaseCallCount dest;
			if ((dest = get(c)) != null) {
				dest.add(src.get(c));
			} else {
				add(c, src.get(c));
			}
		}
	}
	
	@Override
	public AbstractFilteredData<RefPos2BaseCallCount> copy() {
		return new RefPos2BaseCallCountFilteredData(this);
	}
	
	@Override
	protected RefPos2BaseCallCount copy(RefPos2BaseCallCount data) {
		return data.copy();
	}
	
}
