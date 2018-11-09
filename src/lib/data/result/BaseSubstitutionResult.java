package lib.data.result;

import java.util.SortedSet;

import lib.cli.options.has.HasReadSubstitution.BaseSubstitution;
import lib.data.ParallelData;
import lib.util.Info;

public class BaseSubstitutionResult implements Result {

	private static final long serialVersionUID = 1L;

	private final SortedSet<BaseSubstitution> baseSubs;
	private final Result result;
	
	public BaseSubstitutionResult(
			final SortedSet<BaseSubstitution> baseSubs,
			final Result result) {
		
		this.baseSubs = baseSubs;
		this.result = result;
	}
	
	@Override
	public ParallelData getParellelData() {
		return result.getParellelData();
	}

	@Override
	public Info getResultInfo() {
		return result.getResultInfo();
	}

	@Override
	public Info getFilterInfo() {
		return result.getFilterInfo();
	}

	@Override
	public Info getResultInfo(int valueIndex) {
		return result.getResultInfo(); // TODO
	}

	@Override
	public Info getFilterInfo(int valueIndex) {
		return result.getFilterInfo(); // TODO
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
	public SortedSet<Integer> getValues() {
		/*
		int count = 1 + baseSubs.size();
		return count;
		*/
		return null;
	}

	@Override
	public double getStat() {
		return result.getStat();
	}

	@Override
	public double getStat(int value) {
		return result.getStat(value);
	}

}
