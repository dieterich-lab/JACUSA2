package jacusa.io.format.call;

import jacusa.filter.factory.AbstractFilterFactory;
import lib.cli.parameter.AbstractParameter;
import lib.data.AbstractData;
import lib.data.has.HasPileupCount;
import lib.data.has.filter.HasBaseCallCountFilterData;
import lib.data.result.Result;
import lib.data.result.hasStatistic;

public class BED6callDebugWriter<T extends AbstractData & HasPileupCount & HasBaseCallCountFilterData, R extends Result<T> & hasStatistic> 
extends BED6callResultWriter<T, R> {

	public BED6callDebugWriter(final String filename, final AbstractParameter<T, R> parameter) {
		super(filename, parameter);
	}
	
	@Override
	protected void addHeaderConditionData(StringBuilder sb, int conditionIndex, int replicateIndex) {
		super.addHeaderConditionData(sb, conditionIndex, replicateIndex);

		for (final AbstractFilterFactory<?> filterFactory : getParameter().getFilterConfig().getFilterFactories()) {
			sb.append(SEP);
			sb.append(filterFactory.getC());
			sb.append(conditionIndex + 1);
			sb.append(replicateIndex + 1);
		}
	}
	
	@Override
	protected void addResultReplicateData(StringBuilder sb, T data) {
		super.addResultReplicateData(sb, data);
		for (final AbstractFilterFactory<T> filterFactory : getParameter().getFilterConfig().getFilterFactories()) {
			sb.append(SEP);
			filterFactory.addFilteredData(sb, data);
		}
	}
	
	@Override
	protected String getFieldName() {
		return "call";
	}
	
}