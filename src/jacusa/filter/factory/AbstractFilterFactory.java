package jacusa.filter.factory;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import jacusa.method.call.statistic.AbstractStatisticCalculator;
import lib.data.AbstractData;
import lib.data.builder.ConditionContainer;
import lib.util.coordinate.CoordinateController;

/**
 * This factory creates an artefact filter object and registers it.  
 * 
 * @param <T>
 */
public abstract class AbstractFilterFactory<T extends AbstractData> {

	// unique char id - corresponds CLI
	private final char c;
	// description of filter - shown in help
	private final String desc;

	public AbstractFilterFactory(final char c, final String desc) {
		this.c 		= c;
		this.desc	= desc;
	}

	/**
	 * Gets unique char id of this filter.
	 * 
	 * @return unique char id 
	 */
	public char getC() {
		return c;
	}

	/**
	 * Gets the description for this filter.
	 * 
	 * @return string that describes this filter
	 */
	public String getDesc() {
		return desc;
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