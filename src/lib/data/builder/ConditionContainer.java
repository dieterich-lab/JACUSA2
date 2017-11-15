package lib.data.builder;

import jacusa.filter.FilterContainer;

import java.util.ArrayList;
import java.util.List;

import lib.cli.parameters.AbstractConditionParameter;
import lib.cli.parameters.AbstractParameter;
import lib.data.AbstractData;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.util.coordinate.Coordinate;

public class ConditionContainer<T extends AbstractData> {

	private AbstractParameter<T, ?> generalParameter;
	
	private List<ReplicateContainer<T>> replicateContainers;

	public ConditionContainer(final AbstractParameter<T, ?> generalParameter) {
		this.generalParameter = generalParameter;

		replicateContainers = initReplicateContainer(generalParameter);
	}

	public List<List<List<SAMRecordWrapper>>> updateWindowCoordinates(final Coordinate activeWindowCoordinate, final Coordinate reservedWindowCoordinate) {
		final List<List<List<SAMRecordWrapper>>> recordWrappers = 
				new ArrayList<List<List<SAMRecordWrapper>>>(generalParameter.getConditionsSize());
		
		for (ReplicateContainer<T> replicateContainer : replicateContainers) {
			replicateContainer.createIterators(activeWindowCoordinate, reservedWindowCoordinate);
		}
		
		return recordWrappers;
	}
	
	public List<List<List<SAMRecordWrapper>>> updateActiveWindowCoordinates(final Coordinate activeWindowCoordinate) {
		final List<List<List<SAMRecordWrapper>>> recordWrappers = 
				new ArrayList<List<List<SAMRecordWrapper>>>(generalParameter.getConditionsSize());

		for (ReplicateContainer<T> replicateContainer : replicateContainers) {
			recordWrappers.add(replicateContainer.updateIterators(activeWindowCoordinate));
		}

		return recordWrappers;
	}
	
	public ReplicateContainer<T> getReplicatContainer(final int conditionIndex) {
		return replicateContainers.get(conditionIndex);
	}

	public T[][] getData(final Coordinate coordinate) {
		final int conditions = generalParameter.getConditionsSize();

		final T[][] data = generalParameter.getMethodFactory().createContainerData(conditions);
		for (int conditionIndex = 0; conditionIndex < conditions; conditionIndex++) {
			data[conditionIndex] = getReplicatContainer(conditionIndex).getData(coordinate);
		}

		return data;
	}

	public List<List<FilterContainer<T>>> getFilterContainer() {
		final List<List<FilterContainer<T>>> filterContainers 
			= new ArrayList<List<FilterContainer<T>>>(generalParameter.getConditionsSize());

		for (ReplicateContainer<T> replicateContainer : replicateContainers) {
			filterContainers.add(replicateContainer.getFilterContainers());
		}
		return filterContainers;
	}
	
	/*
	public F[][] getFilteredData(final Coordinate coordinate) {
		// final int genomicPosition = coordinate.getStart();
		// final char referenceBase = result.getParellelData().getCombinedPooledData().getReferenceBase();
		
		// create container [condition][replicates]
		final F[][] baseQualData = new PileupData[parallelData.getConditions()][];

		for (int conditionIndex = 0; conditionIndex < parallelData.getConditions(); ++conditionIndex) {
			// filter container per condition
			final List<FilterContainer<T>> filterContainers = conditionContainer
					.getReplicatContainer(conditionIndex).getFilterContainers(coordinate);

			// replicates for condition
			int replicates = filterContainers.size();
			
			// container for replicates of a condition
			PileupData[] replicatesData = new PileupData[replicates];

			// collect data from each replicate
			for (int replicateIndex = 0; replicateIndex < replicates; replicateIndex++) {
				// replicate specific filter container
				final FilterContainer<T> filterContainer = filterContainers.get(replicateIndex);
				// filter storage associated with filter and replicate
				final AbstractCacheStorage<T> storage = filterContainer.getStorage(getC());
				// convert genomic to window/storage speficic coordinates
				final int windowPosition = storage.getBaseCallCache().getWindowCoordinates().convert2WindowPosition(genomicPosition);

				PileupData replicateData = new PileupData(coordinate, referenceBase, filterContainer.getCondition().getLibraryType());
				replicateData.setBaseQualCount(storage.getBaseCallCache().getBaseCallCount(windowPosition).copy());
				replicatesData[replicateIndex] = replicateData;
			}
		}
	}
	*/

	/* TODO
	public int getAlleleCount(final Coordinate coordinate) {
		final int conditions = generalParameter.getConditionsSize();
		final Set<Integer> alleles = new HashSet<Integer>(4);

		for (int conditionIndex = 0; conditionIndex < conditions; conditionIndex++) {
			alleles.addAll(getReplicatContainer(conditionIndex).getAlleles(coordinate));
		}

		return alleles.size();
	}

	public int getAlleleCount(final int conditionIndex, final Coordinate coordinate) {
		return getReplicatContainer(conditionIndex).getAlleles(coordinate).size();
	}
	*/

	/* TODO move somewhere else
	public boolean hasNext() {
		while (checkReferenceWithinWindow()) {
			if (! isCovered(referenceAdvancer.getCurrentCoordinate())) {
				// TODO make it faster
				
				if (! advance()) {
					return false;
				}
			} else {
				return true;
			}
		}

		return false;
	}
	*/ 

	private List<ReplicateContainer<T>> initReplicateContainer(
			final AbstractParameter<T, ?> generalParameter) {

		final List<ReplicateContainer<T>> replicateContainers = 
				new ArrayList<ReplicateContainer<T>>(generalParameter.getConditionsSize());

		for (final AbstractConditionParameter<T> conditionParameter : generalParameter.getConditionParameters()) {
			final ReplicateContainer<T> replicateContainer = new ReplicateContainer<T>(conditionParameter, generalParameter);
			replicateContainers.add(replicateContainer);
		}

		return replicateContainers;
	}

}