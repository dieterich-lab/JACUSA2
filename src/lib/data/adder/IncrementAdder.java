package lib.data.adder;

import htsjdk.samtools.SAMRecord;
import lib.cli.options.Base;
import lib.data.AbstractData;

public interface IncrementAdder<T extends AbstractData> 
extends DataAdder<T> {

	void increment(int referencePosition, int windowPosition, int readPosition, 
			Base base, byte baseQuality,
			SAMRecord record);

}