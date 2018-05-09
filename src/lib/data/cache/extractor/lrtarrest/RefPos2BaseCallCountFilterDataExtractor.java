package lib.data.cache.extractor.lrtarrest;

import lib.data.AbstractData;
import lib.data.cache.lrtarrest.RefPos2BaseCallCount;
import lib.data.has.filter.HasRefPos2BaseCallCountFilterData;

public class RefPos2BaseCallCountFilterDataExtractor<T extends AbstractData & HasRefPos2BaseCallCountFilterData> 
implements RefPos2BaseCallCountExtractor<T> {

	private final char c;
	
	public RefPos2BaseCallCountFilterDataExtractor(final char c) {
		this.c = c;
	}
	
	public RefPos2BaseCallCount getRefPos2BaseCallCountExtractor(T data) {
		return data.getRefPos2BaseCallCountFilterData().get(c);
	}

}
