package lib.data.result;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import lib.data.ParallelData;
import lib.util.ExtendedInfo;

// FIXME

/**
 * TODO add documentation
 */
public class MultiStatResult implements Result {
	
	private static final long serialVersionUID = 1L;
	
	private final SortedMap<Integer, Double> value2stat;
	private final ParallelData parallelData;
	
	private boolean markedFiltered;
	private final Map<Integer, ExtendedInfo> filterInfo;
	private final Map<Integer, ExtendedInfo> resultInfo;
	
	public MultiStatResult(final ParallelData parallelData) {
		value2stat 			= new TreeMap<>();
		markedFiltered 		= false;
		filterInfo			= new HashMap<>();
		resultInfo			= new HashMap<>();
		this.parallelData	= parallelData;
	}
	
	public MultiStatResult(final Result result) {
		value2stat = new TreeMap<>();
		copyStat(result, value2stat);
		
		markedFiltered 	= result.isFiltered();
		final int n		= result.getValueSize();
		filterInfo 		= new HashMap<>(n);
		resultInfo 		= new HashMap<>(n);
		copyInfo(filterInfo, resultInfo, result);
		
		this.parallelData = result.getParellelData();
	}
	
	// TODO is this needed?
	private void copyStat(final Result src, final Map<Integer, Double> dest) {
		for (final int valueIndex : src.getValueIndexes()) {
			if (dest.containsKey(valueIndex)) {
				throw new IllegalStateException("Duplicate keys are not allowed!");
			}
			dest.put(valueIndex, src.getScore(valueIndex));
		}
	}
	
	private void copyInfo(final Map<Integer, ExtendedInfo> filterInfos, final Map<Integer, ExtendedInfo> resultInfos, 
			final Result result) {
		
		for (final int valueIndex : result.getValueIndexes()) {
			copyInfoHelper(valueIndex, filterInfos, result.getFilterInfo(valueIndex));
			copyInfoHelper(valueIndex, resultInfos, result.getResultInfo(valueIndex));
		}
	}
	
	// TODO multiple values - why is it copied between multiple values
	private void copyInfoHelper(final int valueIndex, final Map<Integer, ExtendedInfo> infos, final ExtendedInfo info) {
		if (infos.containsKey(valueIndex)) {
			throw new IllegalStateException("Duplicate keys are not allowed!");
		}
		/* FIXME
		infos.put(valueIndex, new ExtendedInfo());
		infos.get(valueIndex).addAll(info);
		*/
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
		return resultInfo.get(valueIndex);
	}
	
	@Override
	public ExtendedInfo getFilterInfo(final int valueIndex) {
		return filterInfo.get(valueIndex);
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
	public ExtendedInfo getFilterInfo() {
		if (value2stat.size() == 0) {
			return null;
		}
		final int value = value2stat.firstKey();
		return filterInfo.get(value);			
	}
	
	@Override
	public ExtendedInfo getResultInfo() {
		if (value2stat.size() == 0) {
			return null;
		}
		final int value = value2stat.firstKey();
		return resultInfo.get(value);
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
		/* FIXME
		filterInfo.put(newValueIndex, new ExtendedInfo());
		resultInfo.put(newValueIndex, new ExtendedInfo());
		*/
		return newValueIndex;
	}
	
}
