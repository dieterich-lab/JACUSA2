package lib.data.validator.paralleldata;

import java.util.List;

import lib.cli.parameter.ConditionParameter;
import lib.data.DataContainer;
import lib.data.ParallelData;
import lib.data.count.basecall.BaseCallCount;
import lib.data.fetcher.Fetcher;

public class MinCoverageValidator 
implements ParallelDataValidator {

	private final Fetcher<BaseCallCount> bccFetcher;
	private final List<ConditionParameter> conditionParameters;
	
	public MinCoverageValidator(
			final Fetcher<BaseCallCount> bccFetcher,
			final List<ConditionParameter> conditionParameters) {

		this.bccFetcher = bccFetcher;
		this.conditionParameters = conditionParameters;
	}
	
	@Override
	public boolean isValid(final ParallelData parallelData) {
		for (int conditionIndex = 0; conditionIndex < conditionParameters.size(); conditionIndex++) {
			for (final DataContainer container : parallelData.getData(conditionIndex)) {
				final BaseCallCount bcc = bccFetcher.fetch(container);
				if (bcc.getCoverage() < conditionParameters.get(conditionIndex).getMinCoverage()) {
					return false;
				}
			}
		}
		
		return true;
	}
	
}
