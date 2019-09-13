package lib.data.filter;

import java.util.Map;

import lib.data.storage.lrtarrest.ArrestPos2BCC;

public class ArrestPos2BaseCallCountFilteredData 
extends AbstractFilteredData<ArrestPos2BaseCallCountFilteredData, ArrestPos2BCC> {

	private static final long serialVersionUID = 1L;

	public ArrestPos2BaseCallCountFilteredData() {
		super();
	}

	protected ArrestPos2BaseCallCountFilteredData(Map<Character, ArrestPos2BCC> map) {
		super(map);
	}

	@Override
	protected ArrestPos2BaseCallCountFilteredData newInstance(Map<Character, ArrestPos2BCC> map) {
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

		return specificEquals(getClass().cast(obj));
	}
	
}
