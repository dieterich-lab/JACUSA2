package lib.data.generator;

import lib.data.BaseCallReadInfoData;

public class BaseCallReadInfoDataGenerator implements
		DataGenerator<BaseCallReadInfoData> {

	@Override
	public BaseCallReadInfoData createData() {
		return new BaseCallReadInfoData();
	}
	
	@Override
	public BaseCallReadInfoData[] createReplicateData(final int n) {
		return new BaseCallReadInfoData[n];
	}
	
	@Override
	public BaseCallReadInfoData[][] createContainerData(final int n) {
		return new BaseCallReadInfoData[n][];
	}

	@Override
	public BaseCallReadInfoData copyData(final BaseCallReadInfoData dataContainer) {
		return new BaseCallReadInfoData(dataContainer);
	}
	
	@Override
	public BaseCallReadInfoData[] copyReplicateData(final BaseCallReadInfoData[] dataContainer) {
		BaseCallReadInfoData[] ret = createReplicateData(dataContainer.length);
		for (int i = 0; i < dataContainer.length; ++i) {
			ret[i] = new BaseCallReadInfoData(dataContainer[i]);
		}
		return ret;
	}
	
	@Override
	public BaseCallReadInfoData[][] copyContainerData(final BaseCallReadInfoData[][] dataContainer) {
		BaseCallReadInfoData[][] ret = createContainerData(dataContainer.length);
		for (int i = 0; i < dataContainer.length; ++i) {
			ret[i] = new BaseCallReadInfoData[dataContainer[i].length];
			for (int j = 0; j < dataContainer[i].length; ++j) {
				ret[i][j] = new BaseCallReadInfoData(dataContainer[i][j]);
			}	
		}

		return ret;
	}

	
}
