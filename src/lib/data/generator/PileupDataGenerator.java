package lib.data.generator;

import lib.data.basecall.PileupData;

public class PileupDataGenerator 
implements DataGenerator<PileupData> {

	@Override
	public PileupData createData() {
		return new PileupData();
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
	
	@Override
	public PileupData[] copyReplicateData(final PileupData[] dataContainer) {
		PileupData[] ret = createReplicateData(dataContainer.length);
		for (int i = 0; i < dataContainer.length; ++i) {
			ret[i] = new PileupData(dataContainer[i]);
		}
		return ret;
	}
	
	@Override
	public PileupData[][] copyContainerData(final PileupData[][] dataContainer) {
		PileupData[][] ret = createContainerData(dataContainer.length);
		
		for (int i = 0; i < dataContainer.length; ++i) {
			ret[i] = new PileupData[dataContainer[i].length];
			for (int j = 0; j < dataContainer[i].length; ++j) {
				ret[i][j] = new PileupData(dataContainer[i][j]);
			}	
		}
		
		return ret;
	}
	
}
