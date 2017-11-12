package jacusa.filter.factory;

import jacusa.filter.AbstractFilter;
import jacusa.filter.FilterContainer;
import jacusa.filter.HomopolymerFilter;
import jacusa.filter.storage.HomopolymerStorage;
import lib.data.AbstractData;
import lib.data.generator.DataGenerator;
import lib.data.has.hasBaseCallCount;
import lib.data.has.hasReferenceBase;

public class HomopolymerFilterFactory<T extends AbstractData & hasBaseCallCount & hasReferenceBase, F extends AbstractData & hasBaseCallCount> 
extends AbstractFilterFactory<T, F> {

	private static final int LENGTH = 7;
	private int length;
		
	public HomopolymerFilterFactory(final DataGenerator<F> dataGenerator) {
		super('Y', 
				"Filter wrong variant calls within homopolymers. Default: " + LENGTH + " (Y:length)", 
				dataGenerator);
		length = LENGTH;
	}
	
	@Override
	public void processCLI(final String line) throws IllegalArgumentException {
		if(line.length() == 1) {
			throw new IllegalArgumentException("Invalid argument " + line);
		}

		String[] s = line.split(Character.toString(AbstractFilterFactory.SEP));
		// format Y:length
		for (int i = 1; i < s.length; ++i) {
			int value = Integer.valueOf(s[i]);

			switch(i) {
			case 1:
				setLength(value);
				break;

			default:
				throw new IllegalArgumentException("Invalid argument " + length);
			}
		}
	}

	@Override
	public void registerFilter(final FilterContainer<T> filterContainer) {
		filterContainer.add(getFilter());

		// TODO
		HomopolymerStorage<F> storage = new HomopolymerStorage<F>(getC(), length, null);
		
		filterContainer.registerStorage(storage);
		filterContainer.registerProcessAlignment(storage);
	}

	@Override
	public AbstractFilter<T> getFilter() {
		return new HomopolymerFilter<T, F>(getC(), length, this);
	}
	
	public final void setLength(int length) {
		this.length = length;
	}

	public final int getLength() {
		return length;
	}

}