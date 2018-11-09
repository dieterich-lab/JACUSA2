package lib.data.result;

import java.io.Serializable;
import java.util.SortedSet;

import lib.data.has.HasParallelData;
import lib.util.Info;

public interface Result 
extends HasParallelData, Serializable {
	
	Info getResultInfo();
	Info getFilterInfo();
	
	Info getResultInfo(int value);
	Info getFilterInfo(int value);

	void setFiltered(boolean isFiltered);
	boolean isFiltered();

	SortedSet<Integer> getValues();
	int getValueSize();
	
	double getStat();
	double getStat(int value);
	
}
