package jacusa.filter;

import htsjdk.tribble.Feature;
import htsjdk.tribble.FeatureCodec;
import htsjdk.tribble.readers.LineIterator;
import jacusa.filter.factory.exclude.ContainedCoordinate;
import jacusa.filter.factory.exclude.DefaultContainedCoordinate;
import lib.data.ParallelData;
import lib.util.coordinate.Coordinate;

/**
 * TODO add comments.
 * Tested in TODO is this test needed? 
 */
public class ExcludeSiteFilter extends AbstractFilter {

	private final ContainedCoordinate containedCoordinate; 
	
	public ExcludeSiteFilter(
			final char c, 
			final String fileName, 
			final FeatureCodec<? extends Feature, LineIterator> codec) {
		
		super(c);
		containedCoordinate = new DefaultContainedCoordinate(fileName, codec);
	}

	@Override
	public boolean filter(final ParallelData parallelData) {
		final Coordinate coordinate = parallelData.getCoordinate();
		return containedCoordinate.isContained(coordinate);
	}

	@Override
	public int getOverhang() { 
		return 0; 
	}

}