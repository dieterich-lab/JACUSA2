package lib.data.cache.extractor.basecall;

import lib.data.AbstractData;
import lib.data.count.BaseCallCount;

public interface BaseCallCountExtractor<T extends AbstractData> {

	BaseCallCount getBaseCallCount(T data);

}
