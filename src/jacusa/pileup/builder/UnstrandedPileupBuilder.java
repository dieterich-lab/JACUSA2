package jacusa.pileup.builder;

import jacusa.filter.FilterContainer;
import lib.cli.parameters.AbstractParameters;
import lib.cli.parameters.JACUSAConditionParameters;
import lib.data.BaseQualData;
import lib.util.WindowCoordinate;
import lib.util.Coordinate.STRAND;

import htsjdk.samtools.SamReader;

/**
 * @author Michael Piechotta
 *
 */
public class UnstrandedPileupBuilder<T extends BaseQualData> 
extends AbstractDataBuilder<T> {
	
	public UnstrandedPileupBuilder(final WindowCoordinate windowCoordinates,
			final SamReader samReader,
			final STRAND strand,
			final JACUSAConditionParameters<T> condition,
			final AbstractParameters<T> parameters) {
		super(windowCoordinates, 
				samReader, 
				condition, 
				parameters, 
				strand,
				LIBRARY_TYPE.UNSTRANDED);
	}

	@Override
	public FilterContainer<T> getFilterContainer(int windowPosition, STRAND strand) {
		return filterContainer;
	}
	
	@Override
	public T getData(final int windowPosition, final STRAND strand) {
		T dataContainer = parameters.getMethodFactory().createData();

		dataContainer.getCoordinate().setContig(windowCoordinates.getContig()); 
		dataContainer.getCoordinate().setPosition(windowCoordinates.getGenomicPosition(windowPosition));
		dataContainer.getCoordinate().setStrand(strand);

		// copy base and qual info from cache
		dataContainer.setBaseQualCount(windowCache.getBaseCount(windowPosition));

		byte referenceBaseByte = windowCache.getReferenceBase(windowPosition);
		if (referenceBaseByte != (byte)'N') {
			dataContainer.setReferenceBase((char)referenceBaseByte);
		}

		if (strand == STRAND.REVERSE) {
			dataContainer.getBaseQualCount().invert();
		}

		return dataContainer;
	}

	@Override
	protected void addHighQualityBaseCall(
			int windowPosition, int baseIndex, int qualIndex) {
		windowCache.addHighQualityBaseCall(windowPosition, baseIndex, qualIndex);
	}
	
	@Override
	protected void addLowQualityBaseCall(
			int windowPosition, int baseIndex, int qualIndex) {
		windowCache.addLowQualityBaseCall(windowPosition, baseIndex, qualIndex);
	}

	@Override
	public WindowCache getWindowCache(STRAND strand) {
		return windowCache;
	}

	@Override
	public int getCoverage(int windowPosition, STRAND strand) {
		// for unstrandedPileup we ignore strand
		return windowCache.getCoverage(windowPosition);
	}
	
}