package jacusa.filter.factory;

import jacusa.filter.AbstractFilter;
import jacusa.filter.FilterContainer;
import lib.cli.parameters.AbstractParameter;
import lib.data.AbstractData;
import lib.data.ParallelData;
import lib.data.builder.ConditionContainer;
import lib.data.has.hasBaseCallCount;

/**
 * 
 * @author Michael Piechotta
 *
 */
public class HomozygousFilterFactory<T extends AbstractData & hasBaseCallCount> 
extends AbstractFilterFactory<T, T> {

	// 
	private int homozygousConditionIndex;
	//
	private AbstractParameter<T, ?> parameters;

	public HomozygousFilterFactory(final AbstractParameter<T, ?> parameters) {
		super('H', "Filter non-homozygous pileup/BAM in condition 1 or 2 " +
				"(MUST be set to H:1 or H:2). Default: none", null);
		homozygousConditionIndex 	= 0;
		this.parameters 			= parameters;
	}

	@Override
	public void processCLI(final String line) throws IllegalArgumentException {
		if (line.length() == 1) {
			throw new IllegalArgumentException("Invalid argument " + line);
		}

		final String[] s = line.split(Character.toString(AbstractFilterFactory.SEP));
		
		// format of s: 	H:<condition>[:strict]
		// array content:	0:1			  :2
		for (int i = 1; i < s.length; ++i) {
			switch(i) {

			case 1: // set homozygous conditionIndex
				final int conditionIndex = Integer.parseInt(s[1]);
				// make sure conditionIndex is within provided conditions
				if (conditionIndex >= 1 && conditionIndex <= parameters.getConditionsSize()) {
					setHomozygousConditionIndex(conditionIndex);
				} else {
					throw new IllegalArgumentException("Invalid argument: " + line);
				}
				break;

			default:
				throw new IllegalArgumentException("Invalid argument: " + line);
			}
		}
	}
	
	public final void setHomozygousConditionIndex(final int conditionIndex) {
		this.homozygousConditionIndex = conditionIndex;
	}

	public final int getConditionIndex() {
		return homozygousConditionIndex;
	}

	@Override
	public AbstractFilter<T> getFilter() {
		return new HomozygousFilter(getC());
	}

	@Override
	public void registerFilter(final FilterContainer<T> filterContainer) {
		filterContainer.add(getFilter());
	}
	
	private class HomozygousFilter 
	extends AbstractFilter<T> {

		public HomozygousFilter(final char c) {
			super(c);
		}

		@Override
		public boolean filter(final ParallelData<T> parallelData, final ConditionContainer<T> conditionContainer) {
			final int alleles = parallelData.getPooledData(homozygousConditionIndex)
					.getBaseCallCount().getAlleles().length;

			return alleles > 1;
		}
		
		@Override
		public int getOverhang() { return 0; }

	}
	
}