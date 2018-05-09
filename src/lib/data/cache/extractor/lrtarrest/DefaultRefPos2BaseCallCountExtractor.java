package lib.data.cache.extractor.lrtarrest;

import lib.data.AbstractData;
import lib.data.cache.lrtarrest.RefPos2BaseCallCount;
import lib.data.has.HasLRTarrestCount;

public class DefaultRefPos2BaseCallCountExtractor<T extends AbstractData & HasLRTarrestCount> 
implements RefPos2BaseCallCountExtractor<T> {

	public RefPos2BaseCallCount getRefPos2BaseCallCountExtractor(T data) {
		return data.getLRTarrestCount().getRefPos2bc4arrest();
	}

}
