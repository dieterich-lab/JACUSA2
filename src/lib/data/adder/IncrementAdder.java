package lib.data.adder;

import htsjdk.samtools.SAMRecord;
import lib.util.Base;

public interface IncrementAdder 
extends DataContainerAdder {

	void increment(int referencePosition, int windowPosition, int readPosition, 
			Base base, byte baseQuality,
			SAMRecord record);

	int getCoverage(int windowPosition);
	
}