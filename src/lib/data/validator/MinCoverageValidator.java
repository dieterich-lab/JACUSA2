package lib.data.validator;

import java.util.List;

import lib.cli.parameter.AbstractConditionParameter;
import lib.data.AbstractData;
import lib.data.ParallelData;
import lib.data.has.hasCoverage;

public class MinCoverageValidator<T extends AbstractData & hasCoverage> 
implements ParallelDataValidator<T> {

	private final List<AbstractConditionParameter<T>> conditionParameters;
	
	public MinCoverageValidator(final List<AbstractConditionParameter<T>> conditionParameters) {
		this.conditionParameters = conditionParameters;
	}
	
	@Override
	public boolean isValid(final ParallelData<T> parallelData) {
		for (int conditionIndex = 0; conditionIndex < conditionParameters.size(); conditionIndex++) {
			for (T data : parallelData.getData(conditionIndex)) {
				if (data.getCoverage() < conditionParameters.get(conditionIndex).getMinCoverage()) {
					return false;
				}
			}
		}
		
		return true;
	}
	
}
