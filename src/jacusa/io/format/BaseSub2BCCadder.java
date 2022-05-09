package jacusa.io.format;

import java.util.List;

import lib.cli.options.filter.has.BaseSub;
import lib.data.DataContainer;
import lib.data.DataType;
import lib.data.count.BaseSub2BaseCallCount;
import lib.data.count.basecall.BaseCallCount;
import lib.data.result.Result;
import lib.io.InputOutput;
import lib.io.format.bed.DataAdder;

/**
 * This class corresponds to the column of a BEDlike output file that contains
 * read tags information, namely the base call counts that correspond to a
 * specific base substitution.
 */
public class BaseSub2BCCadder implements DataAdder {

	private final BaseCallCount.AbstractParser bccParser;

	private final DataType<BaseSub2BaseCallCount> dataType;
	private final List<BaseSub> baseSubs;
	private final DataAdder dataAdder;

	public BaseSub2BCCadder(final DataType<BaseSub2BaseCallCount> dataType,
			final BaseCallCount.AbstractParser bccParser, final List<BaseSub> baseSubs, final DataAdder dataAdder) {

		this.bccParser = bccParser;
		this.dataType = dataType;
		this.baseSubs = baseSubs;
		this.dataAdder = dataAdder;
	}

	@Override
	public void addHeader(StringBuilder sb, int condI, int replicateI) {
		dataAdder.addHeader(sb, condI, replicateI);
	}

	@Override
	public void addData(StringBuilder sb, int valueIndex, int condI, int replicateI, Result result) {
		final BaseSub baseSub = baseSubs.get(valueIndex);
		final DataContainer container = result.getParellelData().getDataContainer(condI, replicateI);
		BaseCallCount bcc = container.get(dataType).getMap().get(baseSub);
		if (bcc == null) {
			bcc = BaseCallCount.EMPTY;
		}
		sb.append(InputOutput.FIELD_SEP);
		sb.append(bccParser.wrap(bcc));
	}

}
