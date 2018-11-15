package lib.data.result;

import java.io.Serializable;
import java.util.SortedSet;

import lib.data.has.HasParallelData;
import lib.util.Info;

public interface Result 
extends HasParallelData, Serializable {
	
	Info getResultInfo();
	Info getResultInfo(int valueIndex);
	
	Info getFilterInfo();
	Info getFilterInfo(int valueIndex);

	void setFiltered(boolean isFiltered);
	boolean isFiltered();

	SortedSet<Integer> getValueIndex();
	int getValueSize();
	
	double getStat();
	double getStat(int valueIndex);
	
}
