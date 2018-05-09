package lib.data.builder;

import jacusa.filter.FilterContainer;

import java.util.ArrayList;
import java.util.List;

import lib.cli.parameter.AbstractConditionParameter;
import lib.cli.parameter.AbstractParameter;
import lib.data.AbstractData;
import lib.data.builder.recordwrapper.SAMRecordWrapper;
import lib.data.cache.extractor.ReferenceSetter;
import lib.util.coordinate.CoordinateController;
import lib.util.coordinate.Coordinate;

public class ConditionContainer<T extends AbstractData> {

	private AbstractParameter<T, ?> parameter;
	private List<ReplicateContainer<T>> replicateContainers;

	private FilterContainer<T> filterContainer;
	
	public ConditionContainer(final AbstractParameter<T, ?> parameter) {
		this.parameter = parameter;
	}

	public List<List<List<SAMRecordWrapper>>> updateWindowCoordinates(final Coordinate activeWindowCoordinate, final Coordinate reservedWindowCoordinate) {
		final List<List<List<SAMRecordWrapper>>> recordWrappers = 
				new ArrayList<List<List<SAMRecordWrapper>>>(parameter.getConditionsSize());
		
		for (ReplicateContainer<T> replicateContainer : replicateContainers) {
			replicateContainer.createIterators(activeWindowCoordinate, reservedWindowCoordinate);
		}
		
		return recordWrappers;
	}
	
	public List<List<List<SAMRecordWrapper>>> updateActiveWindowCoordinates(final Coordinate activeWindowCoordinate) {
		final List<List<List<SAMRecordWrapper>>> recordWrappers = 
				new ArrayList<List<List<SAMRecordWrapper>>>(parameter.getConditionsSize());

		for (ReplicateContainer<T> replicateContainer : replicateContainers) {
			recordWrappers.add(replicateContainer.updateIterators(activeWindowCoordinate));
		}

		return recordWrappers;
	}
	
	public ReplicateContainer<T> getReplicatContainer(final int conditionIndex) {
		return replicateContainers.get(conditionIndex);
	}

	public T[][] getData(final Coordinate coordinate) {
		final int conditions = parameter.getConditionsSize();

		final T[][] data = parameter.getMethodFactory().getDataGenerator().createContainerData(conditions);
		for (int conditionIndex = 0; conditionIndex < conditions; conditionIndex++) {
			data[conditionIndex] = getReplicatContainer(conditionIndex).getData(coordinate);
		}

		return data;
	}

	public int getConditionSize() {
		return replicateContainers.size();
	}

	public AbstractParameter<T, ?> getParameter() {
		return parameter;
	}
	
	public void initReplicateContainer(
			final ReferenceSetter<T> referenceSetter, 
			final CoordinateController coordinateController,
			final AbstractParameter<T, ?> parameter) {

		filterContainer = parameter.getFilterConfig().createFilterInstances(coordinateController);
		parameter.getFilterConfig().registerFilters(coordinateController, this);
		
		replicateContainers = 
				new ArrayList<ReplicateContainer<T>>(parameter.getConditionsSize());

		for (final AbstractConditionParameter<T> conditionParameter : parameter.getConditionParameters()) {
			final ReplicateContainer<T> replicateContainer = 
					new ReplicateContainer<T>(this, referenceSetter, coordinateController, conditionParameter, parameter);
			replicateContainers.add(replicateContainer);
		}
	}

	public FilterContainer<T> getFilterContainer() {
		return filterContainer;
	}
	
	public List<AbstractConditionParameter<T>> getConditionParameter() {
		return parameter.getConditionParameters();
	}
	
}