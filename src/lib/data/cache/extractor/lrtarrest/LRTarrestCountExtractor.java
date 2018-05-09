package lib.data.cache.extractor.lrtarrest;

import lib.data.AbstractData;
import lib.data.count.LRTarrestCount;

public interface LRTarrestCountExtractor<T extends AbstractData> {

	LRTarrestCount getLRTarrestCountExtractor(T data);

}
