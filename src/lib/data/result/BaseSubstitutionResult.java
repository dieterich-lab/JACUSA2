package lib.data.result;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;

import lib.cli.options.filter.has.BaseSub;
import lib.cli.options.filter.has.HasReadSubstitution;
import lib.data.DataContainer;
import lib.data.ParallelData;
import lib.data.count.BaseSub2BaseCallCount;
import lib.data.fetcher.Fetcher;
import lib.io.InputOutput;
import lib.util.Info;

/**
 * TODO
 */
public class BaseSubstitutionResult implements Result {

	private static final long serialVersionUID = 1L;

	private final MultiStatResult multiResult;
	

	public BaseSubstitutionResult(
			final SortedSet<BaseSub> baseSubs,
			final Fetcher<BaseSub2BaseCallCount> fetcher,
			final Result result) {
		
		if (result.getValuesIndex().size() > 1 || result.getValuesIndex().isEmpty()) {
			throw new IllegalStateException("Multi-level stratification not supported");
		}
		
		// get all observed base substitutions
		final DataContainer container 				= result.getParellelData().getCombPooledData();
		final BaseSub2BaseCallCount bsc 	= fetcher.fetch(container);

		// store valueIndex 2 base substitution
		final Map<Integer, BaseSub> value2bs 	= new HashMap<>();

		multiResult = new MultiStatResult(result);
		for (final BaseSub baseSub : baseSubs) {
			if (bsc.get(baseSub).getCoverage() > 0) {
				final int newValueIndex = multiResult.addStat(Double.NaN);
				value2bs.put(newValueIndex, baseSub);
			}
		}
		
		// add field for stratification
		final String key = HasReadSubstitution.READ_SUB;
		for (final int valueIndex : multiResult.getValuesIndex()) {
			final String value = valueIndex == Result.TOTAL ? 
					Character.toString(InputOutput.EMPTY_FIELD) : value2bs.get(valueIndex).toString();
			getResultInfo(valueIndex).add(key, value);
		}
	}
	
	@Override
	public ParallelData getParellelData() {
		return multiResult.getParellelData();
	}

	@Override
	public void setFiltered(boolean isFiltered) {
		multiResult.setFiltered(isFiltered);
	}

	@Override
	public boolean isFiltered() {
		return multiResult.isFiltered();
	}

	@Override
	public SortedSet<Integer> getValuesIndex() {
		return multiResult.getValuesIndex();
	}
	
	@Override
	public int getValueSize() {
		return multiResult.getValueSize();
	}
	

	@Override
	public Info getFilterInfo() {
		return multiResult.getFilterInfo();
	}
	
	@Override
	public Info getFilterInfo(int valueIndex) {
		return multiResult.getFilterInfo(valueIndex);
	}
	
	@Override
	public Info getResultInfo() {
		return multiResult.getResultInfo();
	}
	
	@Override
	public Info getResultInfo(int valueIndex) {
		return multiResult.getResultInfo(valueIndex);
	}
	
	@Override
	public double getStat() {
		return multiResult.getStat();
	}

	@Override
	public double getStat(int valueIndex) {
		return multiResult.getStat(valueIndex);
	}
	
}
