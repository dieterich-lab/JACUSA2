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

public interface FilterFactory {

	/**
	 * Gets unique char id of this filter.
	 * 
	 * @return unique char id 
	 */
	char getC();

	/**
	 * Gets the description for this filter.
	 * 
	 * @return string that describes this filter
	 */
	String getDesc();

	/**
	 * TODO add comments.
	 * 
	 * @param line
	 * @throws Exception 
	 */
	default void processCLI(String line) throws Exception {
		processCLI(CLIUtil.processCLI(line, getOptions()));
	}

	default  Set<Option> processCLI(final CommandLine cmd) throws Exception {
		final Set<Option> parsed = new HashSet<>();

		final Map<String, AbstractACOption> longOpt2acOption = getACOption().stream()
				.collect(Collectors.toMap(AbstractACOption::getLongOpt, Function.identity()));
		
		for (final Option option : cmd.getOptions()) {
			final String longOpt = option.getLongOpt();
			if (longOpt2acOption.containsKey(longOpt)) {
				final AbstractACOption acOption = longOpt2acOption.get(longOpt);
				acOption.process(cmd);
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
	
	List<AbstractACOption> getACOption();

	void addFilteredData(StringBuilder sb, DataContainer filteredData);

	/**
	 * TODO add comments.
	 * 
	 * @param conditionParameter
	 * @param baseCallConfig
	 * @param coordinateController
	 * @return
	 */
	Cache createFilterCache(ConditionParameter conditionParameter, SharedStorage sharedStorage);

	void initDataContainer(AbstractBuilder builder);

	abstract Filter createFilter(
			CoordinateController coordinateController, 
			ConditionContainer conditionContainer);

	
	/**
	 * TODO add comments.
	 * 
	 * @param coordinateController
	 * @param conditionContainer
	 */
	default void registerFilter(
			final CoordinateController coordinateController, 
			final ConditionContainer conditionContainer) {

		conditionContainer.getFilterContainer().addFilter(
				createFilter(coordinateController, conditionContainer));
	}

}