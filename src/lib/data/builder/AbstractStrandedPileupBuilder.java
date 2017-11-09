package lib.data.builder;

import com.sun.corba.se.spi.protocol.ForwardException;

import jacusa.filter.FilterContainer;
import lib.cli.parameters.AbstractConditionParameter;
import lib.cli.parameters.AbstractParameters;
import lib.cli.parameters.JACUSAConditionParameters;
import lib.data.BaseQualData;
import lib.data.builder.hasLibraryType.LIBRARY_TYPE;
import lib.data.cache.StrandedBaseCallCache;
import lib.location.CoordinateAdvancer;
import lib.location.StrandedCoordinateAdvancer;
import lib.util.Coordinate;
import lib.util.Coordinate.STRAND;

import htsjdk.samtools.SamReader;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SAMRecordIterator;

/**
 * @author Michael Piechotta
 *
 */
public abstract class AbstractStrandedPileupBuilder<T extends BaseQualData> 
extends AbstractDataBuilder<T> {

	private final StrandedBaseCallCache strandedCache;

	public AbstractStrandedPileupBuilder(
			final AbstractConditionParameter<T> conditionParameter,
			final AbstractParameters<T> parameters,
			final LIBRARY_TYPE libraryType,
			final StrandedBaseCallCache strandedCache) {
		super(conditionParameter, parameters, libraryType, strandedCache);
		this.strandedCache = strandedCache;
	}
	
	public void clearCache() {
		// TODO FilterContainer
		super.clearCache();
	}

	public FilterContainer<T> getFilterContainer(int windowPosition, STRAND strand) {
		if (strand == STRAND.FORWARD) {
			return forward.getFilterContainer(windowPosition, strand);
		} else if (strand == STRAND.REVERSE) {
			return reverse.getFilterContainer(windowPosition, strand);
		} else {
			return null;
		}
	}

	// TODO
	public T getData(final int windowPosition, final STRAND strand) {
		final T data = null;

		switch (strand) {
		case FORWARD:
			break;

		case REVERSE:
			break;

		default:
			break;
		}

		if (strand == STRAND.REVERSE) {
			data.getBaseQualCount().invert();
		}

		// for "Stranded"PileupBuilder the basesCounts in the pileup are already inverted (when on the reverse strand) 
		return data;
	}

	/*
	public void advance() {
		final Coordinate nextCoordinate = nextCoordinate();
		adjustPosition(nextCoordinate.getPosition(), nextCoordinate.getStrand());
	}
	*/

}
