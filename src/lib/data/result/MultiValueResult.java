package lib.data.result;

import java.util.HashMap;
import java.util.Map;

import lib.data.ParallelData;
import lib.util.Info;

public class MultiValueResult 
implements Result {

	private static final long serialVersionUID = 1L;
	
	private final int values;
	
	private final ParallelData parallelData;
	private boolean markedFiltered;
	private final Map<Integer, Info> filterInfo;
	private final Map<Integer, Info> resultInfo;
	
	protected MultiValueResult(final int values, final ParallelData parallelData) {
		this.values 		= values;
		this.parallelData 	= parallelData;
		
		markedFiltered 	= false;
		filterInfo 		= new HashMap<Integer, Info>();
		resultInfo 		= new HashMap<Integer, Info>();
	}

	@Override
	public ParallelData getParellelData() {
		return parallelData;
	}

	
	@Override
	public Info getResultInfo(final int lineIndex) {
		if (! resultInfo.containsKey(lineIndex)) {
			resultInfo.put(lineIndex, new Info());
		}
		return resultInfo.get(lineIndex);
	}

	@Override
	public Info getFilterInfo(final int lineIndex) {
		if (! filterInfo.containsKey(lineIndex)) {
			filterInfo.put(lineIndex, new Info());
		}
		return filterInfo.get(lineIndex);
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
		return values;
	}

	@Override
	public Info getResultInfo() {
		return resultInfo.get(0);
	}

	@Override
	public Info getFilterInfo() {
		return filterInfo.get(0);
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
