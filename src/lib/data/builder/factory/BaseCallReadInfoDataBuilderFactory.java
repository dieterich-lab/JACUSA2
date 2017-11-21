package lib.data.builder.factory;

import java.util.List;

import lib.cli.parameters.AbstractConditionParameter;
import lib.cli.parameters.AbstractParameter;
import lib.data.AbstractData;
import lib.data.cache.AlignmentCache;
import lib.data.cache.Cache;
import lib.data.has.hasBaseCallCount;
import lib.data.has.hasReadInfoCount;
import lib.tmp.CoordinateController;


public class BaseCallReadInfoDataBuilderFactory<T extends AbstractData & hasBaseCallCount & hasReadInfoCount> 
extends AbstractDataBuilderFactory<T> {

	private AbstractDataBuilderFactory<T> dataBuilderFactory; 
	
	public BaseCallReadInfoDataBuilderFactory(final AbstractParameter<T, ?> generalParameter) {
		super(generalParameter);
		dataBuilderFactory = new BaseCallDataBuilderFactory<T>(generalParameter);
	}

	@Override
	public List<Cache<T>> createCaches(final CoordinateController coordinateController, final AbstractConditionParameter<T> conditionParameter) {
		final List<Cache<T>> caches = dataBuilderFactory.createCaches(coordinateController, conditionParameter);
		caches.add(
				new AlignmentCache<T>(
						conditionParameter.getLibraryType(), 
						coordinateController));
		return caches;
	}

}
