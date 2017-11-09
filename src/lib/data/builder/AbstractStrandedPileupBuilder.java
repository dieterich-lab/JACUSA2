package lib.data.builder;


import jacusa.filter.FilterContainer;
import lib.cli.parameters.AbstractConditionParameter;
import lib.cli.parameters.AbstractParameter;
import lib.data.AbstractData;
import lib.data.cache.AbstractStrandedBaseCallCache;
import lib.data.has.hasPileupCount;
import lib.util.Coordinate;
import lib.util.Coordinate.STRAND;

public class AbstractStrandedPileupBuilder<T extends AbstractData & hasPileupCount> 
extends AbstractDataBuilder<T> {

	private final AbstractStrandedBaseCallCache strandedCache;

	public AbstractStrandedPileupBuilder(
			final AbstractConditionParameter<T> conditionParameter,
			final AbstractParameter<T> parameters,
			final LIBRARY_TYPE libraryType,
			final AbstractStrandedBaseCallCache strandedCache) {
		super(conditionParameter, parameters, libraryType, strandedCache);
		this.strandedCache = strandedCache;
	}

	@Override
	public void clearCache() {
		// TODO FilterContainer
		super.clearCache();
	}

	@Override
	public FilterContainer<T> getFilterContainer(final Coordinate coordinate) {
		/*
		if (strand == STRAND.FORWARD) {
			return forward.getFilterContainer(windowPosition, strand);
		} else if (strand == STRAND.REVERSE) {
			return reverse.getFilterContainer(windowPosition, strand);
		} else {
			return null;
		}
		*/
		return null;
	}

	@Override
	public T getData(final Coordinate coordinate) {
		final T data = null;

		switch (coordinate.getStrand()) {
		case FORWARD:
			break;

		case REVERSE:
			break;

		default:
			break;
		}

		if (coordinate.getStrand() == STRAND.REVERSE) {
			data.getPileupCount().invert();
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
