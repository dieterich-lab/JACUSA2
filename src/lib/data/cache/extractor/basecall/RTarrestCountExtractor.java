package lib.data.cache.extractor.basecall;

import lib.data.AbstractData;
import lib.data.count.RTarrestCount;

public interface RTarrestCountExtractor<X extends AbstractData> {

	RTarrestCount getRTcount(X data);

}
