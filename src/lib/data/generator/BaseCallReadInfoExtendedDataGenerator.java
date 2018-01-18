package lib.data.generator;

import lib.data.ReadInfoExtendedData;
import lib.data.has.hasLibraryType.LIBRARY_TYPE;
import lib.util.coordinate.Coordinate;

public class BaseCallReadInfoExtendedDataGenerator implements
		DataGenerator<ReadInfoExtendedData> {

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
	
	@Override
	public ReadInfoExtendedData[] copyReplicateData(final ReadInfoExtendedData[] dataContainer) {
		ReadInfoExtendedData[] ret = createReplicateData(dataContainer.length);
		for (int i = 0; i < dataContainer.length; ++i) {
			ret[i] = new ReadInfoExtendedData(dataContainer[i]);
		}
		return ret;
	}
	
	@Override
	public ReadInfoExtendedData[][] copyContainerData(final ReadInfoExtendedData[][] dataContainer) {
		ReadInfoExtendedData[][] ret = createContainerData(dataContainer.length);
		for (int i = 0; i < dataContainer.length; ++i) {
			ret[i] = new ReadInfoExtendedData[dataContainer[i].length];
			for (int j = 0; j < dataContainer[i].length; ++j) {
				ret[i][j] = new ReadInfoExtendedData(dataContainer[i][j]);
			}	
		}

		return ret;
	}

	
}
