package jacusa.filter.factory.rtarrest;

import java.util.Collections;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import jacusa.filter.AbstractFilter;
import jacusa.filter.MaxAlleleFilter;
import jacusa.filter.factory.AbstractFilterFactory;
import jacusa.filter.factory.MaxAlleleCountFilterFactory;
import jacusa.method.rtarrest.RTarrestMethod;
import jacusa.method.rtarrest.RTarrestMethod.RT_READS;
import lib.cli.parameter.AbstractConditionParameter;
import lib.data.DataTypeContainer;
import lib.data.DataTypeContainer.AbstractBuilder;
import lib.data.assembler.ConditionContainer;
import lib.data.cache.container.SharedCache;
import lib.data.cache.fetcher.basecall.Apply2readsBaseCallCountSwitch;
import lib.data.cache.record.RecordWrapperDataCache;
import lib.util.coordinate.CoordinateController;

public class RTarrestMaxAlleleCountFilterFactory 
extends AbstractFilterFactory {

	private MaxAlleleCountFilterFactory maxAlleleCountFilterFactory;
	private final Apply2readsBaseCallCountSwitch bccSwitch;
	
	public RTarrestMaxAlleleCountFilterFactory(
			final Apply2readsBaseCallCountSwitch bccSwitch) {
		super(MaxAlleleCountFilterFactory.getOptionBuilder().build());
		maxAlleleCountFilterFactory = new MaxAlleleCountFilterFactory(bccSwitch);
		this.bccSwitch = bccSwitch;
	}

	@Override
	public void inidDataTypeContainer(AbstractBuilder builder) {
		// nothing to do
	}
	
	public int getMaxAlleles() {
		return maxAlleleCountFilterFactory.getMaxAlleles();
	}
	
	public Set<RT_READS> getApply2Reads() {
		return Collections.unmodifiableSet(bccSwitch.getApply2reads());
	}

	@Override
	protected AbstractFilter createFilter(
			CoordinateController coordinateController,
			ConditionContainer conditionContainer) {
		
		return new MaxAlleleFilter(
				getC(), 
				maxAlleleCountFilterFactory.getMaxAlleles(),
				bccSwitch);
	}
	
	@Override
	public RecordWrapperDataCache createFilterCache(
			AbstractConditionParameter conditionParameter,
			SharedCache sharedCache) {

		return maxAlleleCountFilterFactory.createFilterCache(conditionParameter, sharedCache);
	}
	
	@Override
	public Set<Option> processCLI(final CommandLine cmd) throws IllegalArgumentException, MissingOptionException {
		final Set<Option> parsed = maxAlleleCountFilterFactory.processCLI(cmd);
		for (final Option option : cmd.getOptions()) {
			final String longOpt = option.getLongOpt();
			switch (longOpt) {
			case "reads": // choose arrest, through or arrest&through
				final String readsValue = cmd.getOptionValue(longOpt);
				bccSwitch.getApply2reads().clear();
				bccSwitch.getApply2reads().addAll(RTarrestMethod.processApply2Reads(readsValue));
				parsed.add(option);
				break;
			}
		}
		return parsed;
	}
	
	@Override
	public Options getOptions() {
		final Options options = maxAlleleCountFilterFactory.getOptions();
		options.addOption(RTarrestMethod.getReadsOptionBuilder(bccSwitch.getApply2reads()).build());
		return options;
	}

	@Override
	public void addFilteredData(StringBuilder sb, DataTypeContainer filteredData) {
		maxAlleleCountFilterFactory.addFilteredData(sb, filteredData);
	}
	
}
