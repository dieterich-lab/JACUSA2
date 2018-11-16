package jacusa.io.format;

import java.util.List;

import lib.cli.options.has.HasReadSubstitution.BaseSubstitution;
import lib.data.DataTypeContainer;
import lib.data.count.BaseSubstitutionCount;
import lib.data.count.basecall.BaseCallCount;
import lib.data.result.Result;
import lib.io.format.bed.DataAdder;
import lib.util.Util;

public class RTarrestBaseSubstitutionDataAdder implements DataAdder {

	private final BaseCallCount.AbstractParser bccParser;
	private final List<BaseSubstitution> baseSubs; 
	private final DataAdder dataAdder;
	
	public RTarrestBaseSubstitutionDataAdder(
			final BaseCallCount.AbstractParser bccParser, 
			final List<BaseSubstitution> baseSubs, 
			final DataAdder dataAdder) {		
		
		this.bccParser = bccParser;
		this.baseSubs = baseSubs;
		this.dataAdder = dataAdder;
	}

	@Override
	public void addHeader(StringBuilder sb, int conditionIndex, int replicateIndex) {
		dataAdder.addHeader(sb, conditionIndex, replicateIndex);
	}

	@Override
	public void addData(StringBuilder sb, int valueIndex, int conditionIndex, int replicateIndex, Result result) {
		final BaseSubstitution baseSub = baseSubs.get(valueIndex);
		final DataTypeContainer container = result.getParellelData().getDataContainer(conditionIndex, replicateIndex);
		addBaseCallCount(sb, baseSub, container.getArrestBaseSubstitutionCount());
		addBaseCallCount(sb, baseSub, container.getThroughBaseSubstitutionCount());
	}
	
	private void addBaseCallCount(
			final StringBuilder sb, 
			final BaseSubstitution baseSub, 
			final BaseSubstitutionCount baseSubstitutionCount) {

		BaseCallCount bcc = baseSubstitutionCount.get(baseSub);
		if (bcc == null) {
			bcc = BaseCallCount.EMPTY;
		}
		sb.append(Util.FIELD_SEP);
		sb.append(bccParser.wrap(bcc));
	}
}
