package jacusa.filter.factory.lrtarrest;

import htsjdk.samtools.util.StringUtil;

import java.util.HashSet;
import java.util.Set;

import jacusa.filter.AbstractFilter;
import jacusa.filter.factory.AbstractFilterFactory;
import jacusa.io.format.BEDlikeWriter;
import lib.data.AbstractData;
import lib.data.ParallelData;
import lib.data.builder.ConditionContainer;
import lib.data.count.BaseCallCount;
import lib.data.has.HasLRTarrestCount;
import lib.data.result.Result;
import lib.util.coordinate.CoordinateController;

/**
 * 
 * @author Michael Piechotta
 *
 */
public class LRTarrestMaxAlleleCountFilterFactory<T extends AbstractData & HasLRTarrestCount> 
extends AbstractFilterFactory<T> {
	
	// default value for max alleles
	private static final int MAX_ALLELES = 2;
	// chosen value
	private int alleles;
	
	public LRTarrestMaxAlleleCountFilterFactory() {
		super('M', 
				"Max allowed alleles per parallel pileup. Default: "+ MAX_ALLELES);
		alleles = MAX_ALLELES;
	}

	@Override
	public void registerFilter(final CoordinateController coordinateController, ConditionContainer<T> conditionContainer) {
		conditionContainer.getFilterContainer().addFilter(new MaxAlleleFilter(getC()));
	}

	@Override
	public void processCLI(String line) throws IllegalArgumentException {
		if (line.length() == 1) {
			return;
		}

		// format: M:2
		final String[] s = line.split(Character.toString(AbstractFilterFactory.OPTION_SEP));
		for (int i = 1; i < s.length; ++i) {
			switch(i) {

			case 1:
				final int alleleCount = Integer.valueOf(s[i]);
				if (alleleCount < 0) {
					throw new IllegalArgumentException("Invalid allele count " + line);
				}
				this.alleles = alleleCount;
				break;

			default:
				throw new IllegalArgumentException("Invalid argument: " + line);
			}
		}
	}
	
	/**
	 * TODO add comments.
	 */
	private class MaxAlleleFilter extends AbstractFilter<T> {
		
		private final Set<Integer> filteredRefPositions;
		
		public MaxAlleleFilter(final char c) {
			super(c);
			filteredRefPositions = new HashSet<Integer>(5);
		}
		
		@Override
		public boolean filter(final ParallelData<T> parallelData) {
			final T data = parallelData.getCombinedPooledData();

			if (data.getLRTarrestCount().getRefPos2bc4arrest().getRefPos().size() > 0) {
				filteredRefPositions.clear();
			}
			boolean filter = false;
			for (final int refPos : data.getLRTarrestCount().getRefPos2bc4arrest().getRefPos()) {
				final BaseCallCount baseCallCount = data.getLRTarrestCount().getRefPos2bc4arrest().getBaseCallCount(refPos);
				if (baseCallCount != null && baseCallCount.getAlleles().size() > alleles) {
					filter = true;
					filteredRefPositions.add(refPos);
				}
			}
			
			return filter;
		}

		@Override
		public void addInfo(Result<T> result) {
			final String value = StringUtil.join(Character.toString(BEDlikeWriter.SEP2), filteredRefPositions);
			// add position of artefact(s) to unique char id
			result.getFilterInfo().add(Character.toString(getC()), value);
		}
		
		@Override
		public int getOverhang() { 
			return 0;
		}

	}

	@Override
	public void addFilteredData(StringBuilder sb, T data) {
		sb.append(BEDlikeWriter.EMPTY);	
	}
	
}