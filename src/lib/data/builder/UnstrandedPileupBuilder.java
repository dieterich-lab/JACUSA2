package lib.data.builder;

import lib.cli.parameters.AbstractConditionParameter;
import lib.cli.parameters.AbstractParameters;
import lib.data.BaseQualData;
import lib.data.cache.BaseCallCache;
import lib.util.Coordinate;

public class UnstrandedPileupBuilder<T extends BaseQualData> 
extends AbstractDataBuilder<T> {
	
	private BaseCallCache cache;

	public UnstrandedPileupBuilder(
			final AbstractConditionParameter<T> conditionParameter,
			final AbstractParameters<T> parameters,
			final BaseCallCache cache) {
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