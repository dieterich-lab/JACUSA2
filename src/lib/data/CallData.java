package lib.data;

import lib.util.coordinate.Coordinate;

public class CallData
extends PileupData {

	public CallData(LIBRARY_TYPE libraryType, Coordinate coordinate) {
		super(libraryType, coordinate);
	}

	public CallData(final CallData callData) {
		super(callData);
	}

	@Override
	public CallData copy() {
		return new CallData(this);
	}

	public void merge(final CallData src) {
		super.merge(src);
	}
	
}
