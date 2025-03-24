package jacusa.filter.factory;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Option.Builder;

import jacusa.filter.Filter;
import jacusa.filter.HomozygousFilter;
import lib.cli.options.filter.ConditionOption;
import lib.cli.options.filter.has.HasConditionIndex;
import lib.cli.parameter.ConditionParameter;
import lib.data.DataContainer;
import lib.data.DataContainer.AbstractBuilder;
import lib.data.count.basecall.BaseCallCount;
import lib.data.fetcher.Fetcher;
import lib.data.storage.Cache;
import lib.data.storage.container.SharedStorage;
import lib.io.InputOutput;
import lib.util.ConditionContainer;
import lib.util.coordinate.CoordinateController;

/**
 * This FilterFactory configures and creates the Homozygous filter.
 */
public class HomozygousFilterFactory 
extends AbstractFilterFactory 
implements HasConditionIndex {

	public static final char FILTER = 'H';
	
	// which condition is required to be homozygous
	private int conditionIndex;
	private final Fetcher<BaseCallCount> bccFetcher;
	
	public HomozygousFilterFactory(
			final int conditionSize, final Fetcher<BaseCallCount> bccFetcher) {
		
		super(getOptionBuilder().build());
				
		conditionIndex 				= -1;
		getOption().add(new ConditionOption(this, conditionSize));
		this.bccFetcher 			= bccFetcher;
	}

	@Override
	public void initDataContainer(AbstractBuilder builder) {
		// not needed
	}
	
	@Override
	public Filter createFilter(
			CoordinateController coordinateController, 
			ConditionContainer conditionContainer) {

		return new HomozygousFilter(getID(), conditionIndex, bccFetcher);
	}

	@Override
	public Cache createFilterCache(
			ConditionParameter conditionParameter,
			SharedStorage sharedStorage) {
		
		return null;
	}
		
	public static Builder getOptionBuilder() {
		return Option.builder(Character.toString(FILTER))
				.desc("Filter non-homozygous sites in condition 1 or 2.");
	}
	
	@Override
	public void addFilteredData(StringBuilder sb, DataContainer data) {
		// nothing to display
		// will be shown in filter column
		sb.append(InputOutput.EMPTY_FIELD);	
	}

	@Override
	public int getConditionIndex() {
		return conditionIndex;
	}
	
	@Override
	public void setConditionIndex(int conditionIndex) {
		this.conditionIndex = conditionIndex;
	}
	
}
