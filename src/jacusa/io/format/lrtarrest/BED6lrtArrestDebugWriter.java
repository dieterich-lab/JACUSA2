package jacusa.io.format.lrtarrest;

import jacusa.filter.factory.AbstractFilterFactory;
import lib.cli.parameter.AbstractParameter;
import lib.data.AbstractData;
import lib.data.has.HasBaseCallCount;
import lib.data.has.HasLRTarrestCount;
import lib.data.has.HasReferenceBase;
import lib.data.has.filter.HasRefPos2BaseCallCountFilterData;
import lib.data.result.Result;
import lib.data.result.hasStatistic;
import lib.io.ResultWriter;

public class BED6lrtArrestDebugWriter<T extends AbstractData & HasBaseCallCount & HasReferenceBase & HasLRTarrestCount & HasRefPos2BaseCallCountFilterData, R extends Result<T> & hasStatistic>
extends BED6lrtArrestResultWriter2<T, R> implements ResultWriter<T, R> {

	protected BED6lrtArrestDebugWriter(final String filename,
			final AbstractParameter<T, R> parameter) {
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

}
