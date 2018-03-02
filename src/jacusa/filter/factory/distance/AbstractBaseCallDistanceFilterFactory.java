package jacusa.filter.factory.distance;

import lib.cli.options.BaseCallConfig;
import lib.cli.parameter.AbstractConditionParameter;
import lib.data.AbstractData;
import lib.data.cache.UniqueBaseCallDataCache;
import lib.data.generator.DataGenerator;
import lib.data.has.hasBaseCallCount;
import lib.data.has.hasReferenceBase;
import lib.util.coordinate.CoordinateController;

public abstract class AbstractBaseCallDistanceFilterFactory<T extends AbstractData & hasBaseCallCount & hasReferenceBase, F extends AbstractData & hasBaseCallCount>
extends AbstractDistanceFilterFactory<T, F> {
		
	public AbstractBaseCallDistanceFilterFactory(final char c, final String desc, 
			final int defaultFilterDistance, 
			final double defaultFilterMinRatio, 
			final int defaultFilterMinCount, 
			final DataGenerator<F> dataGenerator) {

		super(c, desc, 
				defaultFilterDistance, defaultFilterMinRatio, defaultFilterMinCount, 
				dataGenerator);
	}

	protected UniqueBaseCallDataCache<F> createUniqueBaseCallCache(
			final AbstractConditionParameter<T> conditionParameter,
			final BaseCallConfig baseCallConfig,
			final CoordinateController coordinateController) {
		
		return new UniqueBaseCallDataCache<F>(
				conditionParameter.getMaxDepth(), 
				conditionParameter.getMinBASQ(), 
				baseCallConfig, 
				coordinateController);
	}

}
