package lib.data.validator.paralleldata;

import java.util.List;

import lib.cli.parameter.ConditionParameter;
import lib.data.DataContainer;
import lib.data.DataType;
import lib.data.ParallelData;
import lib.data.count.basecall.BaseCallCount;

public class MinCoverageValidator 
implements ParallelDataValidator {

	// TODO rename this to dataType coverage
	private final DataType<BaseCallCount> dataType;
	private final List<ConditionParameter> conditionParameters;
	
	public MinCoverageValidator(
			final DataType<BaseCallCount> dataType,
			final List<ConditionParameter> conditionParameters) {

		this.dataType = dataType;
		this.conditionParameters = conditionParameters;
	}
	
	@Override
	public boolean isValid(final ParallelData parallelData) {
		for (int condI = 0; condI < conditionParameters.size(); condI++) {
			for (final DataContainer container : parallelData.getData(condI)) {
				final BaseCallCount bcc = container.get(dataType);
				if (bcc.getCoverage() < conditionParameters.get(condI).getMinCoverage()) {
					return false;
				}
			}
		}
		
		return true;
	}
	
}
