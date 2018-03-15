package jacusa.filter.factory.distance;

import lib.cli.options.BaseCallConfig;
import lib.cli.parameter.AbstractConditionParameter;
import lib.data.AbstractData;
import lib.data.cache.UniqueBaseCallDataCache;
import lib.data.has.hasBaseCallCount;
import lib.data.has.hasReferenceBase;
import lib.util.coordinate.CoordinateController;

/**
 * TODO add comments.
 *
 * @param <T>
 */
public abstract class AbstractBaseCallDistanceFilterFactory<T extends AbstractData & hasBaseCallCount & hasReferenceBase>
extends AbstractDistanceFilterFactory<T> {
		
	public AbstractBaseCallDistanceFilterFactory(final char c, final String desc, 
			final int defaultFilterDistance, 
			final double defaultFilterMinRatio, 
			final int defaultFilterMinCount) {

		super(c, desc, 
				defaultFilterDistance, defaultFilterMinRatio, defaultFilterMinCount);
	}

	/**
	 * 
	 * @param conditionParameter
	 * @param baseCallConfig
	 * @param coordinateController
	 * @return
	 */
	protected UniqueBaseCallDataCache<T> createUniqueBaseCallCache(
			final AbstractConditionParameter<T> conditionParameter,
			final BaseCallConfig baseCallConfig,
			final CoordinateController coordinateController) {
		
		return new UniqueBaseCallDataCache<T>(
				conditionParameter.getMaxDepth(), 
				conditionParameter.getMinBASQ(), 
				baseCallConfig, 
				coordinateController);
	}

}
