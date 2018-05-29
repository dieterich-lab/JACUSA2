package lib.data.adder.basecall;

import lib.data.AbstractData;
import lib.data.adder.IncrementAdder;
import lib.data.count.BaseCallCount;

public interface BaseCallAdder<T extends AbstractData> 
extends IncrementAdder<T> {

	int getCoverage(int windowPosition);
	BaseCallCount getBaseCallCount(int windowPosition);
}