package jacusa.filter.factory;

import java.util.List;

import jacusa.filter.HomopolymerDataFilter;
import jacusa.filter.cache.FilterCache;
import jacusa.filter.cache.HomopolymerFilterCache;
import lib.cli.options.BaseCallConfig;
import lib.cli.parameter.AbstractConditionParameter;
import lib.cli.parameter.AbstractParameter;
import lib.data.AbstractData;
import lib.data.builder.ConditionContainer;
import lib.data.generator.DataGenerator;
import lib.data.has.hasBaseCallCount;
import lib.data.has.hasHomopolymerInfo;
import lib.data.has.hasReferenceBase;
import lib.util.coordinate.CoordinateController;

public class HomopolymerFilterFactory<T extends AbstractData & hasBaseCallCount & hasReferenceBase, F extends AbstractData & hasHomopolymerInfo> 
extends AbstractDataFilterFactory<T, F> {

	private static final int MIN_HOMOPOLYMER_LENGTH = 7;
	private int length;
		
	public HomopolymerFilterFactory(final DataGenerator<F> dataGenerator) {
		super('Y', 
				"Filter wrong variant calls within homopolymers. Default: " + MIN_HOMOPOLYMER_LENGTH + " (Y:length)", 
				dataGenerator);
		length = MIN_HOMOPOLYMER_LENGTH;
	}
	
	@Override
	public void processCLI(final String line) throws IllegalArgumentException {
		if(line.length() == 1) {
			return;
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
		
		return new HomopolymerFilterCache<F>(getC(), MIN_HOMOPOLYMER_LENGTH, baseCallConfig, coordinateController);
	}
	
	@Override
	public void registerFilter(final CoordinateController coordinateController, 
			final ConditionContainer<T> conditionContainer) {

		final AbstractParameter<T, ?> parameter = conditionContainer.getParameter(); 
		
		final List<List<FilterCache<F>>> conditionFilterCaches = createConditionFilterCaches(parameter, coordinateController, this);
		final HomopolymerDataFilter<T, F> dataFilter = 
				new HomopolymerDataFilter<T, F>(getC(), parameter, this, conditionFilterCaches);
		conditionContainer.getFilterContainer().addDataFilter(dataFilter);
	}
	
	public final void setLength(int length) {
		this.length = length;
	}

	public final int getLength() {
		return length;
	}

}