package lib.data.generator;

import lib.data.CallData;
import lib.data.has.HasLibraryType.LIBRARY_TYPE;
import lib.util.coordinate.Coordinate;

public class CallDataGenerator
extends AbstractDataGenerator<CallData> {

	@Override
	public CallData createData(final LIBRARY_TYPE libraryType, final Coordinate coordinate) {
		return new CallData(libraryType, coordinate);
	}

	@Override
	public CallData[] createReplicateData(final int n) {
		return new CallData[n];
	}

	@Override
	public CallData[][] createContainerData(final int n) {
		return new CallData[n][];
	}

	@Override
	public CallData copyData(final CallData data) {
		return new CallData(data);
	}

}
