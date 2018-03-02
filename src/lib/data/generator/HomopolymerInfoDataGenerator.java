package lib.data.generator;

import lib.data.HomopolymerInfoData;
import lib.data.has.hasLibraryType.LIBRARY_TYPE;
import lib.util.coordinate.Coordinate;

@Deprecated
public class HomopolymerInfoDataGenerator
extends AbstractDataGenerator<HomopolymerInfoData> {

	@Override
	public HomopolymerInfoData copyData(final HomopolymerInfoData data) {
		return new HomopolymerInfoData(data);
	}	
	
	@Override
	public HomopolymerInfoData[][] createContainerData(int n) {
		return new HomopolymerInfoData[n][];
	}
	
	@Override
	public HomopolymerInfoData createData(LIBRARY_TYPE libraryType, Coordinate coordinate) {
		return new HomopolymerInfoData(libraryType, coordinate);
	}
	
	@Override
	public HomopolymerInfoData[] createReplicateData(int n) {
		return new HomopolymerInfoData[n];
	}
	
}
