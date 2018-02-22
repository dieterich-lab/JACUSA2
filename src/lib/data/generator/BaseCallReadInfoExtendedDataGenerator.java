package lib.data.generator;

import lib.data.ReadInfoExtendedData;
import lib.data.has.hasLibraryType.LIBRARY_TYPE;
import lib.util.coordinate.Coordinate;

public class BaseCallReadInfoExtendedDataGenerator 
extends AbstractDataGenerator<ReadInfoExtendedData> {

	@Override
	public ReadInfoExtendedData createData(LIBRARY_TYPE libraryType, Coordinate coordinate) {
		return new ReadInfoExtendedData(libraryType, coordinate, (byte)'N');
	}
	
	@Override
	public ReadInfoExtendedData[] createReplicateData(final int n) {
		return new ReadInfoExtendedData[n];
	}
	
	@Override
	public ReadInfoExtendedData[][] createContainerData(final int n) {
		return new ReadInfoExtendedData[n][];
	}

	@Override
	public ReadInfoExtendedData copyData(final ReadInfoExtendedData dataContainer) {
		return new ReadInfoExtendedData(dataContainer);
	}
	
}
