package lib.data.generator;

import lib.data.LRTarrestData;
import lib.data.has.hasLibraryType.LIBRARY_TYPE;
import lib.util.coordinate.Coordinate;

public class LRTarrestDataGenerator 
extends AbstractDataGenerator<LRTarrestData> {

	@Override
	public LRTarrestData createData(LIBRARY_TYPE libraryType, Coordinate coordinate) {
		return new LRTarrestData(libraryType, coordinate, (byte)'N');
	}
	
	@Override
	public LRTarrestData[] createReplicateData(final int n) {
		return new LRTarrestData[n];
	}
	
	@Override
	public LRTarrestData[][] createContainerData(final int n) {
		return new LRTarrestData[n][];
	}

	@Override
	public LRTarrestData copyData(final LRTarrestData dataContainer) {
		return new LRTarrestData(dataContainer);
	}
	
}
