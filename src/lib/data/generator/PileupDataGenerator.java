package lib.data.generator;

import lib.data.basecall.PileupData;

public class PileupDataGenerator 
extends AbstractDataGenerator<PileupData> {

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
}
