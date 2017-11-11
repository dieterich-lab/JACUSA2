package lib.data.builder;

import lib.cli.parameters.AbstractConditionParameter;
import lib.cli.parameters.AbstractParameter;
import lib.data.basecall.PileupData;
import lib.data.cache.PileupCallCache;
import lib.util.Coordinate;

public class UnstrandedPileupBuilder<T extends PileupData> 
extends AbstractDataBuilder<T> {
	
	private PileupCallCache<T> cache;

	public UnstrandedPileupBuilder(
			final AbstractConditionParameter<T> conditionParameter,
			final AbstractParameter<T> parameters,
			final PileupCallCache<T> cache) {
		super(conditionParameter, parameters, LIBRARY_TYPE.UNSTRANDED, cache);
		this.cache = cache;
	}
	
	/* TODO
	@Override
	public FilterContainer<T> getFilterContainer(int windowPosition, STRAND strand) {
		return filterContainer;
	}
	*/
	
	// TODO do check outside
	@Override
	public T getData(final Coordinate coordinate) {
		final T data = cache.getData(coordinate);
		Coordinate newCoordinate = new Coordinate(coordinate);
		newCoordinate.setPosition(newCoordinate.getStart());
		data.setCoordinate(newCoordinate); 
		return data;
	}
	
}