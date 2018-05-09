package lib.data.cache.extractor.basecall;

import lib.data.AbstractData;
import lib.data.count.BaseCallCount;
import lib.data.has.HasArrestBaseCallCount;

public class ArrestBaseCallCountExtractor<T extends AbstractData & HasArrestBaseCallCount>
implements BaseCallCountExtractor<T> {

	@Override
	public BaseCallCount getBaseCallCount(T data) {
		return data.getArrestBaseCallCount();
	}	
	
}
