package lib.data.filter;

import java.util.Map;

import lib.data.cache.lrtarrest.ArrestPos2BaseCallCount;

public class ArrestPos2BaseCallCountFilteredData 
extends AbstractFilteredData<ArrestPos2BaseCallCountFilteredData, ArrestPos2BaseCallCount> {

	private static final long serialVersionUID = 1L;

	public ArrestPos2BaseCallCountFilteredData() {
		super();
	}
	
	protected ArrestPos2BaseCallCountFilteredData(Map<Character, ArrestPos2BaseCallCount> map) {
		super(map);
	}
	
	@Override
	protected ArrestPos2BaseCallCountFilteredData newInstance(Map<Character, ArrestPos2BaseCallCount> map) {
		return new ArrestPos2BaseCallCountFilteredData(map);
	}
	
	@Override
	public final boolean equals(Object obj) {
		if (obj == null || ! getClass().isInstance(obj)) {
			return false;
		}
		if (obj == this) {
			return true;
		}

		return equals(getClass().cast(obj));
	}
	
}
