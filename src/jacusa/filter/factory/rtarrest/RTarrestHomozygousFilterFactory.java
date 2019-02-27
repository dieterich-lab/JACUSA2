package jacusa.filter.factory.rtarrest;

import java.util.Set;

import jacusa.filter.Filter;
import jacusa.filter.factory.AbstractFilterFactory;
import jacusa.filter.factory.HomozygousFilterFactory;
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
 * This FilterFactory configures and creates the Homozygous filter for rt- and lrt-arrest method.
 * The user can decide which base call count instance should be used for counting and filtering, see
 * Apply2readsBaseCallCountSwitch
 */
public class RTarrestHomozygousFilterFactory
extends AbstractFilterFactory 
implements HasApply2reads {

	// which condition is required to be homozygous
	private final Apply2readsBaseCallCountSwitch bccSwitch;

	// use the existing simple implementation
	private final HomozygousFilterFactory homozygousFilterFactory;
	
	public RTarrestHomozygousFilterFactory(
			final int conditionSize, 
			final Apply2readsBaseCallCountSwitch bccSwitch) {

		super(HomozygousFilterFactory.getOptionBuilder().build());
		homozygousFilterFactory = new HomozygousFilterFactory(conditionSize, bccSwitch);
		this.bccSwitch 			= bccSwitch;
		
		// register acOptions in current instance
		getACOption().addAll(homozygousFilterFactory.getACOption());
		getApply2Reads().add(RT_READS.ARREST);
		getApply2Reads().add(RT_READS.THROUGH);
		getACOption().add(new Apply2readsOption(this));
	}

	@Override
	public Filter createFilter(
			CoordinateController coordinateController,
			ConditionContainer conditionContainer) {
		
		return homozygousFilterFactory.createFilter(coordinateController, conditionContainer);
	}
	
	@Override
	public Cache createFilterCache(
			ConditionParameter conditionParameter,
			SharedStorage sharedStorage) {

		return homozygousFilterFactory.createFilterCache(conditionParameter, sharedStorage);
	}
	
	@Override
	public void addFilteredData(StringBuilder sb, DataContainer filteredData) {
		homozygousFilterFactory.addFilteredData(sb, filteredData);	
	}
	
	@Override
	public Set<RT_READS> getApply2Reads() {
		return bccSwitch.getApply2reads();
	}
	
	@Override
	public void initDataContainer(AbstractBuilder builder) {
		homozygousFilterFactory.initDataContainer(builder);
	}
	
	
	
}
