package jacusa.pileup.builder;

import java.util.Arrays;

import jacusa.filter.FilterContainer;
import jacusa.pileup.builder.hasLibraryType.LIBRARY_TYPE;
import lib.cli.parameters.JACUSAConditionParameters;
import lib.data.BaseQualReadInfoData;
import lib.util.Coordinate;
import lib.util.WindowCoordinate;
import lib.util.Coordinate.STRAND;

import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SAMRecordIterator;

/**
 * @author Michael Piechotta
 *
 */
public class RTArrestPileupBuilder<T extends BaseQualReadInfoData>
implements DataBuilder<T> {

	private JACUSAConditionParameters<T> condition;
	private final DataBuilder<T> dataBuilder;
	
	private final int[] readStartCount;
	private final int[] readEndCount;

	private CACHE_STATUS cacheStatus;
	
	public RTArrestPileupBuilder(final JACUSAConditionParameters<T> condition,
			final DataBuilder<T> dataBuilder) {
		this.condition = condition;
		this.dataBuilder = dataBuilder;
		
		final int windowSize = dataBuilder.getWindowCoordinates().getWindowSize();
		readStartCount	= new int[windowSize];
		readEndCount	= new int[windowSize];
		
		cacheStatus 	= CACHE_STATUS.NOT_CACHED;
	}
	
	@Override
	public T getData(final int windowPosition, final STRAND strand) {
		T dataContainer = dataBuilder.getData(windowPosition, strand);
		
		dataContainer.getReadInfoCount().setStart(readStartCount[windowPosition]);
		dataContainer.getReadInfoCount().setEnd(readEndCount[windowPosition]);
		dataContainer.getReadInfoCount().setInner(getCoverage(windowPosition, strand) - 
				readStartCount[windowPosition] - 
				readEndCount[windowPosition]);

		int arrest = 0;
		int through = 0;

		switch (getLibraryType()) {
		
		case UNSTRANDED:
			arrest 	+= dataContainer.getReadInfoCount().getStart();
			arrest 	+= dataContainer.getReadInfoCount().getEnd();
			through += dataContainer.getReadInfoCount().getInner();
			break;

		case FR_FIRSTSTRAND:
			arrest 	+= dataContainer.getReadInfoCount().getEnd();
			through += dataContainer.getReadInfoCount().getInner();
			break;

		case FR_SECONDSTRAND:
			arrest 	+= dataContainer.getReadInfoCount().getStart();
			through += dataContainer.getReadInfoCount().getInner();
			break;				
		}

		dataContainer.getReadInfoCount().setArrest(arrest);
		dataContainer.getReadInfoCount().setThrough(through);
		
		return dataContainer;
	}

	public void processRecord(SAMRecord record) {
		dataBuilder.processRecord(record);
		
		int genomicPosition = record.getAlignmentStart();
		int windowPosition  = getWindowCoordinates().convert2WindowPosition(genomicPosition);
		
		if (windowPosition >= 0) {
			readStartCount[windowPosition] += 1;
		}
		int windowPositionReadEnd = getWindowCoordinates().convert2WindowPosition(record.getAlignmentEnd());
		if (windowPositionReadEnd >= 0) {
			readEndCount[windowPositionReadEnd] += 1;
		}
	}
	
	@Override
	public void clearCache() {
		dataBuilder.clearCache();

		Arrays.fill(readStartCount, 0);
		Arrays.fill(readEndCount, 0);		
	}

	@Override
	public int getCoverage(final int windowPosition, final STRAND strand) {
		return dataBuilder.getCoverage(windowPosition, strand);
	}

	@Override
	public WindowCache getWindowCache(final STRAND strand) {
		return dataBuilder.getWindowCache(strand);
	}

	@Override
	public FilterContainer<T> getFilterContainer(final int windowPosition, final STRAND strand) {
		return dataBuilder.getFilterContainer(windowPosition, strand);
	}

	@Override
	public SAMRecord getNextRecord(final int targetPosition) {
		return dataBuilder.getNextRecord(targetPosition);
	}

	@Override
	public SAMRecordIterator getIterator(int genomicWindowStart) {
		return dataBuilder.getIterator(genomicWindowStart);
	}
	
	@Override
	public int getSAMRecords() {
		return dataBuilder.getSAMRecords();
	}
	
	@Override
	public SAMRecord[] getSAMRecordsBuffer() {
		return dataBuilder.getSAMRecordsBuffer();
	}
	
	@Override
	public void incrementFilteredSAMRecords() {
		dataBuilder.incrementFilteredSAMRecords();
	}
	
	@Override
	public void incrementSAMRecords() {
		dataBuilder.incrementSAMRecords();
	}

	@Override
	public Coordinate nextCoordinate() {
		return dataBuilder.nextCoordinate();
	}
	
	@Override
	public int getFilteredSAMRecords() {
		return dataBuilder.getFilteredSAMRecords();
	}

	@Override
	public WindowCoordinate getWindowCoordinates() {
		return dataBuilder.getWindowCoordinates();
	}

	@Override
	public LIBRARY_TYPE getLibraryType() {
		return dataBuilder.getLibraryType();
	}

	@Override
	public void advance() {
		dataBuilder.advance();
	}

	@Override
	public Coordinate getCurrentCoordinate() {
		return dataBuilder.getCurrentCoordinate();
	}

	@Override
	public void adjustPosition(final int newPosition, final STRAND newStrand) {
		if (cacheStatus == CACHE_STATUS.NOT_CACHED || ! getWindowCoordinates().isContainedInWindow(newPosition)) {
			if (AbstractDataBuilder.fillWindow(this, condition, dataBuilder.getSAMRecordsBuffer(), newPosition)) {
				cacheStatus = CACHE_STATUS.CACHED;
			} else {
				cacheStatus = CACHE_STATUS.NOT_FOUND;
			}
		}
		dataBuilder.adjustPosition(newPosition, newStrand);
	}

	@Override
	public DataBuilder.CACHE_STATUS getCacheStatus() {
		return cacheStatus;
	}

}