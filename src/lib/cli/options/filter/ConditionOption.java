package lib.cli.options.filter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import lib.cli.options.AbstractACOption;
import lib.cli.options.filter.has.HasCondition;

public class ConditionOption extends AbstractACOption {

	private final HasCondition hasCondition;
	private final int conditionSize;
	
	public ConditionOption(final HasCondition hasCondition, final int conditionSize) {
		super(null, "condition");
		this.hasCondition 	= hasCondition;
		this.conditionSize 	= conditionSize;
	}

	/**
	 * Tested in @see test.lib.cli.options.filter.ConditionOptionTest 
	 */
	@Override
	public void process(CommandLine line) throws Exception {
		final int condition = Integer.parseInt(line.getOptionValue(getLongOpt()));
		// make sure condI is within provided conditions
		if (condition >= 1 && condition <= conditionSize) {
			// convert to [0, conditionSize)
			hasCondition.setCondition(condition - 1); 
		} else {
			throw new IllegalArgumentException("Invalid argument: " + getLongOpt());
		}
	}

	@Override
	public Option getOption(boolean printExtendedHelp) {
		return Option.builder()
				.longOpt(getLongOpt())
				.argName(getLongOpt().toUpperCase())
				.hasArg()
				.required()
				.desc("Possible values for condition: 1 or 2.")
				.build();
	}

}
