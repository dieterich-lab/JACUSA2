package jacusa.filter.factory;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import jacusa.filter.Filter;
import lib.cli.options.AbstractACOption;
import lib.cli.parameter.ConditionParameter;
import lib.data.DataContainer;
import lib.data.DataContainer.AbstractBuilder;
import lib.data.storage.Cache;
import lib.data.storage.container.SharedStorage;
import lib.util.CLIUtil;
import lib.util.ConditionContainer;
import lib.util.coordinate.CoordinateController;

/**
 * Interface defines methods for a FilterFactory that allows to configure filter 
 * options via processCLI and create new instance of filters.
 */
public interface FilterFactory {

	/**
	 * Gets unique char id of this filter.
	 * 
	 * @return unique char id 
	 */
	char getID();

	/**
	 * Gets the description for this filter.
	 * 
	 * @return string that describes this filter
	 */
	String getDesc();

	/**
	 * Process CLI options.
	 * 
	 * @param line to be parsed
	 */
	default void processCLI(String line) throws Exception {
		processCLI(CLIUtil.processCLI(line, getOptions()));
	}

	default  Set<Option> processCLI(final CommandLine cmd) throws Exception {
		final Set<Option> parsed = new HashSet<>();

		final Map<String, AbstractACOption> longOpt2acOption = 
				getACOption().stream()
					.collect(Collectors.toMap(
							AbstractACOption::getLongOpt, Function.identity()));
		
		for (final Option option : cmd.getOptions()) {
			final String longOpt = option.getLongOpt();
			if (longOpt2acOption.containsKey(longOpt)) {
				final AbstractACOption acOption = longOpt2acOption.get(longOpt);
				acOption.process(cmd);
				parsed.add(option);
			}
		}
		
		return parsed;
	}
	
	default Options getOptions() {
		final Options options = new Options();
		for (final AbstractACOption acOption : getACOption()) {
			options.addOption(acOption.getOption(false));
		}
		
		return options;
	}

	/**
	 * Returns available list of action options.
	 * 
	 * @return List of AbstractACOptions.
	 */
	List<AbstractACOption> getACOption();

	void addFilteredData(StringBuilder sb, DataContainer filteredData);

	/**
	 * Creates a Cache for this filter - may be null if filter does need a 
	 * cache to store data.
	 * 
	 * @param conditionParameter to be used for the cache
	 * @param baseCallConfig to be used for the cache
	 * @param coordinateController to be used for the cache
	 * @return Cache for this filter
	 */
	Cache createFilterCache(ConditionParameter conditionParameter, SharedStorage sharedStorage);

	/**
	 * Registers filter in a builder.
	 * @param builder to register the filter at
	 */
	void initDataContainer(AbstractBuilder builder);

	/**
	 * Creates an instance of the filter.
	 * 
	 * @param coordinateController to be used within filter
	 * @param conditionContainer to be used within filter
	 * @return a filter instance 
	 */
	abstract Filter createFilter(
			CoordinateController coordinateController, 
			ConditionContainer conditionContainer);

	
	/**
	 * Registers filter with given coordinateController and conditionContainer
	 * 
	 * @param coordinateController to be used within filter
	 * @param conditionContainer to be used within filter
	 */
	default void registerFilter(
			final CoordinateController coordinateController, 
			final ConditionContainer conditionContainer) {

		conditionContainer.getFilterContainer().addFilter(
				createFilter(coordinateController, conditionContainer));
	}

}
