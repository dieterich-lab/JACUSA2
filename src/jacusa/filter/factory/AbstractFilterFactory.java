package jacusa.filter.factory;

import jacusa.filter.AbstractFilter;
import jacusa.filter.FilterContainer;
import lib.data.AbstractData;
import lib.data.generator.DataGenerator;
import lib.data.has.hasLibraryType.LIBRARY_TYPE;
import lib.util.Coordinate;

public abstract class AbstractFilterFactory<T extends AbstractData, F extends AbstractData> 
implements DataGenerator<F> {

	public final static char SEP = ':';

	private final char c;
	protected String desc;
	private final DataGenerator<F> dataGenerator;

	public AbstractFilterFactory(final char c, final String desc, final DataGenerator<F> dataGenerator) {
		this.c 				= c;
		this.desc 			= desc;
		this.dataGenerator 	= dataGenerator;
	}

	public char getC() {
		return c;
	}

	public String getDesc() {
		return desc;
	}

	public void processCLI(final String line) throws IllegalArgumentException {
		// implement to change behavior via CLI
	}

	public abstract void registerFilter(final FilterContainer<T> filterContainer);
	public abstract AbstractFilter<T> getFilter();

	@Override
	public F[][] copyContainerData(final F[][] containerData) {
		return dataGenerator.copyContainerData(containerData);
	}
	
	@Override
	public F copyData(final F data) {
		return dataGenerator.copyData(data);
	}
	
	@Override
	public F[] copyReplicateData(final F[] replicateData) {
		return dataGenerator.copyReplicateData(replicateData);
	}
	
	@Override
	public F[][] createContainerData(final int n) {
		return dataGenerator.createContainerData(n);
	}
	
	@Override
	public F createData(LIBRARY_TYPE libraryType, Coordinate coordinate) {
		return dataGenerator.createData(null, null);
	}
	
	@Override
	public F[] createReplicateData(final int n) {
		return dataGenerator.createReplicateData(n);
	}
	
} 