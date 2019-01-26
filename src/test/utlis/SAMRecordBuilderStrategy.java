package test.utlis;

import test.jacusa.filter.homopolymer.SAMRecordBuilder;

public interface SAMRecordBuilderStrategy {

	void useStrategy(String contig, SAMRecordBuilder builder);
	
}
