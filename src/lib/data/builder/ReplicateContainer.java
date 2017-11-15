package lib.data.builder;

import java.util.ArrayList;
import java.util.List;

import htsjdk.samtools.SamReader;

import jacusa.filter.FilterContainer;
import lib.cli.parameters.AbstractConditionParameter;
import lib.cli.parameters.AbstractParameter;
import lib.data.AbstractData;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.builder.recordwrapper.SAMRecordWrapperIterator;
import lib.data.builder.recordwrapper.SAMRecordWrapperIteratorProvider;
import lib.data.has.hasLibraryType.LIBRARY_TYPE;
import lib.util.coordinate.Coordinate;

public class ReplicateContainer<T extends AbstractData> {

	private final AbstractConditionParameter<T> conditionParameter;
	private final AbstractParameter<T, ?> generalParameters;

	private final List<SAMRecordWrapperIteratorProvider> iteratorProviders;
	private final List<DataBuilder<T>> dataBuilders;
	
	public ReplicateContainer( 
			final AbstractConditionParameter<T> conditionParameter,
			final AbstractParameter<T, ?> generalParameters) {

		this.conditionParameter = conditionParameter;
		this.generalParameters = generalParameters;

		iteratorProviders = createRecordIteratorProviders(conditionParameter);
		dataBuilders = createDataBuilders(conditionParameter, generalParameters);
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

			// TODO test performance
			recordWrappers.add(dataBuilder.buildCache(activeWindowCoordinate, iterator));
			iterator.close();
		}

		return recordWrappers; 
	}

	// FIXME
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

	public List<FilterContainer<T>> getFilterContainers() {
		final int replicateSize = conditionParameter.getReplicateSize();
		final List<FilterContainer<T>> filterContainers = new ArrayList<FilterContainer<T>>(replicateSize);
		
		for (final DataBuilder<T> dataBuilder : dataBuilders) {
			filterContainers.add(dataBuilder.getFilterContainer());
		}

		return filterContainers;
	}
	
	private List<DataBuilder<T>> createDataBuilders(
			final AbstractConditionParameter<T> conditionParameter,
			final AbstractParameter<T, ?> generalParameter) {
		
		final List<DataBuilder<T>> dataBuilders = new ArrayList<DataBuilder<T>>(conditionParameter.getReplicateSize());

		for (int replicateIndex = 0; replicateIndex < conditionParameter.getReplicateSize(); ++replicateIndex) {
			final DataBuilder<T> builder = generalParameter.getMethodFactory().getDataBuilderFactory().newInstance(conditionParameter);
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
	
	private List<SAMRecordWrapperIteratorProvider> createRecordIteratorProviders(
			final AbstractConditionParameter<T> conditionParameter) {
		
		final List<SAMRecordWrapperIteratorProvider> recordProvider = 
				new ArrayList<SAMRecordWrapperIteratorProvider>(conditionParameter.getReplicateSize());
		
		for (final String recordFilename : conditionParameter.getRecordFilenames()) {
			final SamReader samReader = conditionParameter.createSamReader(recordFilename);
			final SAMRecordWrapperIteratorProvider iteratorProvider = 
					new SAMRecordWrapperIteratorProvider(conditionParameter, samReader);
			recordProvider.add(iteratorProvider);
		}

		return recordProvider;
	}

	
	/* TODO
	public Set<Integer> getAlleles(final Coordinate coordinate) {
		final Set<Integer> alleles = new HashSet<Integer>(4);

		for (final DataBuilder<T> builder : dataBuilders) {
			final int windowPosition = builder.getWindowCoordinates().convert2WindowPosition(coordinate.getStart());
			for (int baseIndex : builder.getWindowCache(coordinate.getStrand()).getAlleles(windowPosition)) {
				alleles.add(baseIndex);
			}
		}

		return alleles;
	}
	*/

	/**
	 * Get next valid record position within thread window
	 * @param target
	 * @return
	 */
	/* TODO
	public int getNextPosition(final Coordinate target) {
		int position = Integer.MAX_VALUE; 
		for (final DataBuilder<T> builder : dataBuilders) {
			final SAMRecord record = builder.getNextRecord(target.getPosition());
			if (record == null) {
				continue;
			}

			int genomicPosition = Math.max(target.getPosition(), record.getAlignmentStart());
			position = Math.min(position, genomicPosition);
		}

		return position;
	}
	*/

	/* TODO move to isValid 
	// Change here for more quantitative evaluation
	public boolean isCovered(final Coordinate coordinate) {
		for (final DataBuilder<T> builder : dataBuilders) {
			if (! isCovered(coordinate, builder)) {
				return false;
			}
		}

		return true;
	}

	private boolean isCovered(final Coordinate coordinate, final DataBuilder<T> builder) {
		final int windowPosition = builder.getWindowCoordinates().convert2WindowPosition(coordinate.getStart());

		if (windowPosition < 0) {
			return false;
		}

		return builder.getCoverage(windowPosition, coordinate.getStrand()) >= getCondition().getMinCoverage();
	}
	*/
	
	
}
