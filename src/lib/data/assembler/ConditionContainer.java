package lib.data.assembler;

import jacusa.filter.FilterContainer;

import java.util.List;
import java.util.stream.Collectors;

import lib.cli.parameter.ConditionParameter;
import lib.cli.parameter.GeneralParameter;
import lib.data.DataTypeContainer;
import lib.data.cache.container.SharedCache;
import lib.method.AbstractMethod;
import lib.util.coordinate.Coordinate;

public class ConditionContainer {

	private GeneralParameter parameter;
	private List<ReplicateContainer> replicateContainers;

	private List<Integer> replicateSizes;
	
	private FilterContainer filterContainer;

	public ConditionContainer(final GeneralParameter parameter) {
		this.parameter = parameter;
	}

	public void updateWindowCoordinates(final Coordinate activeWindowCoordinate) {
		replicateContainers.stream()
			.forEach(r -> r.createIterators(activeWindowCoordinate));
	}
	
	public void updateActiveWindowCoordinates(final Coordinate activeWindowCoordinate) {
		replicateContainers.stream()
			.forEach(r -> r.updateIterators(activeWindowCoordinate));
	}
	
	public ReplicateContainer getReplicatContainer(final int conditionIndex) {
		return replicateContainers.get(conditionIndex);
	}

	public DataTypeContainer getDataContainer(final int conditionIndex, final int replicateIndex, final Coordinate coordinate) {
		return replicateContainers.get(conditionIndex).getDataContainer(replicateIndex, coordinate);
	}

	public List<Integer> getReplicateSizes() {
		return replicateSizes;
	}
	
	public int getConditionSize() {
		return replicateContainers.size();
	}
	
	public void initReplicateContainer(
			final SharedCache sharedCache,
			final GeneralParameter parameter,
			final AbstractMethod method) {

		filterContainer = parameter.getFilterConfig().createFilterInstances();
		parameter.getFilterConfig().registerFilters(sharedCache.getCoordinateController(), this);
		replicateContainers = parameter.getConditionParameters().stream()
				.map(cp -> new ReplicateContainer(
						parameter, filterContainer, sharedCache, cp, method))
				.collect(Collectors.toList());
		replicateSizes = replicateContainers.stream()
				.map(rc -> rc.getReplicateSize())
				.collect(Collectors.toList());
	}

	public FilterContainer getFilterContainer() {
		return filterContainer;
	}

	public List<ConditionParameter> getConditionParameter() {
		return parameter.getConditionParameters();
	}
	
}