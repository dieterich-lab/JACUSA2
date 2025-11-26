package lib.record;

import java.util.Iterator;

import htsjdk.samtools.SAMRecordIterator;

public interface RecordIterator extends Iterator<Record> {
	public void updateIterator(SAMRecordIterator iterator);
}
