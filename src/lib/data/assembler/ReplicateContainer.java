package lib.data.assembler;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jacusa.filter.FilterContainer;
import lib.cli.parameter.AbstractConditionParameter;
import lib.cli.parameter.AbstractParameter;
import lib.data.DataTypeContainer;
import lib.data.builder.recordwrapper.SAMRecordWrapperIterator;
import lib.data.builder.recordwrapper.SAMRecordWrapperIteratorProvider;
import lib.data.cache.container.SharedCache;
import lib.data.has.LibraryType;
import lib.method.AbstractMethod;
import lib.util.coordinate.Coordinate;

public class ReplicateContainer {

	private final AbstractConditionParameter conditionParameter;
	
	private final List<SAMRecordWrapperIteratorProvider> iteratorProviders;
	private final List<DataAssembler> dataAssemblers;
	
	public ReplicateContainer(
			final AbstractParameter parameter,
			final FilterContainer filterContainer,
			final SharedCache sharedCache,
			final AbstractConditionParameter conditionParameter,
			final AbstractMethod method) {

		this.conditionParameter = conditionParameter;

		iteratorProviders = createRecordIteratorProviders(conditionParameter);
		dataAssemblers = createDataAssemblers(
				parameter, filterContainer, sharedCache, conditionParameter, method);
	}

	public List<SAMRecordWrapperIteratorProvider> getIteratorProviders() {
		return iteratorProviders;
	}
	
	public void createIterators(final Coordinate activeWindowCoordinate) {
		for (int replicateIndex = 0; replicateIndex < conditionParameter.getReplicateSize(); ++replicateIndex) {
			final SAMRecordWrapperIterator recordIterator = 
					iteratorProviders.get(replicateIndex).getIterator(activeWindowCoordinate);
			final DataAssembler dataAssembler = dataAssemblers.get(replicateIndex);
			dataAssembler.buildCache(activeWindowCoordinate, recordIterator);
			recordIterator.close();
		}
	}

	public void updateIterators(final Coordinate activeWindowCoordinate) {
		createIterators(activeWindowCoordinate);
	}
	
	public DataTypeContainer getDataContainer(final int replicateIndex, final Coordinate coordinate) {
		return dataAssemblers.get(replicateIndex).assembleData(coordinate);
	}
	
	private List<DataAssembler> createDataAssemblers(
			final AbstractParameter parameter,
			final FilterContainer filterContainer,
			final SharedCache sharedCache,
			final AbstractConditionParameter conditionParameter,
			final AbstractMethod method) {

		final List<DataAssembler> dataAssemblers = new ArrayList<DataAssembler>(conditionParameter.getReplicateSize());
		for (int replicateIndex = 0; replicateIndex < conditionParameter.getReplicateSize(); ++replicateIndex) {
			dataAssemblers.add(
				method.getDataAssemblerFactory()
					.newInstance(
							parameter, filterContainer, sharedCache, conditionParameter, replicateIndex) );
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
	
	private List<SAMRecordWrapperIteratorProvider> createRecordIteratorProviders(
			final AbstractConditionParameter conditionParameter) {

		return Stream.of(conditionParameter.getRecordFilenames())
				.map(AbstractConditionParameter::createSamReader)
				.map(s -> new SAMRecordWrapperIteratorProvider(conditionParameter, s))
				.collect(Collectors.toList());
	}

	public int getReplicateSize() {
		return dataAssemblers.size();
	}
	
}
