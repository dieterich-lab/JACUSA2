package lib.data.cache.extractor.lrtarrest;

import lib.data.AbstractData;
import lib.data.count.LRTarrestCount;
import lib.data.has.HasLRTarrestCount;

public class DefaultLRTarrestCountExtractor<T extends AbstractData & HasLRTarrestCount> 
implements LRTarrestCountExtractor<T> {

	public LRTarrestCount getLRTarrestCountExtractor(T data) {
		return data.getLRTarrestCount();
	}

}
