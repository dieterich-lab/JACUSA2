package jacusa.io.format;

import java.util.List;

import lib.data.result.Result;
import lib.io.format.bed.DataAdder;
/**
 * This class allow to combine and display multiple objects that implement DataAdder in sequential order.
 */
public class CombinedDataAdder implements DataAdder {

	private final List<DataAdder> dataAdders;
	
	public CombinedDataAdder(final List<DataAdder> dataAdders) {
		this.dataAdders = dataAdders;
	}
	
	@Override
	public void addHeader(StringBuilder sb, int conditionIndex, int replicateIndex) {
		for  (final DataAdder dataAdder : dataAdders) {
			dataAdder.addHeader(sb, conditionIndex, replicateIndex);
		}
	}
	
	@Override
	public void addData(StringBuilder sb, int valueIndex, int conditionIndex, int replicateIndex, Result result) {
		for  (final DataAdder dataAdder : dataAdders) {
			dataAdder.addData(sb, valueIndex, conditionIndex, replicateIndex, result);
		}
	}

}
