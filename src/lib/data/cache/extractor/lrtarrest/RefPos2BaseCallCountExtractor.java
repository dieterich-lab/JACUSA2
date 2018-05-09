package lib.data.cache.extractor.lrtarrest;

import lib.data.AbstractData;
import lib.data.cache.lrtarrest.RefPos2BaseCallCount;

public interface RefPos2BaseCallCountExtractor<T extends AbstractData> {

	RefPos2BaseCallCount getRefPos2BaseCallCountExtractor(T data);

}
