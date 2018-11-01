package lib.data.result;

import java.io.Serializable;

import lib.data.has.HasParallelData;
import lib.util.Info;

public interface Result 
extends HasParallelData, Serializable {
	
	Info getResultInfo();
	Info getFilterInfo();
	
	Info getResultInfo(int valueIndex);
	Info getFilterInfo(int valueIndex);

	void setFiltered(boolean isFiltered);
	boolean isFiltered();

	int getValues();
	
	double getStat();
	double getStat(int valueIndex);
	
}
