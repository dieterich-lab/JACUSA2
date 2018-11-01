package lib.data.validator.paralleldata;

import java.util.List;

import lib.cli.parameter.AbstractConditionParameter;
import lib.data.DataTypeContainer;
import lib.data.ParallelData;
import lib.data.cache.fetcher.Fetcher;
import lib.data.count.basecall.BaseCallCount;

public class MinCoverageValidator 
implements ParallelDataValidator {

	private final Fetcher<BaseCallCount> bccFetcher;
	private final List<AbstractConditionParameter> conditionParameters;
	
	public MinCoverageValidator(
			final Fetcher<BaseCallCount> bccFetcher,
			final List<AbstractConditionParameter> conditionParameters) {

		this.bccFetcher = bccFetcher;
		this.conditionParameters = conditionParameters;
	}
	
	@Override
	public boolean isValid(final ParallelData parallelData) {
		for (int conditionIndex = 0; conditionIndex < conditionParameters.size(); conditionIndex++) {
			for (final DataTypeContainer container : parallelData.getData(conditionIndex)) {
				final BaseCallCount bcc = bccFetcher.fetch(container);
				if (bcc.getCoverage() < conditionParameters.get(conditionIndex).getMinCoverage()) {
					return false;
				}
			}
		}
		
		return true;
	}
	
}
