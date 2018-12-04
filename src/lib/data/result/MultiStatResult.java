package lib.data.result;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeSet;

import lib.data.ParallelData;
import lib.util.Info;

public class MultiStatResult 
implements Result {

	private static final long serialVersionUID = 1L;
	private static final int MAIN_VALUE_INDEX = -1;
	
	private final SortedMap<Integer, Double> stat;
	private final ParallelData parallelData;
	
	private boolean markedFiltered;
	private final Map<Integer, Info> filterInfo;
	private final Map<Integer, Info> resultInfo;

	public MultiStatResult(final SortedMap<Integer, Double> stat, final ParallelData parallelData) {
		this.stat = stat;
		this.parallelData = parallelData;
		
		final int n = stat.size();
		markedFiltered = false;
		filterInfo = new HashMap<>(n);
		resultInfo = new HashMap<>(n);
		for (final int valueIndex : stat.keySet()) {
			filterInfo.put(valueIndex, new Info());
			resultInfo.put(valueIndex, new Info());			
		}
	}

	@Override
	public double getStat(final int valueIndex) {
		return stat.get(valueIndex);
	}

	@Override
	public ParallelData getParellelData() {
		return parallelData;
	}

	@Override
	public Info getResultInfo(final int valueIndex) {
		return resultInfo.get(valueIndex);
	}

	@Override
	public Info getFilterInfo(final int valueIndex) {
		return filterInfo.get(valueIndex);
	}

	@Override
	public void setFiltered(boolean marked) {
		markedFiltered = marked;
	}

	@Override
	public SortedSet<Integer> getValueIndex() {
		return new TreeSet<>(stat.keySet());
	}
	
	@Override
	public boolean isFiltered() {
		return markedFiltered;
	}

	@Override
	public int getValueSize() {
		return stat.size();
	}
	
	@Override
	public Info getFilterInfo() {
		return filterInfo.get(MAIN_VALUE_INDEX);
	}
	
	@Override
	public Info getResultInfo() {
		return resultInfo.get(MAIN_VALUE_INDEX);
	}
	
	@Override
	public double getStat() {
		return stat.get(MAIN_VALUE_INDEX);
	}
	
}
