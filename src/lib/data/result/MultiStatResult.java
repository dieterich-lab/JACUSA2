package lib.data.result;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import lib.data.ParallelData;
import lib.util.Info;

/**
 * TODO add documentation
 */
public class MultiStatResult implements Result {
	
	private static final long serialVersionUID = 1L;
	
	private final SortedMap<Integer, Double> value2stat;
	private final ParallelData parallelData;
	
	private boolean markedFiltered;
	private final Map<Integer, Info> filterInfo;
	private final Map<Integer, Info> resultInfo;
	
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
	
	private void copyStat(final Result src, final Map<Integer, Double> dest) {
		for (final int valueIndex : src.getValuesIndex()) {
			if (dest.containsKey(valueIndex)) {
				throw new IllegalStateException("Duplicate keys are not allowed!");
			}
			dest.put(valueIndex, src.getStat(valueIndex));
		}
	}
	
	private void copyInfo(final Map<Integer, Info> filterInfos, final Map<Integer, Info> resultInfos, 
			final Result result) {
		
		for (final int valueIndex : result.getValuesIndex()) {
			copyInfoHelper(valueIndex, filterInfos, result.getFilterInfo(valueIndex));
			copyInfoHelper(valueIndex, resultInfos, result.getResultInfo(valueIndex));
		}
	}
	
	// TODO multiple values - why is it copied between multiple values
	private void copyInfoHelper(final int valueIndex, final Map<Integer, Info> infos, final Info info) {
		if (infos.containsKey(valueIndex)) {
			throw new IllegalStateException("Duplicate keys are not allowed!");
		}
		infos.put(valueIndex, new Info());
		infos.get(valueIndex).addAll(info);
	}
	
	@Override
	public double getStat(final int valueIndex) {
		return value2stat.get(valueIndex);
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
	public SortedSet<Integer> getValuesIndex() {
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
	public Info getFilterInfo() {
		if (value2stat.size() == 0) {
			return null;
		}
		final int value = value2stat.firstKey();
		return filterInfo.get(value);			
	}
	
	@Override
	public Info getResultInfo() {
		if (value2stat.size() == 0) {
			return null;
		}
		final int value = value2stat.firstKey();
		return resultInfo.get(value);
	}
	
	@Override
	public double getStat() {
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
		filterInfo.put(newValueIndex, new Info());
		resultInfo.put(newValueIndex, new Info());
		return newValueIndex;
	}
	
}
