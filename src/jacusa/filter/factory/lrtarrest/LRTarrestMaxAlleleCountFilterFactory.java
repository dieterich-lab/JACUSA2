package jacusa.filter.factory.lrtarrest;

import htsjdk.samtools.util.StringUtil;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import jacusa.filter.AbstractFilter;
import jacusa.filter.factory.AbstractFilterFactory;
import jacusa.filter.factory.MaxAlleleCountFilterFactory;
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
		super(Option.builder(Character.toString('M'))
				.desc("Max allowed alleles per parallel pileup. Default: "+ MAX_ALLELES)
				.build());
		alleles = MAX_ALLELES;
	}

	@Override
	public void registerFilter(final CoordinateController coordinateController, ConditionContainer<T> conditionContainer) {
		conditionContainer.getFilterContainer().addFilter(new MaxAlleleFilter(getC()));
	}

	@Override
	public void processCLI(final CommandLine cmd) throws IllegalArgumentException {
		// format: M:2
		for (final Option option : cmd.getOptions()) {
			final String opt = option.getOpt();
			switch (opt) {
			case "maxAlleles":
				final int alleleCount = Integer.valueOf(cmd.getOptionValue(opt));
				if (alleleCount < 0) {
					throw new IllegalArgumentException("Invalid allele count: " + opt);
				}
				break;
				
			default:
				throw new IllegalArgumentException("Invalid argument: " + opt);
			}
		}
	}	
	@Override
	protected Options getOptions() {
		final Options options = new Options();
		options.addOption(MaxAlleleCountFilterFactory.getMaxAlleleOptionBuilder(MAX_ALLELES).build());
		// do we need this
		// options.addOption(RTArrestFactory.getReadsOptionBuilder().build());
		return options;
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
			final String value = StringUtil.join(Character.toString(BEDlikeWriter.VALUE_SEP), filteredRefPositions);
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
		sb.append(BEDlikeWriter.EMPTY_FIELD);	
	}
	
}
