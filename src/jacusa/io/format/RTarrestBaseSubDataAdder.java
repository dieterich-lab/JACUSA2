package jacusa.io.format;

import java.util.List;

import lib.cli.options.filter.has.BaseSub;
import lib.data.DataContainer;
import lib.data.count.BaseSub2BCC;
import lib.data.count.basecall.BaseCallCount;
import lib.data.result.Result;
import lib.io.InputOutput;
import lib.io.format.bed.DataAdder;

/**
 * This class corresponds to the two columns of a BEDlike output file that contains base call count  
 * information for read through and read arrest reads.
 */
public class RTarrestBaseSubDataAdder implements DataAdder {

	private final BaseCallCount.AbstractParser bccParser;
	private final List<BaseSub> baseSubs; 
	private final DataAdder dataAdder;
	
	public RTarrestBaseSubDataAdder(
			final BaseCallCount.AbstractParser bccParser, 
			final List<BaseSub> baseSubs, 
			final DataAdder dataAdder) {		
		
		this.bccParser 	= bccParser;
		this.baseSubs 	= baseSubs;
		this.dataAdder 	= dataAdder;
	}

	@Override
	public void addHeader(StringBuilder sb, int condI, int replicateI) {
		dataAdder.addHeader(sb, condI, replicateI);
	}

	@Override
	public void addData(StringBuilder sb, int valueIndex, int condI, int replicateI, Result result) {
		final BaseSub baseSub 	= baseSubs.get(valueIndex);
		final DataContainer container 	= result.getParellelData().getDataContainer(condI, replicateI);
		addBaseCallCount(sb, baseSub, container.getArrestBaseSub2BCC());
		addBaseCallCount(sb, baseSub, container.getThroughBaseSub2BCC());
	}
	
	private void addBaseCallCount(
			final StringBuilder sb, 
			final BaseSub baseSub, 
			final BaseSub2BCC baseSubCount) {

		BaseCallCount bcc = baseSubCount.get(baseSub);
		if (bcc == null) {
			bcc = BaseCallCount.EMPTY;
		}
		sb.append(InputOutput.FIELD_SEP);
		sb.append(bccParser.wrap(bcc));
	}
}
