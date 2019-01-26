package test.utlis;

import java.util.Collection;
import java.util.Iterator;

import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.util.CloseableIterator;
import lib.util.coordinate.Coordinate;

public class SAMRecordIterator implements CloseableIterator<SAMRecord> {

	private final Coordinate coordinate;
	
	private final Iterator<SAMRecord> iterator;
	private SAMRecord next;
	
	public SAMRecordIterator(
			final Coordinate coordinate,
			final Collection<SAMRecord> records) {
		
		this.coordinate = coordinate;
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
    	
    	SAMRecord tmp = iterator.next();
    	while (! tmp.overlaps(coordinate)) {
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
