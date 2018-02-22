package lib.data.generator;

import lib.data.LinkedReadArrestCountData;
import lib.data.has.hasLibraryType.LIBRARY_TYPE;
import lib.util.coordinate.Coordinate;

public class BaseCallReadInfoExtendedDataGenerator 
extends AbstractDataGenerator<LinkedReadArrestCountData> {

	@Override
	public LinkedReadArrestCountData createData(LIBRARY_TYPE libraryType, Coordinate coordinate) {
		return new LinkedReadArrestCountData(libraryType, coordinate, (byte)'N');
	}
	
	@Override
	public LinkedReadArrestCountData[] createReplicateData(final int n) {
		return new LinkedReadArrestCountData[n];
	}
	
	@Override
	public LinkedReadArrestCountData[][] createContainerData(final int n) {
		return new LinkedReadArrestCountData[n][];
	}

	@Override
	public LinkedReadArrestCountData copyData(final LinkedReadArrestCountData dataContainer) {
		return new LinkedReadArrestCountData(dataContainer);
	}
	
}
