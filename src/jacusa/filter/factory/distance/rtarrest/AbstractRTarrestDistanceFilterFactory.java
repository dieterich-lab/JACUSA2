package jacusa.filter.factory.distance.rtarrest;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import jacusa.filter.factory.basecall.AbstractBaseCallCountFilterFactory;
import jacusa.method.rtarrest.RTArrestFactory;
import jacusa.method.rtarrest.RTArrestFactory.RT_READS;
import lib.data.AbstractData;
import lib.data.cache.extractor.basecall.ArrestBaseCallCountExtractor;
import lib.data.cache.extractor.basecall.BaseCallCountExtractor;
import lib.data.cache.extractor.basecall.BaseCallCountFilterDataExtractor;
import lib.data.cache.extractor.basecall.DefaultBaseCallCountExtractor;
import lib.data.cache.extractor.basecall.ThroughBaseCallCountExtractor;
import lib.data.has.HasArrestBaseCallCount;
import lib.data.has.HasBaseCallCount;
import lib.data.has.HasReferenceBase;
import lib.data.has.HasThroughBaseCallCount;
import lib.data.has.filter.HasBaseCallCountFilterData;

public abstract class AbstractRTarrestDistanceFilterFactory<T extends AbstractData & HasBaseCallCount & HasArrestBaseCallCount & HasThroughBaseCallCount & HasBaseCallCountFilterData & HasReferenceBase> 
extends AbstractBaseCallCountFilterFactory<T> {

	private final Set<RT_READS> apply2reads;

	public AbstractRTarrestDistanceFilterFactory(final Option option,
			final BaseCallCountExtractor<T> observed, final BaseCallCountExtractor<T> filtered, 
			final int defaultFilterDistance, final double defaultFilterMinRatio) {

		super(option, 
				observed, filtered,
				defaultFilterDistance, 
				defaultFilterMinRatio);
		
		apply2reads = new HashSet<RT_READS>(2);
	}
	
	public AbstractRTarrestDistanceFilterFactory(final Option option,
			final BaseCallCountExtractor<T> observed,
			final int defaultFilterDistance, final double defaultFilterMinRatio) {
		
		this(option, 
				observed, 
				new BaseCallCountFilterDataExtractor<T>(option.getOpt().charAt(0)), 
				defaultFilterDistance, defaultFilterMinRatio);
	}

	@Override
	protected Options getOptions() {
		final Options options = super.getOptions();
		options.addOption(RTArrestFactory.getReadsOptionBuilder(apply2reads).build());
		return options;
	}
	
	@Override
	public void processCLI(final CommandLine cmd) throws IllegalArgumentException {
		super.processCLI(cmd);
		
		// ignore any first array element of s (e.g.: s[0] = "-u DirMult") 
		for (final Option option : cmd.getOptions()) {
			final String longOpt = option.getLongOpt();
			switch (longOpt) {
				case "reads":
					final String optionValue = cmd.getOptionValue(longOpt);
					final Set<RT_READS> apply2reads = RTArrestFactory.processApply2Reads(optionValue);
					if (apply2reads.size() > 0) {
						getApply2Reads().clear();
						getApply2Reads().addAll(apply2reads);
						
						if (getApply2Reads().size() == 2) {
							setObserved(new DefaultBaseCallCountExtractor<T>());	
						} else {
							if (getApply2Reads().contains(RT_READS.ARREST)) {
								setObserved(new ArrestBaseCallCountExtractor<T>());	
							} else if (getApply2Reads().contains(RT_READS.THROUGH)) {
								setObserved(new ThroughBaseCallCountExtractor<T>());	
							} else {
								throw new IllegalStateException(); 
							}
						}
					}
				break;
	
			default:
				break;
			}
		}
	}

	protected Set<RT_READS> getApply2Reads() {
		return apply2reads;
	}
	
}