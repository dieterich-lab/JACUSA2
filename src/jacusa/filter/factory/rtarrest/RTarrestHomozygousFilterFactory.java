package jacusa.filter.factory.rtarrest;

import java.util.Collections;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import jacusa.filter.AbstractFilter;
import jacusa.filter.HomozygousFilter;
import jacusa.filter.factory.AbstractFilterFactory;
import jacusa.filter.factory.HomozygousFilterFactory;
import jacusa.method.rtarrest.RTarrestMethod;
import jacusa.method.rtarrest.RTarrestMethod.RT_READS;
import lib.cli.parameter.AbstractConditionParameter;
import lib.data.DataTypeContainer;
import lib.data.DataTypeContainer.AbstractBuilder;
import lib.data.assembler.ConditionContainer;
import lib.data.cache.container.SharedCache;
import lib.data.cache.fetcher.basecall.Apply2readsBaseCallCountSwitch;
import lib.data.cache.record.RecordWrapperProcessor;
import lib.util.coordinate.CoordinateController;

/**
 * TODO add comments
 * @param 
 */
public class RTarrestHomozygousFilterFactory 
extends AbstractFilterFactory {

	private HomozygousFilterFactory homozygousFilterFactory;
	
	// which condition is required to be homozygous
	private final Apply2readsBaseCallCountSwitch bccSwitch;
	
	public RTarrestHomozygousFilterFactory(
			final int conditionSize, 
			final Apply2readsBaseCallCountSwitch bccSwitch) {
		
		super(HomozygousFilterFactory.getOptionBuilder().build());
		homozygousFilterFactory = new HomozygousFilterFactory(conditionSize, bccSwitch);
		this.bccSwitch = bccSwitch;
	}

	@Override
	public void inidDataTypeContainer(AbstractBuilder builder) {
		// nothing to do
	}
	
	@Override
	public Set<Option> processCLI(final CommandLine cmd) throws IllegalArgumentException, MissingOptionException {
		final Set<Option> parsed = homozygousFilterFactory.processCLI(cmd);
		for (final Option option : cmd.getOptions()) {
			final String longOpt = option.getLongOpt();
			switch (longOpt) {
			case "reads": // choose arrest, through or arrest&through
				final String optionValue = cmd.getOptionValue(longOpt);
				if (optionValue.isEmpty()) {
					throw new MissingOptionException("Missing value for " + longOpt);
				}
				final Set<RT_READS> tmpApply2reads = RTarrestMethod.processApply2Reads(optionValue);
				if (tmpApply2reads.size() == 0) {
					throw new IllegalArgumentException("Unknown value for " + longOpt);
				}
				bccSwitch.getApply2reads().clear();
				bccSwitch.getApply2reads().addAll(tmpApply2reads);
				parsed.add(option);
				break;
			}
		}
		return parsed;
	}

	@Override
	public Options getOptions() {
		final Options options = homozygousFilterFactory.getOptions();
		options.addOption(RTarrestMethod.getReadsOptionBuilder(bccSwitch.getApply2reads()).build());
		return options;
	}
	
	@Override
	protected AbstractFilter createFilter(
			CoordinateController coordinateController,
			ConditionContainer conditionContainer) {
		
		return new HomozygousFilter(
				getC(), 
				homozygousFilterFactory.getHomozygousConditionIndex(),
				bccSwitch);
	}
	
	@Override
	public RecordWrapperProcessor createFilterCache(
			AbstractConditionParameter conditionParameter,
			SharedCache sharedCache) {

		return null;
	}
	
	@Override
	public void addFilteredData(StringBuilder sb, DataTypeContainer filteredData) {
		homozygousFilterFactory.addFilteredData(sb, filteredData);	
	}
	
	public Set<RT_READS> getApply2Reads() {
		return Collections.unmodifiableSet(bccSwitch.getApply2reads());
	}
	
	public int getHomozygousConditionIndex() {
		return homozygousFilterFactory.getHomozygousConditionIndex();
	}
	
}
