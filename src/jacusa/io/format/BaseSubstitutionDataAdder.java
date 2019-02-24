package jacusa.io.format;

import java.util.List;

import lib.cli.options.filter.has.HasReadSubstitution.BaseSubstitution;
import lib.data.DataContainer;
import lib.data.count.basecall.BaseCallCount;
import lib.data.result.Result;
import lib.io.InputOutput;
import lib.io.format.bed.DataAdder;

public class BaseSubstitutionDataAdder implements DataAdder {

	private final BaseCallCount.AbstractParser bccParser;
	private final List<BaseSubstitution> baseSubs; 
	private final DataAdder dataAdder;
	
	public BaseSubstitutionDataAdder(
			final BaseCallCount.AbstractParser bccParser, 
			final List<BaseSubstitution> baseSubs, 
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
		final BaseSubstitution baseSub = baseSubs.get(valueIndex);
		final DataContainer container = result.getParellelData().getDataContainer(conditionIndex, replicateIndex);
		BaseCallCount bcc = container.getBaseSubstitutionCount().get(baseSub);
		if (bcc == null) {
			bcc = BaseCallCount.EMPTY;
		}
		sb.append(InputOutput.FIELD_SEP);
		sb.append(bccParser.wrap(bcc));
	}
	
}
