package jacusa.filter.factory;

import jacusa.cli.parameters.AbstractParameters;
import jacusa.data.BaseQualData;
import jacusa.data.ParallelPileupData;
import jacusa.data.Result;
import jacusa.filter.AbstractFilter;
import jacusa.filter.FilterContainer;
import jacusa.pileup.iterator.WindowedIterator;

/**
 * 
 * @author Michael Piechotta
 *
 */
public class HomozygousFilterFactory<T extends BaseQualData> 
extends AbstractFilterFactory<T> {

	// 
	private int homozygousConditionIndex;
	//
	private AbstractParameters<T> parameters;
	//
	private boolean strict;
	
	public HomozygousFilterFactory(AbstractParameters<T> parameters) {
		super('H', "Filter non-homozygous pileup/BAM in condition 1 or 2 " +
				"(MUST be set to H:1 or H:2). Default: none");
		homozygousConditionIndex 	= 0;
		this.parameters 			= parameters;
		strict 						= parameters.collectLowQualityBaseCalls();
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
				if (conditionIndex >= 1 && conditionIndex <= parameters.getConditions()) {
					setHomozygousConditionIndex(conditionIndex);
				} else {
					throw new IllegalArgumentException("Invalid argument: " + line);
				}
				break;

			case 2: // consider low quality base calls
				if (! s[i].equals("strict")) {
					throw new IllegalArgumentException("Did you mean strict? " + line);
				}
				parameters.collectLowQualityBaseCalls(true);
				strict = true;
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
		if (strict) {
			return new HomozygousStrictFilter(getC());
		}
		return new HomozygousFilter(getC());
	}

	@Override
	public void registerFilter(FilterContainer<T> filterContainer) {
		filterContainer.add(getFilter());
	}
	
	private class HomozygousStrictFilter 
	extends AbstractFilter<T> {

		public HomozygousStrictFilter(final char c) {
			super(c);
		}

		@Override
		public boolean filter(final Result<T> result, final WindowedIterator<T> windowIterator) {
			int alleles = 0;
			alleles = windowIterator.getConditionContainer().getAlleleCount(homozygousConditionIndex, result.getParellelData().getCoordinate());
	
			if (alleles > 1) {
				return true;
			}
	
			return false;
		}
		@Override
		public int getOverhang() { return 0; }

	}
	
	private class HomozygousFilter 
	extends AbstractFilter<T> {

		public HomozygousFilter(final char c) {
			super(c);
		}

		@Override
		public boolean filter(final Result<T> result, final WindowedIterator<T> windowIterator) {
			final ParallelPileupData<T> parallelData = result.getParellelData();
			final int alleles = parallelData.getPooledData(homozygousConditionIndex)
					.getBaseQualCount().getAlleles().length;

			return alleles > 1;
		}
		
		@Override
		public int getOverhang() { return 0; }

	}
	
}