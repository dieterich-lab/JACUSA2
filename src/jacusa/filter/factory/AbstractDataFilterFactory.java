package jacusa.filter.factory;

import jacusa.filter.cache.FilterCache;

import java.util.ArrayList;
import java.util.List;

import lib.cli.options.BaseCallConfig;
import lib.cli.parameter.AbstractConditionParameter;
import lib.cli.parameter.AbstractParameter;
import lib.data.AbstractData;
import lib.data.generator.DataGenerator;
import lib.data.has.hasLibraryType.LIBRARY_TYPE;
import lib.tmp.CoordinateController;
import lib.util.coordinate.Coordinate;

public abstract class AbstractDataFilterFactory<T extends AbstractData, F extends AbstractData> 
extends AbstractFilterFactory<T> 
implements DataGenerator<F> {

	private final DataGenerator<F> dataGenerator;

	public AbstractDataFilterFactory(final char c, final String desc, 
			final DataGenerator<F> dataGenerator) {

		super(c, desc);
		this.dataGenerator = dataGenerator;
	}

	@Override
	public F[][] copyContainerData(final F[][] containerData) {
		return dataGenerator.copyContainerData(containerData);
	}	
	
	@Override
	public F copyData(final F data) {
		return dataGenerator.copyData(data);
	}
	
	@Override
	public F[] copyReplicateData(final F[] replicateData) {
		return dataGenerator.copyReplicateData(replicateData);
	}
	
	@Override
	public F[][] createContainerData(final int n) {
		return dataGenerator.createContainerData(n);
	}
	
	@Override
	public F createData(final LIBRARY_TYPE libraryFype, final Coordinate coordinate) {
		return dataGenerator.createData(libraryFype, coordinate);
	}
	
	@Override
	public F[] createReplicateData(final int n) {
		return dataGenerator.createReplicateData(n);
	}
	
	protected abstract FilterCache<F> createFilterCache(final AbstractConditionParameter<T> conditionParameter,
			final BaseCallConfig baseCallConfig, 
			final CoordinateController coordinateController);
	
	protected List<List<FilterCache<F>>> createConditionFilterCaches(final AbstractParameter<T, ?> parameter,
			final CoordinateController coordinateContainer,
			final AbstractDataFilterFactory<T, F> dataFilterFactory) {

		final List<List<FilterCache<F>>> filterCaches = 
				new ArrayList<List<FilterCache<F>>>(parameter.getConditionsSize());
		
		for (int conditionIndex = 0; conditionIndex < parameter.getConditionsSize(); conditionIndex++) {
			final int replicates = parameter.getReplicates(conditionIndex);
			List<FilterCache<F>> list = new ArrayList<FilterCache<F>>(replicates);
		
			for (int replicateIndex = 0; replicateIndex < parameter.getConditionsSize(); replicateIndex++) {
				final FilterCache<F> filterCache = createFilterCache(parameter.getConditionParameter(conditionIndex),
						parameter.getBaseConfig(),
						coordinateContainer);
				list.add(filterCache);
			}
			filterCaches.add(list);
		}

		return filterCaches;
	}
	
} 