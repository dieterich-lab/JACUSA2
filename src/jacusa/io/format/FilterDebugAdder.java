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
	public void addHeader(StringBuilder sb, int condI, int replicateI) {
		dataAdder.addHeader(sb, condI, replicateI);

		for (final FilterFactory filterFactory : filterFactories) {
			sb.append(InputOutput.FIELD_SEP);
			sb.append(filterFactory.getID());
			sb.append(condI + 1);
			sb.append(replicateI + 1);
		}
	}

	@Override
	public void addData(StringBuilder sb, int valueIndex, int condI, int replicateI, Result result) {
		dataAdder.addData(sb, valueIndex, condI, replicateI, result);
		
		final DataContainer container = result.getParellelData().getDataContainer(condI, replicateI);
		for (final FilterFactory filterFactory : filterFactories) {
			sb.append(InputOutput.FIELD_SEP);
			filterFactory.addFilteredData(sb, container);
		}
	}
	
}
