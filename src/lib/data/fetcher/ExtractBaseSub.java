package lib.data.fetcher;

import lib.cli.options.filter.has.BaseSub;
import lib.data.DataContainer;
import lib.data.DataType;
import lib.data.IntegerData;
import lib.data.count.BaseSub2Integer;

public class ExtractBaseSub implements Fetcher<IntegerData> {

	private final BaseSub baseSub;
	private final DataType<BaseSub2Integer> dataType;

	public ExtractBaseSub(final BaseSub baseSub, final DataType<BaseSub2Integer> dataType) {
		this.baseSub = baseSub;
		this.dataType = dataType;
	}

	@Override
	public IntegerData fetch(final DataContainer dataContainer) {
		return dataContainer.get(dataType).getMap().get(baseSub);
	}

}
