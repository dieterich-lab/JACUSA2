package lib.data.result;

import java.util.SortedSet;
import java.util.TreeSet;

import lib.cli.options.filter.has.HasReadSubstitution.BaseSubstitution;
import lib.data.DataContainer;
import lib.data.ParallelData;
import lib.data.count.BaseSubstitutionCount;
import lib.util.Info;

public class BaseSubstitutionResult implements Result {

	private static final long serialVersionUID = 1L;

	private final Result result;
	
	private SortedSet<Integer> valuesIndex;
	
	public BaseSubstitutionResult(
			final SortedSet<BaseSubstitution> baseSubs,
			final Result result) {
		
		this.result = result;

		final DataContainer container = result.getParellelData().getCombinedPooledData();
		final BaseSubstitutionCount bsc = container.getBaseSubstitutionCount();
		valuesIndex = new TreeSet<>();
		valuesIndex.addAll(result.getValueIndex());
		valuesIndex.addAll(process(baseSubs, bsc));
	}
	
	public static SortedSet<Integer> process(
			final SortedSet<BaseSubstitution> baseSubs, 
			final BaseSubstitutionCount baseSubstitutionCount) {
		
		final SortedSet<Integer> valuesIndex = new TreeSet<>();
		int baseSubId = 0;
		for (final BaseSubstitution baseSub : baseSubs) {
			if (baseSubstitutionCount.get(baseSub).getCoverage() > 0) {
				valuesIndex.add(baseSubId);
			}
			baseSubId++;
		}
		return valuesIndex;
	}
	
	@Override
	public ParallelData getParellelData() {
		return result.getParellelData();
	}

	@Override
	public void setFiltered(boolean isFiltered) {
		result.setFiltered(isFiltered);
	}

	@Override
	public boolean isFiltered() {
		return result.isFiltered();
	}

	@Override
	public SortedSet<Integer> getValueIndex() {
		return valuesIndex;
	}
	
	@Override
	public int getValueSize() {
		return valuesIndex.size();
	}
	
	@Override
	public double getStat(int valueIndex) {
		return result.getStat(valueIndex);
	}

	@Override
	public Info getFilterInfo() {
		return result.getFilterInfo();
	}
	
	@Override
	public Info getFilterInfo(int valueIndex) {
		return result.getFilterInfo(valueIndex);
	}
	
	@Override
	public Info getResultInfo() {
		return result.getResultInfo();
	}
	
	@Override
	public Info getResultInfo(int valueIndex) {
		return result.getResultInfo(valueIndex);
	}
	
	@Override
	public double getStat() {
		return result.getStat();
	}
	
}
