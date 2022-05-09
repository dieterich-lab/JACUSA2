package jacusa.io.format;

import lib.data.result.Result;
import lib.io.format.bed.DataAdder;

/**
 * This class allows to display stratified data.
 */
public class StratifiedDataAdder implements DataAdder {

	private final DataAdder dataAdderTotal;
	private final DataAdder dataAdderStratified;

	public StratifiedDataAdder(final DataAdder dataAdderTotal, final DataAdder dataAdderStratified) {

		this.dataAdderTotal = dataAdderTotal;
		this.dataAdderStratified = dataAdderStratified;
	}

	@Override
	public void addHeader(StringBuilder sb, int condI, int replicateI) {
		dataAdderTotal.addHeader(sb, condI, replicateI);
	}

	@Override
	public void addData(StringBuilder sb, int valueIndex, int condI, int replicateI, Result result) {
		if (valueIndex == Result.TOTAL) {
			dataAdderTotal.addData(sb, valueIndex, condI, replicateI, result);
		} else {
			dataAdderStratified.addData(sb, valueIndex, condI, replicateI, result);
		}
	}

}
