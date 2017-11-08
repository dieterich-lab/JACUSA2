package jacusa.pileup.builder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import htsjdk.samtools.SamReader;
import htsjdk.samtools.SAMRecord;

import jacusa.JACUSA;
import jacusa.filter.FilterContainer;
import jacusa.pileup.iterator.location.CoordinateAdvancer;
import jacusa.pileup.iterator.location.CoordinateContainer;
import lib.cli.parameters.AbstractParameters;
import lib.cli.parameters.JACUSAConditionParameters;
import lib.data.AbstractData;
import lib.util.Coordinate;
import lib.util.WindowCoordinate;

public class ReplicateContainer<T extends AbstractData> {

	private final int conditionIndex;
	private final AbstractParameters<T> parameters;
	
	private final List<DataBuilder<T>> builders;
	private CoordinateContainer coordinateContainer;

	public ReplicateContainer(final Coordinate window, 
			final int conditionIndex,
			final SAMFileReader[] readers,
			final AbstractParameters<T> parameters) {
		this.conditionIndex = conditionIndex;
		this.parameters 	= parameters;

		builders 			= createBuilders(window, conditionIndex, readers, parameters);
		coordinateContainer = new CoordinateContainer(builders.toArray(new CoordinateAdvancer[builders.size()]));
	}

	public CoordinateContainer getCoordinateContainer() {
		return coordinateContainer;
	}

	public T[] getData(final Coordinate coordinate) {
		int replicates = builders.size();

		// create new container array
		T[] data = parameters.getMethodFactory().createReplicateData(builders.size());
		for (int replicateIndex = 0; replicateIndex < replicates; ++replicateIndex) {
			final DataBuilder<T> builder = builders.get(replicateIndex);
			final int windowPosition = builder.getWindowCoordinates()
					.convert2WindowPosition(coordinate.getStart());

			data[replicateIndex] = builder.getData(windowPosition, coordinate.getStrand());
		}

		return data;
	}

	public List<FilterContainer<T>> getFilterContainers(final Coordinate coordinate) {
		int replicates = builders.size();

		List<FilterContainer<T>> filterContainers = new ArrayList<FilterContainer<T>>(replicates);
		
		for (final DataBuilder<T> builder : builders) {
			final int windowPosition = builder.getWindowCoordinates()
					.convert2WindowPosition(coordinate.getStart());

			filterContainers.add(builder.getFilterContainer(windowPosition, coordinate.getStrand()));
		}

		return filterContainers;
	}
	
	private List<DataBuilder<T>> createBuilders(final Coordinate window, 
			final int conditionIndex, final SAMFileReader[] readers,
			final AbstractParameters<T> parameters) {
		final JACUSAConditionParameters<T> condition = parameters.getConditionParameters(conditionIndex);
		final List<DataBuilder<T>> builders = new ArrayList<DataBuilder<T>>(readers.length);

		final String contig = window.getContig();
		for (int replicateIndex = 0; replicateIndex < readers.length; ++replicateIndex) {
			final int sequenceLength = readers[replicateIndex]
					.getFileHeader()
					.getSequence(contig)
					.getSequenceLength();
	
			if (window.getEnd() > sequenceLength) {
				Coordinate samHeader = new Coordinate(window.getContig(), 1, sequenceLength);
				JACUSA.printWarning("Coordinates in BED file (" + window.toString() + 
						") exceed SAM sequence header (" + samHeader.toString()+ ").");
			}
	
			final WindowCoordinate windowCoordinates = new WindowCoordinate(
					window.getContig(), 
					window.getStart(), 
					parameters.getActiveWindowSize(),
					window.getEnd());

			DataBuilder<T> builder = condition.getDataBuilderFactory()
					.newInstance(windowCoordinates, readers[replicateIndex], condition, parameters);
			builders.add(builder);
		}

		return builders;
	}

	public boolean isStranded() {
		for (final DataBuilder<T> builder : builders) {
			if (builder.getLibraryType() != LIBRARY_TYPE.UNSTRANDED) {
				return true;
			}
		}
		
		return false;
	}
	
	public Set<Integer> getAlleles(final Coordinate coordinate) {
		final Set<Integer> alleles = new HashSet<Integer>(4);

		for (final DataBuilder<T> builder : builders) {
			final int windowPosition = builder.getWindowCoordinates().convert2WindowPosition(coordinate.getStart());
			for (int baseIndex : builder.getWindowCache(coordinate.getStrand()).getAlleles(windowPosition)) {
				alleles.add(baseIndex);
			}
		}

		return alleles;
	}

	/**
	 * Get next valid record position within thread window
	 * @param target
	 * @return
	 */
	public int getNextPosition(final Coordinate target) {
		int position = Integer.MAX_VALUE; 
		for (final DataBuilder<T> builder : builders) {
			final SAMRecord record = builder.getNextRecord(target.getPosition());
			if (record == null) {
				continue;
			}

			int genomicPosition = Math.max(target.getPosition(), record.getAlignmentStart());
			position = Math.min(position, genomicPosition);
		}

		return position;
	}

	// Change here for more quantitative evaluation
	public boolean isCovered(final Coordinate coordinate) {
		for (final DataBuilder<T> builder : builders) {
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
	
	private JACUSAConditionParameters<T> getCondition() {
		return parameters.getConditionParameters(conditionIndex);
	}

}
