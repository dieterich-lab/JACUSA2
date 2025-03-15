package lib.cli.options.filter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import lib.cli.options.AbstractOption;
import lib.cli.options.filter.has.HasConditionIndex;

public class ConditionOption extends AbstractOption {

	private final HasConditionIndex hasCondition;
	private final int conditionSize;
	
	public ConditionOption(final HasConditionIndex hasCondition, final int conditionSize) {
		super(null, "condition");
		this.hasCondition 	= hasCondition;
		this.conditionSize 	= conditionSize;
	}

	/**
	 * Tested in @see test.lib.cli.options.filter.ConditionOptionTest 
	 */
	@Override
	public void process(CommandLine line) throws Exception {
		final int conditionIndex = Integer.parseInt(line.getOptionValue(getLongOpt()));
		// make sure conditionIndex is within provided conditions
		if (conditionIndex >= 0 && conditionIndex < conditionSize) {
			hasCondition.setConditionIndex(conditionIndex); 
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
