package test.utlis;

import java.util.Collection;
import java.util.Iterator;

import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.util.CloseableIterator;
import htsjdk.samtools.util.Interval;
import htsjdk.samtools.util.Locatable;

public class SAMRecordIterator implements CloseableIterator<SAMRecord> {

	private final String contig;
	private final int start;
	private final int end;
	
	private final Iterator<SAMRecord> iterator;
	private SAMRecord next;
	
	public SAMRecordIterator(
			final String contig, final int start, final int end,
			final Collection<SAMRecord> records) {
		
		this.contig = contig;
		this.start = start;
		this.end = end;

		iterator = records.iterator();
	}

	@Override
    public void close() { }
	
    @Override
    public boolean hasNext() {
    	if (next != null) {
    		return true;
    	}
    	if (! iterator.hasNext()) {
    		return false;
    	}
    	
    	final Locatable position = new Interval(contig, start, end);
    	SAMRecord tmp = iterator.next();
    	while (! tmp.overlaps(position)) {
    		if (! iterator.hasNext()) {
        		return false;
        	}
    		tmp = iterator.next();
    	}
    	next = tmp;
    	return true; 
    }

    @Override
    public SAMRecord next() {
    	if (! hasNext()) {
    		return null;
    	}

    	SAMRecord tmp = next;
    	next = null;
    	return tmp;
    }

    @Override
    public void remove() { }

}
