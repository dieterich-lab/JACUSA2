package jacusa.filter;

import htsjdk.tribble.Feature;
import htsjdk.tribble.FeatureCodec;
import htsjdk.tribble.readers.LineIterator;
import jacusa.filter.factory.exclude.ContainedCoordinate;
import jacusa.filter.factory.exclude.FileBasedContainedCoordinate;
import lib.data.ParallelData;
import lib.util.coordinate.Coordinate;

/**
 * This class adds a filter that enables to mark sites as contained in an other 
 * BED or VCF file, e. g.: polymorphic positions etc. 
 * The file has to be sorted - this is not checked!
 * TODO test 
 */
public class ExcludeSiteFilter extends AbstractFilter {

	// file type independent representation of the file
	private final ContainedCoordinate containedCoordinate; 
	
	public ExcludeSiteFilter(
			final char id,
			final String fileName,
			final FeatureCodec<? extends Feature, LineIterator> codec) {
		
		super(id);
		containedCoordinate = new FileBasedContainedCoordinate(fileName, codec);
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
