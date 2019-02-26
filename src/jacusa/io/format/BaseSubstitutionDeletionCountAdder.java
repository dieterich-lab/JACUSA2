package jacusa.io.format;

import java.util.List;

import lib.cli.options.filter.has.HasReadSubstitution.BaseSubstitution;
import lib.data.DataContainer;
import lib.data.result.Result;
import lib.io.InputOutput;
import lib.io.format.bed.DataAdder;

/**
 * This class corresponds to the a column of a BEDlike output file that contains read base substitution 
 * information, namely the base call counts that correspond to a specific base substitution.
 */
public class BaseSubstitutionDeletionCountAdder implements DataAdder {

	private final List<BaseSubstitution> baseSubs; 
	private final DataAdder dataAdder;
	
	public BaseSubstitutionDeletionCountAdder(

			final List<BaseSubstitution> baseSubs, 
			final DataAdder dataAdder) {		
		
		this.baseSubs 	= baseSubs;
		this.dataAdder 	= dataAdder;
	}

	@Override
	public void addHeader(StringBuilder sb, int conditionIndex, int replicateIndex) {
		dataAdder.addHeader(sb, conditionIndex, replicateIndex);
	}

	@Override
	public void addData(StringBuilder sb, int valueIndex, int conditionIndex, int replicateIndex, Result result) {
		final BaseSubstitution baseSub = baseSubs.get(valueIndex);
		final DataContainer container = result.getParellelData().getDataContainer(conditionIndex, replicateIndex);

		final int deletionCount = container.getBaseSubstitution2DeletionCount().get(baseSub).getValue();
		final int coverage		= container.getBaseSubstitution2Coverage().get(baseSub).getValue();
		
		sb.append(InputOutput.FIELD_SEP);
		sb.append(deletionCount);
		sb.append(InputOutput.VALUE_SEP);
		sb.append(coverage);

	}
	
}
