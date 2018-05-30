package lib.data.builder;

import java.util.ArrayList;
import java.util.List;

import htsjdk.samtools.SamReader;

import lib.cli.parameter.AbstractConditionParameter;
import lib.cli.parameter.AbstractParameter;
import lib.data.AbstractData;
import lib.data.builder.recordwrapper.SAMRecordWrapperIterator;
import lib.data.builder.recordwrapper.SAMRecordWrapperIteratorProvider;
import lib.data.cache.extractor.ReferenceSetter;
import lib.data.cache.record.RecordWrapperDataCache;
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
			final ReferenceSetter<T> referenceSetter,
			final CoordinateController coordinateController,
			final AbstractConditionParameter<T> conditionParameter,
			final AbstractParameter<T, ?> parameter) {

		this.conditionContainer = conditionContainer;
		
		this.conditionParameter = conditionParameter;
		this.generalParameters = parameter;

		iteratorProviders = createRecordIteratorProviders(conditionParameter);
		dataBuilders = createDataBuilders(referenceSetter, coordinateController, conditionParameter, parameter);
	}

	public List<SAMRecordWrapperIteratorProvider> getIteratorProviders() {
		return iteratorProviders;
	}
	
	public void createIterators(final Coordinate activeWindowCoordinate,
			final Coordinate reservedWindowCoordinate) {

		for (int replicateIndex = 0; replicateIndex < conditionParameter.getReplicateSize(); replicateIndex++) {
			final SAMRecordWrapperIteratorProvider iteratorProvider = iteratorProviders.get(replicateIndex);
			final SAMRecordWrapperIterator iterator = 
					iteratorProvider.createIterator(activeWindowCoordinate);
			final DataBuilder<T> dataBuilder = dataBuilders.get(replicateIndex);
			dataBuilder.buildCache(activeWindowCoordinate, iterator);
			iterator.close();
		}
	}

	public void updateIterators(final Coordinate activeWindowCoordinate) {
		createIterators(activeWindowCoordinate, null);
	}
	
	public T[] getData(final Coordinate coordinate) {
		int replicateSize = conditionParameter.getReplicateSize();
		// create new container array
		T[] data = generalParameters.getMethodFactory().getDataGenerator().createReplicateData(replicateSize);

		for (int replicateIndex = 0; replicateIndex < replicateSize; ++replicateIndex) {
			final DataBuilder<T> dataBuilder = dataBuilders.get(replicateIndex);
			data[replicateIndex] = dataBuilder.getData(coordinate);
		}

		return data;
	}
	
	private List<DataBuilder<T>> createDataBuilders(
			final ReferenceSetter<T> referenceSetter,
			final CoordinateController coordinateController,
			final AbstractConditionParameter<T> conditionParameter,
			final AbstractParameter<T, ?> parameter) {
		
		final List<DataBuilder<T>> dataBuilders = new ArrayList<DataBuilder<T>>(conditionParameter.getReplicateSize());

		for (int replicateIndex = 0; replicateIndex < conditionParameter.getReplicateSize(); ++replicateIndex) {
			final List<RecordWrapperDataCache<?>> filterCaches = 
					conditionContainer.getFilterContainer().getFilterCaches(conditionParameter.getConditionIndex(), replicateIndex);

			final DataBuilder<T> builder = 
					parameter.getMethodFactory().getDataBuilderFactory().newInstance(referenceSetter, replicateIndex, coordinateController, conditionParameter, filterCaches);
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
