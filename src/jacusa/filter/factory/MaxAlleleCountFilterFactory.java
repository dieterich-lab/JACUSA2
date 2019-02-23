package jacusa.filter.factory;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Option.Builder;

import jacusa.filter.Filter;
import jacusa.filter.MaxAlleleFilter;
import lib.cli.options.filter.MaxAlleleCountOption;
import lib.cli.options.filter.has.HasMaxAlleleCount;
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
 * TODO add comments
 */
public class MaxAlleleCountFilterFactory 
extends AbstractFilterFactory 
implements HasMaxAlleleCount {

	public static final char FILTER = 'M';
	
	// default value for max alleles
	public static final int MAX_ALLELES = 2;

	// chosen value
	private int maxAlleles;
	private final Fetcher<BaseCallCount> bccFetcher;
	
	public MaxAlleleCountFilterFactory(final Fetcher<BaseCallCount> bccFetcher) {
		super(getOptionBuilder().build());
		maxAlleles = MAX_ALLELES;
		getACOption().add(new MaxAlleleCountOption(this));
		this.bccFetcher = bccFetcher;
	}

	@Override
	public Filter createFilter(
			CoordinateController coordinateController,
			ConditionContainer conditionContainer) {
		
		return new MaxAlleleFilter(getC(), maxAlleles, bccFetcher);
	}
	
	@Override
	public Cache createFilterCache(
			ConditionParameter conditionParameter,
			SharedStorage sharedStorage) {

		return null;
	}
	
	@Override
	public void initDataContainer(AbstractBuilder builder) {
		// not needed
	}
	
	public static Builder getOptionBuilder() {
		return Option.builder(Character.toString(FILTER))
				.desc("Max allowed alleles per site.");
	}
	
	@Override
	public void addFilteredData(StringBuilder sb, DataContainer filteredData) {
		sb.append(InputOutput.EMPTY_FIELD);
	}

	@Override
	public int getMaxAlleleCount() {
		return maxAlleles;
	}
	
	@Override
	public void setMaxAlleleCount(int count) {
		this.maxAlleles = count;
	}
	
}
