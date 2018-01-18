package lib.data.generator;

import lib.data.BaseCallData;
import lib.data.has.hasLibraryType.LIBRARY_TYPE;
import lib.util.coordinate.Coordinate;

public class BaseCallDataGenerator
extends AbstractDataGenerator<BaseCallData> {

	@Override
	public BaseCallData copyData(final BaseCallData data) {
		return new BaseCallData(data);
	}	
	
	@Override
	public BaseCallData[][] createContainerData(int n) {
		return new BaseCallData[n][];
	}
	
	@Override
	public BaseCallData createData(LIBRARY_TYPE libraryType, Coordinate coordinate) {
		return new BaseCallData(libraryType, coordinate, (byte)'N');
	}
	
	@Override
	public BaseCallData[] createReplicateData(int n) {
		return new BaseCallData[n];
	}
	
}
