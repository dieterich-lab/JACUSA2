package lib.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jacusa.filter.FilterContainer;
import lib.cli.parameter.ConditionParameter;
import lib.cli.parameter.GeneralParameter;
import lib.data.DataContainer;
import lib.data.assembler.DataAssembler;
import lib.data.assembler.DataAssembler.CACHE_STATUS;
import lib.data.storage.container.SharedStorage;
import lib.record.RecordIterator;
import lib.record.RecordIteratorProvider;
import lib.util.coordinate.Coordinate;

public class ReplicateContainer {

	private final ConditionParameter condPrm;
	
	private final List<RecordIteratorProvider> itProvs;
	private final List<DataAssembler> dataAssemblers;
	
	public ReplicateContainer(
			final GeneralParameter parameter,
			final FilterContainer filterContainer,
			final SharedStorage sharedStorage,
			final ConditionParameter conditionParameter,
			final AbstractMethod<?> method) {

		this.condPrm = conditionParameter;

		itProvs = createRecordIteratorProviders(conditionParameter);
		dataAssemblers = createDataAssemblers(
				parameter, filterContainer, sharedStorage, conditionParameter, method);
	}

	public List<RecordIteratorProvider> getIteratorProviders() {
		return itProvs;
	}
	
	public void createIterators(final Coordinate activeWindowCoordinate) {
		for (int replicateI = 0; replicateI < condPrm.getReplicateSize(); ++replicateI) {
			final RecordIterator recordIterator = 
					itProvs.get(replicateI).getIterator(activeWindowCoordinate);
			final DataAssembler dataAssembler = dataAssemblers.get(replicateI);
			dataAssembler.buildCache(activeWindowCoordinate, recordIterator);
			recordIterator.close();
		}
	}

	public void updateIterators(final Coordinate activeWindowCoordinate) {
		createIterators(activeWindowCoordinate);
	}
	
	// returns null if cache is empty
	public DataContainer getNullDataContainer(final int replicateI, final Coordinate coordinate) {
		if (dataAssemblers.get(replicateI).getCacheStatus() == CACHE_STATUS.CACHED) {
			return dataAssemblers.get(replicateI).assembleData(coordinate);
		}
		
		return null;
	}
	// returns default container if cache emptry
	public DataContainer getDefaultDataContainer(final int replicateI, final Coordinate coordinate) {
		if (dataAssemblers.get(replicateI).getCacheStatus() == CACHE_STATUS.CACHED) {
			return dataAssemblers.get(replicateI).assembleData(coordinate);
		}
		
		return 	dataAssemblers.get(replicateI).createDefaultDataContainer(coordinate);
	}
	
	private List<DataAssembler> createDataAssemblers(
			final GeneralParameter parameter,
			final FilterContainer filterContainer,
			final SharedStorage sharedStorage,
			final ConditionParameter conditionParameter,
			final AbstractMethod<?> method) {

		final List<DataAssembler> dataAssemblers = new ArrayList<DataAssembler>(conditionParameter.getReplicateSize());
		for (int replicateI = 0; replicateI < conditionParameter.getReplicateSize(); ++replicateI) {
			dataAssemblers.add(
				method.getDataAssemblerFactory()
					.newInstance(
							parameter, filterContainer, sharedStorage, conditionParameter, replicateI) );
		}
		return dataAssemblers;
	}

	public boolean isStranded() {
		for (final DataAssembler dataAssembler : dataAssemblers) {
			if (dataAssembler.getLibraryType() != LibraryType.UNSTRANDED) {
				return true;
			}
		}
		return false;
	}
	
	public List<DataAssembler> getDataAssemblers() {
		return dataAssemblers;
	}
	
	private List<RecordIteratorProvider> createRecordIteratorProviders(
			final ConditionParameter conditionParameter) {

		return Stream.of(conditionParameter.getRecordFilenames())
				.map(f -> new RecordIteratorProvider(conditionParameter, f))
				.collect(Collectors.toList());
	}

	public int getReplicateSize() {
		return dataAssemblers.size();
	}
	
}
