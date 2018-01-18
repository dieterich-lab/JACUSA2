package jacusa.filter.factory;

import jacusa.filter.cache.FilterCache;
import lib.cli.options.BaseCallConfig;
import lib.cli.parameter.AbstractConditionParameter;
import lib.data.AbstractData;
import lib.data.builder.ConditionContainer;
import lib.data.generator.DataGenerator;
import lib.data.has.hasBaseCallCount;
import lib.data.has.hasReferenceBase;
import lib.tmp.CoordinateController;

public class HomopolymerFilterFactory<T extends AbstractData & hasBaseCallCount & hasReferenceBase, F extends AbstractData & hasBaseCallCount> 
extends AbstractDataFilterFactory<T, F> {

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
	protected FilterCache<F> createFilterCache(
			AbstractConditionParameter<T> conditionParameter,
			BaseCallConfig baseCallConfig,
			CoordinateController coordinateController) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void registerFilter(CoordinateController coordinateController,
			ConditionContainer<T> conditionContainer) {
		// TODO Auto-generated method stub
		
	}
	
	/*
	@Override
	public void registerFilter(final FilterContainer<T> filterContainer) {
		final HomopolymerFilterCache filterCache = 
				new HomopolymerFilterCache(getC(), length, filterContainer.getCoordinateController());
		filterContainer.addFilterCache(filterCache);
	}
	
	@Override
	public void registerFilter(ConditionContainer<T> conditionContainer) {
		// TODO Auto-generated method stub
		conditionContainer.getFilterContainer().addDataFilter(new HomopolymerFilter<T>(getC(), length, this));
	}
	*/
	
	public final void setLength(int length) {
		this.length = length;
	}

	public final int getLength() {
		return length;
	}

}