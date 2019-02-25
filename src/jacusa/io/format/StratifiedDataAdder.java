package jacusa.io.format;

import lib.data.result.Result;
import lib.io.format.bed.DataAdder;

/**
 * This class allows to display stratified data.
 */
public class StratifiedDataAdder implements DataAdder {

	private final DataAdder dataAdderTotal;
	private final DataAdder dataAdderStratified;
	
	public StratifiedDataAdder(
			final DataAdder dataAdderTotal,
			final DataAdder dataAdderStratified) {		
		
		this.dataAdderTotal = dataAdderTotal;
		this.dataAdderStratified = dataAdderStratified;
	}

	@Override
	public void addHeader(StringBuilder sb, int conditionIndex, int replicateIndex) {
		dataAdderTotal.addHeader(sb, conditionIndex, replicateIndex);
	}

	@Override
	public void addData(StringBuilder sb, int valueIndex, int conditionIndex, int replicateIndex, Result result) {
		if (valueIndex >= 0 ) {
			dataAdderStratified.addData(sb, valueIndex, conditionIndex, replicateIndex, result);
		} else {
			dataAdderTotal.addData(sb, valueIndex, conditionIndex, replicateIndex, result);	
		}
	}
	
}
