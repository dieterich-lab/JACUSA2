package lib.data.count;

import java.util.Set;

import lib.data.basecall.array.ArrayBaseCallCount;
import lib.data.has.HasCoverage;
import lib.util.Base;

public interface BaseCallCount extends HasCoverage {

	BaseCallCount copy();
	
	int getBaseCall(Base base);
	Set<Base> getAlleles();
	
	void increment(Base base);

	void clear();

	void set(Base base, int count);
	void add(Base base, BaseCallCount baseQualCount);
	void add(BaseCallCount baseCallCount);
	void add(Base dest, Base src, BaseCallCount baseCallCount);
	
	void substract(Base base, BaseCallCount baseCallCount);
	void substract(Base dest, Base src, BaseCallCount baseCallCount);
	void substract(BaseCallCount baseCallCount);

	void invert();
	
	public String toString();
	
	public static BaseCallCount createDefault() {
		return new ArrayBaseCallCount();
	}
	
}
