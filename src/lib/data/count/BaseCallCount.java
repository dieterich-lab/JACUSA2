package lib.data.count;

import java.util.Set;

import lib.data.basecall.array.ArrayBaseCallCount;
import lib.data.has.HasCoverage;

public interface BaseCallCount extends HasCoverage {

	BaseCallCount copy();
	
	int getBaseCall(int baseIndex);
	Set<Integer> getAlleles();
	
	void increment(int baseIndex);

	void clear();

	void set(int baseIndex, int count);
	void add(int baseIndex, BaseCallCount baseQualCount);
	void add(BaseCallCount baseCallCount);
	void add(int baseIndexDest, int baseIndexSrc, BaseCallCount baseCallCount);
	
	void substract(int baseIndex, BaseCallCount baseCallCount);
	void substract(int baseIndexDest, int baseIndexSrc, BaseCallCount baseCallCount);
	void substract(BaseCallCount baseCallCount);

	void invert();
	
	public String toString();
	
	public static BaseCallCount createDefault() {
		return new ArrayBaseCallCount();
	}
	
}
