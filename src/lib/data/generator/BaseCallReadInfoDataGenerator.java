package lib.data.generator;

import lib.data.BaseCallReadInfoData;
import lib.data.has.hasLibraryType.LIBRARY_TYPE;
import lib.util.coordinate.Coordinate;

public class BaseCallReadInfoDataGenerator 
extends AbstractDataGenerator<BaseCallReadInfoData> {

	@Override	public BaseCallReadInfoData createData(LIBRARY_TYPE libraryType, Coordinate coordinate) {
		return new BaseCallReadInfoData(libraryType, coordinate, (byte)'N');
	}
	
	@Override
	public BaseCallReadInfoData[] createReplicateData(final int n) {
		return new BaseCallReadInfoData[n];
	}
	
	@Override
	public BaseCallReadInfoData[][] createContainerData(final int n) {
		return new BaseCallReadInfoData[n][];
	}

	@Override
	public BaseCallReadInfoData copyData(final BaseCallReadInfoData dataContainer) {
		return new BaseCallReadInfoData(dataContainer);
	}
	
}
