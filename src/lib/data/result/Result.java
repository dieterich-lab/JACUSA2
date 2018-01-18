package lib.data.result;

import lib.data.AbstractData;
import lib.data.has.hasParallelData;
import lib.util.Info;

public interface Result <T extends AbstractData> extends hasParallelData<T> {

	Info getResultInfo();
	Info getFilterInfo();

	void setFiltered(boolean marked);
	boolean isFiltered();
	
}