package jacusa.filter.factory;

import jacusa.filter.AbstractFilter;
import jacusa.filter.FilterContainer;
import jacusa.filter.HomopolymerFilter;
import jacusa.filter.storage.HomopolymerStorage;
import lib.cli.parameters.AbstractParameter;
import lib.data.basecall.PileupData;

public class HomopolymerFilterFactory<T extends PileupData> 
extends AbstractFilterFactory<T> {

	private static final int LENGTH = 7;
	private int length;
	private AbstractParameter<T> parameters;
		
	public HomopolymerFilterFactory(final AbstractParameter<T> parameters) {
		super('Y', "Filter wrong variant calls within homopolymers. Default: " + LENGTH + " (Y:length)");
		this.parameters = parameters;
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

		HomopolymerStorage<T> storage = 
					new HomopolymerStorage<T>(getC(), length, parameters.getBaseConfig());
		
		filterContainer.registerWindowStorage(storage);
		filterContainer.registerProcessAlignment(storage);
	}

	@Override
	public AbstractFilter<T> getFilter() {
		return new HomopolymerFilter<T>(getC(), length, parameters);
	}
	
	public final void setLength(int length) {
		this.length = length;
	}

	public final int getLength() {
		return length;
	}

}