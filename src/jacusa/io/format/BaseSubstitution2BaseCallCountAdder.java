package jacusa.io.format;

import java.util.List;

import lib.cli.options.filter.has.BaseSub;
import lib.data.DataContainer;
import lib.data.count.basecall.BaseCallCount;
import lib.data.result.Result;
import lib.io.InputOutput;
import lib.io.format.bed.DataAdder;

/**
 * This class corresponds to the column of a BEDlike output file that contains read base substitution 
 * information, namely the base call counts that correspond to a specific base substitution.
 */
public class BaseSubstitution2BaseCallCountAdder implements DataAdder {

	private final BaseCallCount.AbstractParser bccParser;
	private final List<BaseSub> baseSubs; 
	private final DataAdder dataAdder;
	
	public BaseSubstitution2BaseCallCountAdder(
			final BaseCallCount.AbstractParser bccParser,
			final List<BaseSub> baseSubs,
			final DataAdder dataAdder) {
		
		this.bccParser 	= bccParser;
		this.baseSubs 	= baseSubs;
		this.dataAdder 	= dataAdder;
	}

	@Override
	public void addHeader(StringBuilder sb, int conditionIndex, int replicateIndex) {
		dataAdder.addHeader(sb, conditionIndex, replicateIndex);
	}

	@Override
	public void addData(StringBuilder sb, int valueIndex, int conditionIndex, int replicateIndex, Result result) {
		final BaseSub baseSub = baseSubs.get(valueIndex);
		final DataContainer container = result.getParellelData().getDataContainer(conditionIndex, replicateIndex);
		BaseCallCount bcc = container.getBaseSub2BCC().get(baseSub);
		if (bcc == null) {
			bcc = BaseCallCount.EMPTY;
		}
		sb.append(InputOutput.FIELD_SEP);
		sb.append(bccParser.wrap(bcc));
	}
	
}
