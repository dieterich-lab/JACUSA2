package lib.data.cache.extractor.basecall;

import lib.data.AbstractData;
import lib.data.basecall.array.ArrayBaseCallCount;
import lib.data.count.BaseCallCount;
import lib.data.has.filter.HasBaseCallCountFilterData;

public class BaseCallCountFilterDataExtractor<T extends AbstractData & HasBaseCallCountFilterData> 
implements BaseCallCountExtractor<T> {

	private final char c;
	
	public BaseCallCountFilterDataExtractor(final char c) {
		this.c = c;
	}
	
	@Override
	public BaseCallCount getBaseCallCount(T data) {
		if (! data.getBaseCallCountFilterData().contains(c)) {
			data.getBaseCallCountFilterData().add(c, new ArrayBaseCallCount());
		}
		return data.getBaseCallCountFilterData().get(c);
	}

}
