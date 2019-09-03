package jacusa.io.format;

import java.util.List;

import jacusa.filter.factory.FilterFactory;
import lib.data.DataContainer;
import lib.data.result.Result;
import lib.io.InputOutput;
import lib.io.format.bed.DataAdder;

/**
 * This class allows to add additional filter related data to the output. 
 * All chosen filters will be represented by their unique id.
 */
public class FilterDebugAdder implements DataAdder {

	private final List<FilterFactory> filterFactories;
	
	private final DataAdder dataAdder;
	
	public FilterDebugAdder(
			final List<FilterFactory> filterFactories,
			final DataAdder dataAdder) {
		
		this.filterFactories = filterFactories;
		this.dataAdder = dataAdder;
	}

	@Override
	public void addHeader(StringBuilder sb, int conditionIndex, int replicateIndex) {
		dataAdder.addHeader(sb, conditionIndex, replicateIndex);

		for (final FilterFactory filterFactory : filterFactories) {
			sb.append(InputOutput.FIELD_SEP);
			sb.append(filterFactory.getID());
			sb.append(conditionIndex + 1);
			sb.append(replicateIndex + 1);
		}
	}

	@Override
	public void addData(StringBuilder sb, int valueIndex, int conditionIndex, int replicateIndex, Result result) {
		dataAdder.addData(sb, valueIndex, conditionIndex, replicateIndex, result);
		
		final DataContainer container = result.getParellelData().getDataContainer(conditionIndex, replicateIndex);
		for (final FilterFactory filterFactory : filterFactories) {
			sb.append(InputOutput.FIELD_SEP);
			filterFactory.addFilteredData(sb, container);
		}
	}
	
}
