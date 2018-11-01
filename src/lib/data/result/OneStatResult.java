package lib.data.result;

import lib.data.ParallelData;
import lib.util.Info;

public class OneStatResult 
implements Result {

	private static final long serialVersionUID = 1L;
	
	private final double stat;
	private final Result result;

	protected OneStatResult(final double stat, final ParallelData parallelData) {
		this.stat 	= stat;
		this.result = ResultFactory.createResult(parallelData);
	}
	
	@Override
	public double getStat(final int valueIndex) {
		return stat;
	}

	@Override
	public ParallelData getParellelData() {
		return result.getParellelData();
	}

	@Override
	public Info getResultInfo(final int valueIndex) {
		return result.getResultInfo(valueIndex);
	}

	@Override
	public Info getFilterInfo(final int valueIndex) {
		return result.getFilterInfo(valueIndex);
	}

	@Override
	public void setFiltered(boolean marked) {
		result.setFiltered(marked);
	}

	@Override
	public boolean isFiltered() {
		return result.isFiltered();
	}

	@Override
	public int getValues() {
		return result.getValues();
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
	public double getStat() {
		return stat;
	}
	
}
