package lib.data.cache.extractor.basecall;

import lib.data.AbstractData;
import lib.data.count.RTarrestCount;
import lib.data.has.HasRTcount;

public class DefaultRTcountExtractor<T extends AbstractData & HasRTcount>
implements RTarrestCountExtractor<T> {

	@Override
	public RTarrestCount getRTcount(T data) {
		return data.getRTarrestCount();
	}

}
