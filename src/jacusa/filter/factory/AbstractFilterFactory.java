package jacusa.filter.factory;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import jacusa.method.call.statistic.AbstractStatisticCalculator;
import lib.data.AbstractData;
import lib.data.builder.ConditionContainer;
import lib.util.Util;
import lib.util.coordinate.CoordinateController;

/**
 * This factory creates an artefact filter object and registers it.  
 * 
 * @param <T>
 */
public abstract class AbstractFilterFactory<T extends AbstractData> {

	private final Option option;

	public AbstractFilterFactory(final Option option) {
		this.option = option;
	}

	/**
	 * Gets unique char id of this filter.
	 * 
	 * @return unique char id 
	 */
	public char getC() {
		return option.getOpt().charAt(0);
	}

	/**
	 * Gets the description for this filter.
	 * 
	 * @return string that describes this filter
	 */
	public String getDesc() {
		// HACK
		Option tmp = (Option)option.clone();
		Util.adjustOption(tmp, getOptions());
		return tmp.getDescription();
	}

	/**
	 * TODO add comments.
	 * 
	 * @param line
	 */
	public void processCLI(final String line) {
		final Options options = getOptions();
		if (options.getOptions().size() == 0 || line == null || line.isEmpty()) {
			return;
		}

		final String[] args = line.split(Character.toString(AbstractStatisticCalculator.SEP));
		final CommandLineParser parser = new DefaultParser();
		
		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			e.printStackTrace();
			return;
		}
		processCLI(cmd);
	}

	protected abstract void processCLI(CommandLine cmd);
	protected abstract Options getOptions();

	/**
	 * TODO add comments.
	 * 
	 * @param coordinateController
	 * @param conditionContainer
	 */
	public abstract void registerFilter(final CoordinateController coordinateController, final ConditionContainer<T> conditionContainer);

	public abstract void addFilteredData(StringBuilder sb, T data);
	
} 