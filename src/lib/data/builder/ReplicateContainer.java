package lib.data.builder;

import java.util.ArrayList;
import java.util.List;

import htsjdk.samtools.SamReader;

import jacusa.filter.cache.FilterCache;
import lib.cli.parameter.AbstractConditionParameter;
import lib.cli.parameter.AbstractParameter;
import lib.data.AbstractData;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.builder.recordwrapper.SAMRecordWrapperIterator;
import lib.data.builder.recordwrapper.SAMRecordWrapperIteratorProvider;
import lib.data.has.HasLibraryType.LIBRARY_TYPE;
import lib.util.coordinate.CoordinateController;
import lib.util.coordinate.Coordinate;

public class ReplicateContainer<T extends AbstractData> {

	private final ConditionContainer<T> conditionContainer;
	
	private final AbstractConditionParameter<T> conditionParameter;
	private final AbstractParameter<T, ?> generalParameters;

	private final List<SAMRecordWrapperIteratorProvider> iteratorProviders;
	private final List<DataBuilder<T>> dataBuilders;
	
	public ReplicateContainer(
			final ConditionContainer<T> conditionContainer,
			final CoordinateController coordinateController,
			final AbstractConditionParameter<T> conditionParameter,
			final AbstractParameter<T, ?> parameter) {

		this.conditionContainer = conditionContainer;
		
		this.conditionParameter = conditionParameter;
		this.generalParameters = parameter;

		iteratorProviders = createRecordIteratorProviders(conditionParameter);
		dataBuilders = createDataBuilders(coordinateController, conditionParameter, parameter);
	}

	public List<SAMRecordWrapperIteratorProvider> getIteratorProviders() {
		return iteratorProviders;
	}
	
	public List<List<SAMRecordWrapper>> createIterators(final Coordinate activeWindowCoordinate,
			final Coordinate reservedWindowCoordinate) {

		final List<List<SAMRecordWrapper>> recordWrappers = 
				new ArrayList<List<SAMRecordWrapper>>(conditionParameter.getReplicateSize());
		
		for (int replicateIndex = 0; replicateIndex < conditionParameter.getReplicateSize(); replicateIndex++) {
			final SAMRecordWrapperIteratorProvider iteratorProvider = iteratorProviders.get(replicateIndex);
			final SAMRecordWrapperIterator iterator = 
					iteratorProvider.createIterator(activeWindowCoordinate);
			final DataBuilder<T> dataBuilder = dataBuilders.get(replicateIndex);

			recordWrappers.add(dataBuilder.buildCache(activeWindowCoordinate, iterator));
			iterator.close();
		}

		return recordWrappers; 
	}

	public List<List<SAMRecordWrapper>> updateIterators(final Coordinate activeWindowCoordinate) {
		return createIterators(activeWindowCoordinate, null);
	}
	
	public T[] getData(final Coordinate coordinate) {
		int replicateSize = conditionParameter.getReplicateSize();
		// create new container array
		T[] data = generalParameters.getMethodFactory().createReplicateData(replicateSize);

		for (int replicateIndex = 0; replicateIndex < replicateSize; ++replicateIndex) {
			final DataBuilder<T> dataBuilder = dataBuilders.get(replicateIndex);
			data[replicateIndex] = dataBuilder.getData(coordinate);
		}

		return data;
	}
	
	private List<DataBuilder<T>> createDataBuilders(
			final CoordinateController coordinateController,
			final AbstractConditionParameter<T> conditionParameter,
			final AbstractParameter<T, ?> parameter) {
		
		final List<DataBuilder<T>> dataBuilders = new ArrayList<DataBuilder<T>>(conditionParameter.getReplicateSize());

		for (int replicateIndex = 0; replicateIndex < conditionParameter.getReplicateSize(); ++replicateIndex) {
			final List<FilterCache<?>> filterCaches = 
					conditionContainer.getFilterContainer().getFilterCaches(conditionParameter.getConditionIndex(), replicateIndex);
			
			final DataBuilder<T> builder = 
					parameter.getMethodFactory().getDataBuilderFactory().newInstance(coordinateController, conditionParameter, filterCaches);
			dataBuilders.add(builder);
		}

		return dataBuilders;
	}

	public boolean isStranded() {
		for (final DataBuilder<T> builder : dataBuilders) {
			if (builder.getLibraryType() != LIBRARY_TYPE.UNSTRANDED) {
				return true;
			}
		}

		return false;
	}
	
	public List<DataBuilder<T>> getDataBuilder() {
		return dataBuilders;
	}
	
	private List<SAMRecordWrapperIteratorProvider> createRecordIteratorProviders(
			final AbstractConditionParameter<T> conditionParameter) {
		
		final List<SAMRecordWrapperIteratorProvider> recordProvider = 
				new ArrayList<SAMRecordWrapperIteratorProvider>(conditionParameter.getReplicateSize());
		
		for (final String recordFilename : conditionParameter.getRecordFilenames()) {
			final SamReader samReader = AbstractConditionParameter.createSamReader(recordFilename);
			final SAMRecordWrapperIteratorProvider iteratorProvider = 
					new SAMRecordWrapperIteratorProvider(conditionParameter, samReader);
			recordProvider.add(iteratorProvider);
		}

		return recordProvider;
	}

	public int getReplicateSize() {
		return dataBuilders.size();
	}
	
}
