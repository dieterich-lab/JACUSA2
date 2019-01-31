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
import lib.data.storage.container.SharedStorage;
import lib.util.coordinate.Coordinate;
import lib.recordextended.SAMRecordExtendedIterator;
import lib.recordextended.SAMRecordExtendedIteratorProvider;

public class ReplicateContainer {

	private final ConditionParameter conditionParameter;
	
	private final List<SAMRecordExtendedIteratorProvider> iteratorProviders;
	private final List<DataAssembler> dataAssemblers;
	
	public ReplicateContainer(
			final GeneralParameter parameter,
			final FilterContainer filterContainer,
			final SharedStorage sharedStorage,
			final ConditionParameter conditionParameter,
			final AbstractMethod method) {

		this.conditionParameter = conditionParameter;

		iteratorProviders = createRecordIteratorProviders(conditionParameter);
		dataAssemblers = createDataAssemblers(
				parameter, filterContainer, sharedStorage, conditionParameter, method);
	}

	public List<SAMRecordExtendedIteratorProvider> getIteratorProviders() {
		return iteratorProviders;
	}
	
	public void createIterators(final Coordinate activeWindowCoordinate) {
		for (int replicateIndex = 0; replicateIndex < conditionParameter.getReplicateSize(); ++replicateIndex) {
			final SAMRecordExtendedIterator recordIterator = 
					iteratorProviders.get(replicateIndex).getIterator(activeWindowCoordinate);
			final DataAssembler dataAssembler = dataAssemblers.get(replicateIndex);
			dataAssembler.buildCache(activeWindowCoordinate, recordIterator);
			recordIterator.close();
		}
	}

	public void updateIterators(final Coordinate activeWindowCoordinate) {
		createIterators(activeWindowCoordinate);
	}
	
	public DataContainer getDataContainer(final int replicateIndex, final Coordinate coordinate) {
		return dataAssemblers.get(replicateIndex).assembleData(coordinate);
	}
	
	private List<DataAssembler> createDataAssemblers(
			final GeneralParameter parameter,
			final FilterContainer filterContainer,
			final SharedStorage sharedStorage,
			final ConditionParameter conditionParameter,
			final AbstractMethod method) {

		final List<DataAssembler> dataAssemblers = new ArrayList<DataAssembler>(conditionParameter.getReplicateSize());
		for (int replicateIndex = 0; replicateIndex < conditionParameter.getReplicateSize(); ++replicateIndex) {
			dataAssemblers.add(
				method.getDataAssemblerFactory()
					.newInstance(
							parameter, filterContainer, sharedStorage, conditionParameter, replicateIndex) );
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
	
	private List<SAMRecordExtendedIteratorProvider> createRecordIteratorProviders(
			final ConditionParameter conditionParameter) {

		return Stream.of(conditionParameter.getRecordFilenames())
				.map(ConditionParameter::createSamReader)
				.map(s -> new SAMRecordExtendedIteratorProvider(conditionParameter, s))
				.collect(Collectors.toList());
	}

	public int getReplicateSize() {
		return dataAssemblers.size();
	}
	
}
