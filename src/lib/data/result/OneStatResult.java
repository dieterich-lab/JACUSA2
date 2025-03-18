package lib.data.result;

import java.util.SortedSet;
import java.util.TreeSet;

import lib.data.ParallelData;
import lib.util.ExtendedInfo;

/**
 * TODO add documentation
 */
public class OneStatResult implements Result {

	private static final long serialVersionUID = 1L;
	
	private final Double stat;
	private final ParallelData parallelData;
	
	private boolean markedFiltered;
	private final ExtendedInfo filterInfo;
	private final ExtendedInfo resultInfo;

	private final SortedSet<Integer> valueIndex;
	
	public OneStatResult(final double stat, final ParallelData parallelData, final ExtendedInfo info) {
		this.stat 			= stat;
		this.parallelData 	= parallelData;
		
		markedFiltered 	= false;
		filterInfo 		= new ExtendedInfo(parallelData.getReplicates());
		resultInfo 		= info;
		
		valueIndex = new TreeSet<>();
		valueIndex.add(Result.TOTAL);
	}
	
	@Override
	public double getScore(final int value) {
		return stat;
	}

	@Override
	public ParallelData getParellelData() {
		return parallelData;
	}

	@Override
	public ExtendedInfo getResultInfo(final int valueIndex) {
		return resultInfo;
	}

	@Override
	public ExtendedInfo getFilterInfo(final int valueIndex) {
		return filterInfo;
	}

	@Override
	public void setFiltered(boolean marked) {
		this.markedFiltered = marked;
	}

	@Override
	public boolean isFiltered() {
		return markedFiltered;
	}
	
	@Override
	public SortedSet<Integer> getValueIndexes() {
		return valueIndex;
	}
	
	@Override
	public int getValueSize() {
		return valueIndex.size();
	}

	@Override
	public ExtendedInfo getResultInfo() {
		return resultInfo;
	}

	@Override
	public ExtendedInfo getFilterInfo() {
		return filterInfo;
	}
	
	@Override
	public double getScore() {
		return stat;
	}
	
}
