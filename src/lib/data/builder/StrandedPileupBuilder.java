package lib.data.builder;

import lib.cli.parameters.AbstractConditionParameter;
import lib.cli.parameters.AbstractParameter;
import lib.data.basecall.PileupData;
import lib.data.cache.AbstractStrandedBaseCallCache;
import lib.util.Coordinate;

public class StrandedPileupBuilder<T extends PileupData> 
extends AbstractDataBuilder<T> {
	
	private AbstractStrandedBaseCallCache cache;

	public StrandedPileupBuilder(
			final AbstractConditionParameter<T> conditionParameter,
			final AbstractParameter<T> parameters,
			final LIBRARY_TYPE libraryType,
			final AbstractStrandedBaseCallCache cache) {
		super(conditionParameter, parameters, libraryType, cache);
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
		T data = getParameters().getMethodFactory().createData();

		Coordinate newCoordinate = new Coordinate(coordinate);
		newCoordinate.setPosition(newCoordinate.getStart());
		data.setCoordinate(newCoordinate); 
		
		// copy base and qual info from cache
		// TODO dataContainer.setBaseQualCount(Cache.getBaseCount(windowPosition));

		/* TODO
		byte referenceBaseByte = windowCache.getReferenceBase(windowPosition);
		if (referenceBaseByte != (byte)'N') {
			dataContainer.setReferenceBase((char)referenceBaseByte);
		}
		*/

		return data;
	}
	
}