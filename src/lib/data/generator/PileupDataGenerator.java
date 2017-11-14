package lib.data.generator;

import lib.data.PileupData;
import lib.data.has.hasLibraryType.LIBRARY_TYPE;
import lib.util.Coordinate;

public class PileupDataGenerator 
extends AbstractDataGenerator<PileupData> {

	@Override
	public PileupData createData(LIBRARY_TYPE libraryType, final Coordinate coordinate) {
		return new PileupData(libraryType, coordinate);
	}

	@Override
	public PileupData[] createReplicateData(final int n) {
		return new PileupData[n];
	}

	@Override
	public PileupData[][] createContainerData(final int n) {
		return new PileupData[n][];
	}

	@Override
	public PileupData copyData(final PileupData dataContainer) {
		return new PileupData(dataContainer);
	}
}
