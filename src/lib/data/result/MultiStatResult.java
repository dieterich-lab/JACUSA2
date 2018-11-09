package lib.data.result;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import lib.data.ParallelData;
import lib.util.Info;

public class MultiStatResult 
implements Result {

	private static final long serialVersionUID = 1L;
	
	private final Map<Integer, Double> stat;
	private final ParallelData parallelData;
	
	private boolean markedFiltered;
	private final Map<Integer, Info> filterInfo;
	private final Map<Integer, Info> resultInfo;

	protected MultiStatResult(final Map<Integer, Double> stat, final ParallelData parallelData) {
		this.stat = stat;
		this.parallelData = parallelData;
		
		final int n = stat.size();
		markedFiltered = false;
		filterInfo = new HashMap<>(n);
		resultInfo = new HashMap<>(n);
	}

	@Override
	public double getStat(final int value) {
		return stat.get(value);
	}

	@Override
	public ParallelData getParellelData() {
		return parallelData;
	}

	@Override
	public Info getResultInfo(final int value) {
		return resultInfo.get(value);
	}

	@Override
	public Info getFilterInfo(final int value) {
		return filterInfo.get(value);
	}

	@Override
	public void setFiltered(boolean marked) {
		markedFiltered = marked;
	}

	@Override
	public boolean isFiltered() {
		return markedFiltered;
	}

	@Override
	public SortedSet<Integer> getValues() {
		return Collections.unmodifiableSortedSet(new TreeSet<>(stat.keySet()));
	}

	@Override
	public int getValueSize() {
		return stat.size();
	}
	
	@Override
	public Info getFilterInfo() {
		return null; // TODO
	}
	
	@Override
	public Info getResultInfo() {
		return null; // TODO
	}

	@Override
	public double getStat() {
		return Double.NaN; // TODO
	}
	
}
