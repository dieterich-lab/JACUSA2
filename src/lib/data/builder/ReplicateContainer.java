package lib.data.builder;

import java.util.ArrayList;
import java.util.List;

import htsjdk.samtools.SamReader;

import jacusa.filter.FilterContainer;
import lib.cli.parameters.AbstractConditionParameter;
import lib.cli.parameters.AbstractParameters;
import lib.data.AbstractData;
import lib.data.builder.hasLibraryType.LIBRARY_TYPE;
import lib.location.CoordinateAdvancer;
import lib.location.CoordinateContainer;
import lib.util.Coordinate;

public class ReplicateContainer<T extends AbstractData> {

	private final AbstractConditionParameter<T> conditionParameter;
	private final AbstractParameters<T> parameters;

	private final List<SAMRecordWrapperIteratorProvider> recordIteratorProviders;
	private final List<AbstractDataBuilder<T>> dataBuilders;
	
	private CoordinateContainer coordinateContainer;

	
	public ReplicateContainer(final Coordinate window, 
			final AbstractConditionParameter<T> conditionParameter,
			final AbstractParameters<T> generalParameters) {
		this.conditionParameter = conditionParameter;
		this.parameters = generalParameters;

		recordIteratorProviders = createRecordIteratorProviders(conditionParameter);
		dataBuilders = createDataBuilders(conditionParameter, generalParameters);

		coordinateContainer = new CoordinateContainer(dataBuilders.toArray(new CoordinateAdvancer[dataBuilders.size()]));
	}

	public CoordinateContainer getCoordinateContainer() {
		return coordinateContainer;
	}

	public T[] getData(final Coordinate coordinate) {
		int replicateSize = conditionParameter.getReplicateSize();
		// create new container array
		T[] data = parameters.getMethodFactory().createReplicateData(replicateSize);

		for (int replicateIndex = 0; replicateIndex < replicateSize; ++replicateIndex) {
			final AbstractDataBuilder<T> dataBuilder = dataBuilders.get(replicateIndex);
			data[replicateIndex] = dataBuilder.getData(coordinate);
		}

		return data;
	}

	public List<FilterContainer<T>> getFilterContainers(final Coordinate coordinate) {
		int replicateSize = conditionParameter.getReplicateSize();

		List<FilterContainer<T>> filterContainers = new ArrayList<FilterContainer<T>>(replicateSize);
		
		for (final AbstractDataBuilder<T> dataBuilder : dataBuilders) {
			filterContainers.add(dataBuilder.getFilterContainer(coordinate));
		}

		return filterContainers;
	}
	
	private List<AbstractDataBuilder<T>> createDataBuilders(
			final AbstractConditionParameter<T> conditionParameter,
			final AbstractParameters<T> generalParameter) {

		for (int replicateIndex = 0; replicateIndex < conditionParameter.getReplicateSize(); ++replicateIndex) {
			final AbstractDataBuilder<T> builder = conditionParameter.getDataBuilderFactory()
					.newInstance(conditionParameter, generalParameter);
			dataBuilders.add(builder);
		}

		return dataBuilders;
	}

	public boolean isStranded() {
		for (final AbstractDataBuilder<T> builder : dataBuilders) {
			if (builder.getLibraryType() != LIBRARY_TYPE.UNSTRANDED) {
				return true;
			}
		}

		return false;
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
	
}
