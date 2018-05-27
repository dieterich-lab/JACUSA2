package lib.data.count;

import java.util.Set;

import lib.cli.options.Base;

public interface BaseCallQualityCount {

	BaseCallQualityCount copy();

	Set<Base> getAlleles();
	Set<Byte> getBaseCallQuality(Base base);
	int getBaseCallQuality(Base base, byte baseQual);
	
	void increment(Base base, byte baseQual);

	void clear();

	void set(Base base, byte baseQual, int count);
	void add(Base base, BaseCallQualityCount baseCallQualCount);
	void add(Set<Base> alleles, BaseCallQualityCount baseCallQualCount);
	void add(Base dest, Base src, BaseCallQualityCount baseCallQualCount);
	
	void substract(Base base, BaseCallQualityCount baseCallQualCount);
	void substract(Base dest, Base src, BaseCallQualityCount baseCallQualCount);
	void substract(Base[] alleles, BaseCallQualityCount baseCallQualCount);

	void invert();
	
	public String toString();
	
}
