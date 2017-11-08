package lib.io.record;

import lib.io.AbstractWriter;
import htsjdk.samtools.SAMRecord;


public abstract class AbstractRecordWriter 
extends AbstractWriter {
	
	public AbstractRecordWriter(final String filename, final AbstractRecordFormat format) {
		super(filename, format);
	}
	
	public abstract void addRecord(final SAMRecord record);
	
}
