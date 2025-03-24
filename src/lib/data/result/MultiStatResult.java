package lib.data.result;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import lib.data.ParallelData;
import lib.util.ExtendedInfo;
import lib.util.FilterInfo;

// FIXME rework multiple results add ParallelData / DataContainer to 1:n ParallelData / DataContainer/Results

/**
 * Represents a structured result Object with multiple stat values and own FilterInfo and ExtendedInfo fields.
 */
public class MultiStatResult implements Result {
	
	private static final long serialVersionUID = 1L;
	
	private final SortedMap<Integer, Double> value2stat;
	private final ParallelData parallelData;
	
	private boolean markedFiltered; 
	private final Map<Integer, FilterInfo> filterInfos;
	private final Map<Integer, ExtendedInfo> resultInfos;
	
	public MultiStatResult(final ParallelData parallelData) {
		value2stat 			= new TreeMap<>();
		markedFiltered 		= false;
		filterInfos			= new HashMap<>();
		resultInfos			= new HashMap<>();
		this.parallelData	= parallelData;
	}
	
	@Override
	public double getScore(final int valueIndex) {
		return value2stat.get(valueIndex);
	}
	
	@Override
	public ParallelData getParellelData() {
		return parallelData;
	}
	
	@Override
	public ExtendedInfo getResultInfo(final int valueIndex) {
		return resultInfos.get(valueIndex);
	}
	
	@Override
	public FilterInfo getFilterInfo(final int valueIndex) {
		return filterInfos.get(valueIndex);
	}
	
	@Override
	public void setFiltered(boolean marked) {
		markedFiltered = marked;
	}
	
	@Override
	public SortedSet<Integer> getValueIndexes() {
		return new TreeSet<>(value2stat.keySet());
	}
	
	@Override
	public boolean isFiltered() {
		return markedFiltered;
	}
	
	@Override
	public int getValueSize() {
		return value2stat.size();
	}
	
	@Override
	public FilterInfo getFilterInfo() {
		if (value2stat.isEmpty()) {
			return null;
		}
		final int value = value2stat.firstKey();
		return filterInfos.get(value);			
	}
	
	@Override
	public ExtendedInfo getResultInfo() {
		if (value2stat.size() == 0) {
			return null;
		}
		final int value = value2stat.firstKey();
		return resultInfos.get(value);
	}
	
	@Override
	public double getScore() {
		if (value2stat.size() == 0) {
			return Double.NaN;
		}
		final int value = value2stat.firstKey();
		return value2stat.get(value);
	}

	private int getNewValueIndex() {
		if (value2stat.size() == 0) {
			return 0;
		}
		
		return value2stat.lastKey() + 1;
	}
	
	public int addStat(final double stat) {
		final int newValueIndex = getNewValueIndex();
		value2stat.put(newValueIndex, stat);
		filterInfos.put(newValueIndex, new FilterInfo());
		resultInfos.put(newValueIndex, new ExtendedInfo());

		return newValueIndex;
	}
	
}
