package jacusa.io.format;

import java.util.List;

import jacusa.JACUSA;
import lib.cli.options.has.HasReadSubstitution;
import lib.cli.options.has.HasReadSubstitution.BaseSubstitution;
import lib.data.DataTypeContainer;
import lib.data.count.basecall.BaseCallCount;
import lib.data.count.basecall.UnmodifiableBaseCallCount;
import lib.data.result.Result;
import lib.io.format.bed.DataAdder;
import lib.util.Util;

public class BaseSubstitutionAdder implements DataAdder {

	private final BaseCallCount.AbstractParser bccParser;
	private final List<BaseSubstitution> baseSubs; 
	private final DataAdder dataAdder;
	
	private final BaseCallCount emptyBcc;
	
	public BaseSubstitutionAdder(
			final BaseCallCount.AbstractParser bccParser, 
			final List<BaseSubstitution> baseSubs, 
			final DataAdder dataAdder) {		
		
		this.bccParser = bccParser;
		this.baseSubs = baseSubs;
		this.dataAdder = dataAdder;
		
		emptyBcc = new UnmodifiableBaseCallCount(JACUSA.bccFactory.create());
	}

	@Override
	public void addHeader(StringBuilder sb, int conditionIndex, int replicateIndex) {
		sb.append(Util.FIELD_SEP);
		sb.append(HasReadSubstitution.READ_SUB);
		sb.append(conditionIndex + 1);
		sb.append(replicateIndex + 1);

		dataAdder.addHeader(sb, conditionIndex, replicateIndex);

		sb.append(Util.FIELD_SEP);
		sb.append(HasReadSubstitution.READ_SUB_BASES);
		sb.append(conditionIndex + 1);
		sb.append(replicateIndex + 1);
	}

	@Override
	public void addData(StringBuilder sb, int valueIndex, int conditionIndex, int replicateIndex, Result result) {
		final DataTypeContainer container = result.getParellelData().getDataContainer(conditionIndex, replicateIndex);
		
		sb.append(Util.FIELD_SEP);
		sb.append(baseSubs.get(valueIndex).toString());
		
		dataAdder.addData(sb, valueIndex, conditionIndex, replicateIndex, result);
		final BaseSubstitution baseSub = baseSubs.get(valueIndex);
		BaseCallCount bcc = container.getBaseSubstitutionCount().get(baseSub);
		if (bcc == null) {
			bcc = emptyBcc;
		}
		sb.append(Util.FIELD_SEP);
		sb.append(bccParser.wrap(bcc));
	}
	
}
