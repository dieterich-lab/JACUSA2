package lib.data.builder.factory;

import jacusa.filter.FilterContainer;
import lib.cli.parameters.AbstractConditionParameter;
import lib.data.AbstractData;
import lib.data.builder.AbstractDataBuilder;
import lib.data.builder.RTArrestPileupBuilder;
import lib.data.cache.AlignmentCache;
import lib.data.cache.Cache;
import lib.data.has.hasBaseCallCount;
import lib.data.has.hasReadInfoCount;

// TODO
public class RTArrestPileupBuilderFactory<T extends AbstractData & hasBaseCallCount & hasReadInfoCount> 
extends AbstractDataBuilderFactory<T> {

	final AbstractDataBuilderFactory<T> pbf;

	public RTArrestPileupBuilderFactory(final AbstractDataBuilderFactory<T> pbf) {
		super(pbf.getLibraryType(), pbf.getGeneralParameter());
		this.pbf = pbf;
	}

	@Override
	public AbstractDataBuilder<T> newInstance(final AbstractConditionParameter<T> conditionParameter) {
		final Cache<T> cache = new AlignmentCache<T>(getGeneralParameter().getMethodFactory());
		final FilterContainer<T> filterContainer = null;

		return new RTArrestPileupBuilder<T>(conditionParameter,
				pbf.newInstance(conditionParameter), 
				cache, filterContainer);
	}

}
