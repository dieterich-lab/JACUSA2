package jacusa.pileup.builder;

import jacusa.filter.FilterContainer;
import jacusa.pileup.builder.hasLibraryType.LIBRARY_TYPE;
import jacusa.pileup.iterator.location.CoordinateAdvancer;
import jacusa.pileup.iterator.location.StrandedCoordinateAdvancer;
import lib.cli.parameters.AbstractParameters;
import lib.cli.parameters.JACUSAConditionParameters;
import lib.data.BaseQualData;
import lib.util.Coordinate;
import lib.util.WindowCoordinate;
import lib.util.Coordinate.STRAND;

import htsjdk.samtools.SamReader;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SAMRecordIterator;

/**
 * @author Michael Piechotta
 *
 */
public abstract class AbstractStrandedPileupBuilder<T extends BaseQualData> 
implements DataBuilder<T> {

	private CoordinateAdvancer advancer;
	private JACUSAConditionParameters<T> condition;
	
	private WindowCoordinate windowCoordinates;
	private AbstractParameters<T> parameters;
	private LIBRARY_TYPE libraryType;

	private AbstractDataBuilder<T> forward;
	private AbstractDataBuilder<T> reverse;

	private CACHE_STATUS cacheStatus;
	
	public AbstractStrandedPileupBuilder(final WindowCoordinate windowCoordinates,
			final SamReader reader, 
			final JACUSAConditionParameters<T> condition,
			final AbstractParameters<T> parameters,
			final LIBRARY_TYPE libraryType) {
		this.condition = condition;
		advancer = new StrandedCoordinateAdvancer(new Coordinate(windowCoordinates.getContig(), -1, STRAND.FORWARD));

		this.windowCoordinates 	= windowCoordinates;
		this.parameters 		= parameters;
		this.libraryType		= libraryType;

		forward = new UnstrandedPileupBuilder<T>(windowCoordinates, reader, STRAND.FORWARD, condition, parameters);
		reverse = new UnstrandedPileupBuilder<T>(windowCoordinates, reader, STRAND.REVERSE, condition, parameters);
		cacheStatus				= CACHE_STATUS.NOT_CACHED;
	}
	
	@Override
	public void clearCache() {
		forward.clearCache();
		reverse.clearCache();
		cacheStatus = CACHE_STATUS.NOT_CACHED;
	}

	@Override
	public int getCoverage(int windowPosition, STRAND strand) {
		if (strand == STRAND.FORWARD) {
			return forward.getCoverage(windowPosition, strand);
		} else if(strand == STRAND.REVERSE) {
			return reverse.getCoverage(windowPosition, strand);
		} else {
			return forward.getCoverage(windowPosition, strand) + reverse.getCoverage(windowPosition, strand);
		}
	}

	@Override
	public Coordinate nextCoordinate() {
		return advancer.nextCoordinate();
	}
	
	@Override
	public FilterContainer<T> getFilterContainer(int windowPosition, STRAND strand) {
		if (strand == STRAND.FORWARD) {
			return forward.getFilterContainer(windowPosition, strand);
		} else if (strand == STRAND.REVERSE) {
			return reverse.getFilterContainer(windowPosition, strand);
		} else {
			return null;
		}
	}

	@Override
	public T getData(final int windowPosition, final STRAND strand) {
		T dataContainer = parameters.getMethodFactory().createData();
		
		dataContainer.getCoordinate().setContig(windowCoordinates.getContig()); 
		dataContainer.getCoordinate().setPosition(windowCoordinates.getGenomicPosition(windowPosition));
		dataContainer.getCoordinate().setStrand(strand);

		WindowCache windowCache = getWindowCache(strand);

		// copy base and qual info from cache
		dataContainer.setBaseQualCount(windowCache.getBaseCount(windowPosition));

		byte referenceBaseByte = windowCache.getReferenceBase(windowPosition);
		if (referenceBaseByte != (byte)'N') {
			dataContainer.setReferenceBase((char)referenceBaseByte);
		}

		if (strand == STRAND.REVERSE) {
			dataContainer.getBaseQualCount().invert();
		}

		// for "Stranded"PileupBuilder the basesCounts in the pileup are already inverted (when on the reverse strand) 
		return dataContainer;
	}

	@Override
	public WindowCache getWindowCache(final STRAND strand) {
		if (strand == STRAND.FORWARD) {
			return forward.getWindowCache(strand);
		} else if (strand == STRAND.REVERSE) {
			return reverse.getWindowCache(strand);
		} else {
			return null;
		}
	}

	@Override
	public LIBRARY_TYPE getLibraryType() {
		return libraryType;
	}
	
	@Override
	public WindowCoordinate getWindowCoordinates() {
		return windowCoordinates;
	}
	
	@Override
	public SAMRecordIterator getIterator(final int genomicPosition) {
		return forward.getIterator(genomicPosition);
	}
	
	public void adjustPosition(final int newPosition, final STRAND newStrand) {
		if (cacheStatus == CACHE_STATUS.NOT_CACHED || ! windowCoordinates.isContainedInWindow(newPosition)) {
			if (AbstractDataBuilder.fillWindow(this, condition, forward.getSAMRecordsBuffer(), newPosition)) {
				cacheStatus = CACHE_STATUS.CACHED;
			} else {
				cacheStatus = CACHE_STATUS.NOT_FOUND;
			}
		}

		advancer.adjustPosition(newPosition, newStrand);
	}
	
	@Override
	public SAMRecord[] getSAMRecordsBuffer() {
		return forward.getSAMRecordsBuffer();
	}
	
	@Override
	public void incrementFilteredSAMRecords() {
		forward.incrementFilteredSAMRecords();
	}
	
	@Override
	public void incrementSAMRecords() {
		forward.incrementSAMRecords();
	}
	
	@Override
	public int getSAMRecords() {
		return forward.getSAMRecords() + reverse.getSAMRecords();
	}
	
	@Override
	public int getFilteredSAMRecords() {
		return forward.getFilteredSAMRecords() + reverse.getFilteredSAMRecords();
	}
	
	@Override
	public SAMRecord getNextRecord(int targetPosition) {
		// ignore reverse; reference object shared between forward and reverse
		return forward.getNextRecord(targetPosition);
	}

	protected AbstractDataBuilder<T> getForward() {
		return forward;
	}
	
	protected AbstractDataBuilder<T> getReverse() {
		return reverse;
	}

	public Coordinate getCurrentCoordinate() {
		return advancer.getCurrentCoordinate();
	}

	@Override
	public void advance() {
		final Coordinate nextCoordinate = nextCoordinate();
		adjustPosition(nextCoordinate.getPosition(), nextCoordinate.getStrand());
	}

	@Override
	public DataBuilder.CACHE_STATUS getCacheStatus() {
		return cacheStatus;
	}

}
