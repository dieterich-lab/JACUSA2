package lib.data.generator;

import lib.data.RTarrestData;
import lib.data.has.HasLibraryType.LIBRARY_TYPE;
import lib.util.coordinate.Coordinate;

public class RTarrestDataGenerator 
extends AbstractDataGenerator<RTarrestData> {

	@Override	public RTarrestData createData(LIBRARY_TYPE libraryType, Coordinate coordinate) {
		return new RTarrestData(libraryType, coordinate, (byte)'N');
	}
	
	@Override
	public RTarrestData[] createReplicateData(final int n) {
		return new RTarrestData[n];
	}
	
	@Override
	public RTarrestData[][] createContainerData(final int n) {
		return new RTarrestData[n][];
	}

	@Override
	public RTarrestData copyData(final RTarrestData dataContainer) {
		return new RTarrestData(dataContainer);
	}
	
}
