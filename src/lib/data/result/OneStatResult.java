package lib.data.result;

import java.util.SortedSet;
import java.util.TreeSet;

import lib.data.ParallelData;
import lib.util.Info;

/**
 * TODO add documentation
 */
public class OneStatResult 
implements Result {

	private static final long serialVersionUID = 1L;
	
	private final Double stat;
	private final ParallelData parallelData;
	
	private boolean markedFiltered;
	private final Info filterInfo;
	private final Info resultInfo;

	private final SortedSet<Integer> valueIndex;
	
	public OneStatResult(final double stat, final ParallelData parallelData) {
		this.stat 			= stat;
		this.parallelData 	= parallelData;
		
		markedFiltered 	= false;
		filterInfo 		= new Info();
		resultInfo 		= new Info();
		
		valueIndex = new TreeSet<>();
		valueIndex.add(Result.TOTAL);
	}
	
	@Override
	public double getStat(final int value) {
		return stat;
	}

	@Override
	public ParallelData getParellelData() {
		return parallelData;
	}

	@Override
	public Info getResultInfo(final int valueIndex) {
		return resultInfo;
	}

	@Override
	public Info getFilterInfo(final int valueIndex) {
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
	public SortedSet<Integer> getValuesIndex() {
		return valueIndex;
	}
	
	@Override
	public int getValueSize() {
		return valueIndex.size();
	}

	@Override
	public Info getResultInfo() {
		return resultInfo;
	}

	@Override
	public Info getFilterInfo() {
		return filterInfo;
	}
	
	@Override
	public double getStat() {
		return stat;
	}
	
}
