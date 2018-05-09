package lib.data.cache.extractor.basecall;

import lib.data.AbstractData;
import lib.data.count.BaseCallCount;
import lib.data.has.HasThroughBaseCallCount;

public class ThroughBaseCallCountExtractor<T extends AbstractData & HasThroughBaseCallCount>
implements BaseCallCountExtractor<T> {

	@Override
	public BaseCallCount getBaseCallCount(T data) {
		return data.getThroughBaseCallCount();
	}	
	
}
