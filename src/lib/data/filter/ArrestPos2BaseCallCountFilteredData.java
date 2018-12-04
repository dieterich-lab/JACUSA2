package lib.data.filter;

import java.util.Map;

import lib.data.cache.lrtarrest.ArrestPosition2baseCallCount;

public class ArrestPos2BaseCallCountFilteredData 
extends AbstractFilteredData<ArrestPos2BaseCallCountFilteredData, ArrestPosition2baseCallCount> {

	private static final long serialVersionUID = 1L;

	public ArrestPos2BaseCallCountFilteredData() {
		super();
	}
	
	protected ArrestPos2BaseCallCountFilteredData(Map<Character, ArrestPosition2baseCallCount> map) {
		super(map);
	}
	
	@Override
	protected ArrestPos2BaseCallCountFilteredData newInstance(Map<Character, ArrestPosition2baseCallCount> map) {
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
