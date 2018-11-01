package lib.data.result;

import lib.data.ParallelData;
import lib.util.Info;

public class OneValueResult implements Result {

	private static final long serialVersionUID = 1L;
	
	private final ParallelData parallelData;
	private boolean markedFiltered;
	private final Info filterInfo;
	private final Info resultInfo;
	
	protected OneValueResult(final ParallelData parallelData) {
		this.parallelData = parallelData;
		
		markedFiltered = false;
		filterInfo = new Info();
		resultInfo = new Info();
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
	public void setFiltered(final boolean marked) {
		markedFiltered = marked;
	}

	@Override
	public boolean isFiltered() {
		return markedFiltered;
	}

	@Override
	public int getValues() {
		return 1;
	}
	
	@Override
	public Info getFilterInfo() {
		return filterInfo;
	}
	
	@Override
	public Info getResultInfo() {
		return resultInfo;
	}

	@Override
	public double getStat() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getStat(int valueIndex) {
		// TODO Auto-generated method stub
		return 0;
	}

}
