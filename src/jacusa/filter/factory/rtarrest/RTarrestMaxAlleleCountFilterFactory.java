package jacusa.filter.factory.rtarrest;

import java.util.Set;

import jacusa.filter.Filter;
import jacusa.filter.factory.AbstractFilterFactory;
import jacusa.filter.factory.MaxAlleleCountFilterFactory;
import jacusa.method.rtarrest.RTarrestMethod.RT_READS;
import lib.cli.options.filter.Apply2readsOption;
import lib.cli.options.filter.has.HasApply2reads;
import lib.cli.parameter.ConditionParameter;
import lib.data.DataContainer;
import lib.data.DataContainer.AbstractBuilder;
import lib.data.fetcher.basecall.Apply2readsBaseCallCountSwitch;
import lib.data.storage.Cache;
import lib.data.storage.container.SharedStorage;
import lib.util.ConditionContainer;
import lib.util.coordinate.CoordinateController;

/**
 * This FilterFactory configures and creates the MaxAllele filter for rt- and lrt-arrest method.
 * The user can decide which base call count instance should be used for counting and filtering, see
 * Apply2readsBaseCallCountSwitch
 */
public class RTarrestMaxAlleleCountFilterFactory 
extends AbstractFilterFactory 
implements HasApply2reads {

	private MaxAlleleCountFilterFactory maxAlleleCountFilterFactory;
	private final Apply2readsBaseCallCountSwitch bccSwitch;
	
	public RTarrestMaxAlleleCountFilterFactory(
			final Apply2readsBaseCallCountSwitch bccSwitch) {

		super(MaxAlleleCountFilterFactory.getOptionBuilder().build());
		maxAlleleCountFilterFactory = new MaxAlleleCountFilterFactory(bccSwitch);
		this.bccSwitch = bccSwitch;
		getOption().addAll(maxAlleleCountFilterFactory.getOption());
		getApply2Reads().add(RT_READS.ARREST);
		getApply2Reads().add(RT_READS.THROUGH);
		getOption().add(new Apply2readsOption(this));
	}

	@Override
	public void initDataContainer(AbstractBuilder builder) {
		// nothing to do
	}

	@Override
	public Set<RT_READS> getApply2Reads() {
		return bccSwitch.getApply2reads();
	}

	@Override
	public Filter createFilter(
			CoordinateController coordinateController,
			ConditionContainer conditionContainer) {
		
		return maxAlleleCountFilterFactory.createFilter(coordinateController, conditionContainer);
	}
	
	@Override
	public Cache createFilterCache(
			ConditionParameter conditionParameter,
			SharedStorage sharedStorage) {

		return maxAlleleCountFilterFactory.createFilterCache(conditionParameter, sharedStorage);
	}

	@Override
	public void addFilteredData(StringBuilder sb, DataContainer filteredData) {
		maxAlleleCountFilterFactory.addFilteredData(sb, filteredData);
	}
	
}
