package lib.data.builder.factory;

import jacusa.filter.FilterContainer;

import java.util.List;

import lib.cli.parameters.AbstractConditionParameter;
import lib.cli.parameters.AbstractParameter;
import lib.data.AbstractData;
import lib.data.builder.DataBuilder;
import lib.data.cache.Cache;
import lib.data.generator.DataGenerator;

public abstract class AbstractDataBuilderFactory<T extends AbstractData> {
	
	private AbstractParameter<T, ?> generalParameter;

	public AbstractDataBuilderFactory(final AbstractParameter<T, ?> generalParameter) {
		this.generalParameter = generalParameter;
	}
	
	public DataBuilder<T> newInstance(final AbstractConditionParameter<T> conditionParameter) throws IllegalArgumentException {
		final List<Cache<T>> caches = createCaches(conditionParameter);
		
		final DataGenerator<T> dataGenerator = getGeneralParameter().getMethodFactory().getDataGenerator();
		final FilterContainer<T> filterContainer = null; // TODO filter

		return new DataBuilder<T>(dataGenerator, conditionParameter, conditionParameter.getLibraryType(), caches, filterContainer);
	}

	public abstract List<Cache<T>> createCaches(final AbstractConditionParameter<T> conditionParameter);
	
	public AbstractParameter<T, ?> getGeneralParameter() {
		return generalParameter;
	}
	
	public void setGeneralParameter(final AbstractParameter<T, ?> generalParameter)  {
		this.generalParameter = generalParameter;
	}
	
}
