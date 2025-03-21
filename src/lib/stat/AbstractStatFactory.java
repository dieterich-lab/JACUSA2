package lib.stat;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import lib.io.InputOutput;
import lib.util.CLIUtil;

/**
 * TODO add documentation
 */
public abstract class AbstractStatFactory {

	private final Option option;
	
	public AbstractStatFactory(final Option option) {
		this.option = option;
	}

	/**
	 * Returns a new instance of this StatisticCalculator.
	 * @param threshold
	 * @return
	 */
	public abstract AbstractStat newInstance(double threshold, int conditions);

	/**
	 * Process command lines options.
	 * 
	 * @param line
	 * @return
	 */
	protected abstract void processCLI(final CommandLine cmd);
	
	protected abstract Options getOptions();
	
	/**
	 * Return the short name of this StatisticCalculator.
	 * @return
	 */
	public String getName() {
		return option.getOpt();
	}
	
	/**
	 * Return a short description of this StatisticCalculator.
	 * @return
	 */
	public String getDesc() {
		Option tmp = (Option)option.clone();
		CLIUtil.adjustOption(tmp, getOptions(), tmp.getOpt().length());
		return tmp.getDescription();
	}

	public void processCLI(final String line) {
		final Options options = getOptions();
		if (options.getOptions().isEmpty() || line == null || line.isEmpty()) {
			return;
		}

		final String[] args = line.split(Character.toString(InputOutput.WITHIN_FIELD_SEP));
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

	@Override
	public boolean equals(Object obj) {
		if (! (obj instanceof AbstractStatFactory)) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		
		final AbstractStatFactory asf = (AbstractStatFactory) obj;
		return getName().equals(asf.getName()) && getDesc().equals(asf.getDesc()); 
	}

	@Override
	public int hashCode() {
		int hash = 1;
		hash = 31 * hash + getName().hashCode();
		hash = 31 * hash + getDesc().hashCode();
		return hash;
	}
	
}
