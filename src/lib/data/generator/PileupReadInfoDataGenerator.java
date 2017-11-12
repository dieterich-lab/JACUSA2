package lib.data.generator;

import lib.data.PileupReadInfoData;

public class PileupReadInfoDataGenerator implements
		DataGenerator<PileupReadInfoData> {

	@Override
	public PileupReadInfoData createData() {
		return new PileupReadInfoData();
	}
	
	@Override
	public PileupReadInfoData[] createReplicateData(final int n) {
		return new PileupReadInfoData[n];
	}
	
	@Override
	public PileupReadInfoData[][] createContainerData(final int n) {
		return new PileupReadInfoData[n][];
	}

	@Override
	public PileupReadInfoData copyData(final PileupReadInfoData dataContainer) {
		return new PileupReadInfoData(dataContainer);
	}
	
	@Override
	public PileupReadInfoData[] copyReplicateData(final PileupReadInfoData[] dataContainer) {
		PileupReadInfoData[] ret = createReplicateData(dataContainer.length);
		for (int i = 0; i < dataContainer.length; ++i) {
			ret[i] = new PileupReadInfoData(dataContainer[i]);
		}
		return ret;
	}
	
	@Override
	public PileupReadInfoData[][] copyContainerData(final PileupReadInfoData[][] dataContainer) {
		PileupReadInfoData[][] ret = createContainerData(dataContainer.length);
		for (int i = 0; i < dataContainer.length; ++i) {
			ret[i] = new PileupReadInfoData[dataContainer[i].length];
			for (int j = 0; j < dataContainer[i].length; ++j) {
				ret[i][j] = new PileupReadInfoData(dataContainer[i][j]);
			}	
		}

		return ret;
	}

	
}
