package jacusa.io.format;

import java.util.List;

import jacusa.filter.factory.AbstractFilterFactory;
import jacusa.filter.factory.FilterFactory;
import lib.data.DataTypeContainer;
import lib.data.result.Result;
import lib.io.format.bed.DataAdder;
import lib.util.Util;

public class FilterDebugAdder implements DataAdder {

	private final List<AbstractFilterFactory> filterFactories;
	
	private final DataAdder dataAdder;
	
	public FilterDebugAdder(
			final List<AbstractFilterFactory> filterFactories,
			final DataAdder dataAdder) {
		
		this.filterFactories = filterFactories;
		this.dataAdder = dataAdder;
	}

	@Override
	public void addHeader(StringBuilder sb, int conditionIndex, int replicateIndex) {
		dataAdder.addHeader(sb, conditionIndex, replicateIndex);

		for (final FilterFactory filterFactory : filterFactories) {
			sb.append(Util.FIELD_SEP);
			sb.append(filterFactory.getC());
			sb.append(conditionIndex + 1);
			sb.append(replicateIndex + 1);
		}
	}

	@Override
	public void addData(StringBuilder sb, int valueIndex, int conditionIndex, int replicateIndex, Result result) {
		dataAdder.addData(sb, valueIndex, conditionIndex, replicateIndex, result);
		
		final DataTypeContainer container = result.getParellelData().getDataContainer(conditionIndex, replicateIndex);
		for (final FilterFactory filterFactory : filterFactories) {
			sb.append(Util.FIELD_SEP);
			filterFactory.addFilteredData(sb, container);
		}
	}
	
}
