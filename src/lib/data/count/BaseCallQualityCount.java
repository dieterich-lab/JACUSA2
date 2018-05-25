package lib.data.count;

import java.util.Set;

public interface BaseCallQualityCount {

	BaseCallQualityCount copy();

	Set<Integer> getAlleles();
	Set<Byte> getBaseCallQuality(int baseIndex);
	int getBaseCallQuality(int baseIndex, byte baseQual);
	
	void increment(int baseIndex, byte baseQual);

	void clear();

	void set(int baseIndex, byte baseQual, int count);
	void add(int baseIndex, BaseCallQualityCount baseCallQualCount);
	void add(Set<Integer> alleles, BaseCallQualityCount baseCallQualCount);
	void add(int baseIndexDest, int baseIndexSrc, BaseCallQualityCount baseCallQualCount);
	
	void substract(int baseIndex, BaseCallQualityCount baseCallQualCount);
	void substract(int baseIndexDest, int baseIndexSrc, BaseCallQualityCount baseCallQualCount);
	void substract(int[] alleles, BaseCallQualityCount baseCallQualCount);

	void invert();
	
	public String toString();
	
}
