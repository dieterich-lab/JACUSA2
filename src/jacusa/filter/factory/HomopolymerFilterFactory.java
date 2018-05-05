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
import lib.data.has.HasBaseCallCount;
import lib.data.has.HasHomopolymerInfo;
import lib.data.has.HasReferenceBase;
import lib.util.coordinate.CoordinateController;

public class HomopolymerFilterFactory<T extends AbstractData & HasBaseCallCount & HasReferenceBase & HasHomopolymerInfo> 
extends AbstractDataFilterFactory<T> {

	// default length of consecutive identical base call for
	// a homopolymer
	private static final int MIN_HOMOPOLYMER_LENGTH = 7;
	// chosen length of homopolymer
	private int length;
		
	public HomopolymerFilterFactory() {
		super('Y', 
				"Filter wrong variant calls within homopolymers. Default: " + MIN_HOMOPOLYMER_LENGTH + " (Y:length)");
		length = MIN_HOMOPOLYMER_LENGTH;
	}
	
	@Override
	public void processCLI(final String line) throws IllegalArgumentException {
		if(line.length() == 1) {
			return;
		}

		// format Y:length
		final String[] s = line.split(Character.toString(AbstractFilterFactory.OPTION_SEP));
		for (int i = 1; i < s.length; ++i) {
			int value = Integer.valueOf(s[i]);

			switch(i) {
			case 1:
				this.length = value;
				break;

			default:
				throw new IllegalArgumentException("Invalid argument " + length);
			}
		}
	}

	@Override
	protected FilterCache<T> createFilterCache(
			AbstractConditionParameter<T> conditionParameter,
			BaseCallConfig baseCallConfig,
			CoordinateController coordinateController) {
		
		return new HomopolymerFilterCache<T>(getC(), MIN_HOMOPOLYMER_LENGTH, baseCallConfig, coordinateController);
	}
	
	@Override
	public void registerFilter(final CoordinateController coordinateController, 
			final ConditionContainer<T> conditionContainer) {

		final AbstractParameter<T, ?> parameter = conditionContainer.getParameter(); 

		// TODO add comments.
		final List<List<FilterCache<T>>> conditionFilterCaches = 
				createConditionFilterCaches(parameter, coordinateController, this);
		// create filter 
		final HomopolymerDataFilter<T> dataFilter = 
				new HomopolymerDataFilter<T>(getC(), length, parameter, conditionFilterCaches);
		// and propagate conditionFilterCache
		conditionContainer.getFilterContainer().addDataFilter(dataFilter);
	}

	@Override
	public void addFilteredData(StringBuilder sb, T data) {
		if (data.isHomopolymer()) {
			sb.append('1');
		} else {
			sb.append('0');
		}
	}
	
}
