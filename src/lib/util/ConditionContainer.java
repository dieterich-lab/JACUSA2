package lib.util;

import jacusa.filter.FilterContainer;

import java.util.List;
import java.util.stream.Collectors;

import lib.cli.parameter.ConditionParameter;
import lib.cli.parameter.GeneralParameter;
import lib.data.DataContainer;
import lib.data.storage.container.SharedStorage;
import lib.util.coordinate.Coordinate;

public class ConditionContainer {

	private GeneralParameter prm;
	private List<ReplicateContainer> repContainers;

	private List<Integer> repSizes;
	
	private FilterContainer filterContainer;

	public ConditionContainer(final GeneralParameter prm) {
		this.prm = prm;
	}

	public void updateWindowCoordinates(final Coordinate activeWindowCoordinate) {
		repContainers.stream()
			.forEach(r -> r.createIterators(activeWindowCoordinate));
	}
	
	public void updateActiveWinCoord(final Coordinate activeWindowCoordinate) {
		repContainers.stream()
			.forEach(r -> r.updateIterators(activeWindowCoordinate));
	}
	
	public ReplicateContainer getReplicatContainer(final int condI) {
		return repContainers.get(condI);
	}

	public DataContainer getNullDataContainer(final int condI, final int replicateI, final Coordinate coordinate) {
		return repContainers.get(condI).getNullDataContainer(replicateI, coordinate);
	}
	
	public DataContainer getDefaultDataContainer(final int condI, final int replicateI, final Coordinate coordinate) {
		return repContainers.get(condI).getDefaultDataContainer(replicateI, coordinate);
	}

	public List<Integer> getReplicateSizes() {
		return repSizes;
	}
	
	public int getConditionSize() {
		return repContainers.size();
	}
	
	public void initReplicateContainer(
			final SharedStorage sharedStorage,
			final GeneralParameter parameter,
			final AbstractMethod<?> method) {

		filterContainer = parameter.getFilterConfig().createFilterContainer();
		parameter.getFilterConfig().registerFilters(sharedStorage.getCoordinateController(), this);
		repContainers = parameter.getConditionParameters().stream()
				.map(cp -> new ReplicateContainer(
						parameter, filterContainer, sharedStorage, cp, method))
				.collect(Collectors.toList());
		repSizes = repContainers.stream()
				.map(rc -> rc.getReplicateSize())
				.collect(Collectors.toList());
	}

	public FilterContainer getFilterContainer() {
		return filterContainer;
	}

	public List<ConditionParameter> getConditionParameter() {
		return prm.getConditionParameters();
	}
	
}