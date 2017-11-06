package jacusa.pileup.builder;

import jacusa.data.AbstractData;
import jacusa.filter.FilterContainer;
import jacusa.pileup.builder.hasLibraryType.LIBRARY_TYPE;
import jacusa.pileup.iterator.location.CoordinateAdvancer;
import jacusa.util.WindowCoordinate;
import jacusa.util.Coordinate.STRAND;
import net.sf.samtools.SAMRecord;
import net.sf.samtools.SAMRecordIterator;

public interface DataBuilder<T extends AbstractData> extends CoordinateAdvancer {

	SAMRecord getNextRecord(final int targetPosition);

	WindowCoordinate getWindowCoordinates();

	SAMRecordIterator getIterator(final int targetPosition);
	SAMRecord[] getSAMRecordsBuffer();
 	void processRecord(final SAMRecord record);
	
	int getFilteredSAMRecords();
	int getSAMRecords();
	void incrementFilteredSAMRecords();
	void incrementSAMRecords();

	void clearCache();
	
	// strand dependent methods
	int getCoverage(final int windowPosition, final STRAND strand);

	T getData(final int windowPosition, final STRAND strand);
	WindowCache getWindowCache(final STRAND strand);

	FilterContainer<T> getFilterContainer(final int windowPosition, final STRAND strand); 

	LIBRARY_TYPE getLibraryType();

	CACHE_STATUS getCacheStatus();
	
	public enum CACHE_STATUS {NOT_CACHED,CACHED,NOT_FOUND};
	
}
