package lib.data.cache.extractor.basecall;

import lib.data.AbstractData;
import lib.data.count.BaseCallCount;
import lib.data.has.HasBaseCallCount;

public class DefaultBaseCallCountExtractor<T extends AbstractData & HasBaseCallCount>
implements BaseCallCountExtractor<T> {

	@Override
	public BaseCallCount getBaseCallCount(T data) {
		return data.getBaseCallCount();
	}	

}
