package lib.data.result;

import lib.data.AbstractData;
import lib.data.ParallelData;
import lib.util.Info;

public class DefaultResult<T extends AbstractData> 
implements Result<T> {

	private final ParallelData<T> parallelData;
	private boolean markedFiltered;
	private final Info filterInfo;
	private final Info resultInfo;
	
	public DefaultResult(final ParallelData<T> parallelData) {
		this.parallelData = parallelData;
		
		markedFiltered = false;
		filterInfo = new Info();
		resultInfo = new Info();
	}

	@Override
	public ParallelData<T> getParellelData() {
		return parallelData;
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
	public void setFiltered(final boolean marked) {
		markedFiltered = marked;
	}

	@Override
	public boolean isFiltered() {
		return markedFiltered;
	}

}
