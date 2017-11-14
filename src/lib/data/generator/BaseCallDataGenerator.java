package lib.data.generator;

import lib.data.BaseCallData;

public class BaseCallDataGenerator
extends AbstractDataGenerator<BaseCallData> {

	@Override
	public BaseCallData copyData(final BaseCallData data) {
		return new BaseCallData(data);
	}	
	
	@Override
	public BaseCallData[][] createContainerData(int n) {
		return new BaseCallData[n][];
	}
	
	@Override
	public BaseCallData createData() {
		return new BaseCallData();
	}
	
	@Override
	public BaseCallData[] createReplicateData(int n) {
		return new BaseCallData[n];
	}
	
}
