package jacusa.io.format;

import lib.data.DataContainer;
import lib.data.result.Result;
import lib.io.InputOutput;
import lib.io.format.bed.DataAdder;

/**
 * This class corresponds to the a column of a BEDlike output file that contains deletion count 
 * information.
 */
public class DeletionCountDataAdder implements DataAdder {
	
	@Override
	public void addHeader(StringBuilder sb, int conditionIndex, int replicateIndex) {
		sb.append(InputOutput.FIELD_SEP);
		sb.append(InputOutput.DELETION_FIELD);
		sb.append(conditionIndex + 1);
		sb.append(replicateIndex + 1);
	}
	
	@Override
	public void addData(StringBuilder sb, int valueIndex, int conditionIndex, int replicateIndex, Result result) {
		final DataContainer container = result.getParellelData().getDataContainer(conditionIndex, replicateIndex);
		sb.append(InputOutput.FIELD_SEP);
		final int count = container.getDeletionCount().getValue();
		sb.append(count);
	}

}
