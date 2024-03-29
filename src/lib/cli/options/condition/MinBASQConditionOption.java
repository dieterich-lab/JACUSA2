package lib.cli.options.condition;

import java.util.List;

import lib.cli.parameter.ConditionParameter;
import lib.phred2prob.Phred2Prob;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

public class MinBASQConditionOption extends AbstractConditionACOption {

	private static final String OPT = "q";
	private static final String LONG_OPT = "min-basq";
	
	public MinBASQConditionOption(final ConditionParameter conditionParameter) {
		super(OPT, LONG_OPT, conditionParameter);
	}
	
	public MinBASQConditionOption(final List<ConditionParameter> conditionParameters) {
		super(OPT, LONG_OPT, conditionParameters);
	}
	
	@Override
	public Option getOption(final boolean printExtendedHelp) {
		String s = "";
		
		byte minBasq = getConditionParameter().getMinBASQ();
		if (getcondI() >= 0) {
			s = " for condition " + getcondI();
		} else if (getConditionParameters().size() > 1) {
			s = " for all conditions";
		}
		s = "filter positions with base quality < " + getLongOpt().toUpperCase() +
				s + "\n default: " + minBasq;

		return Option.builder(getOpt())
				.argName(getLongOpt().toUpperCase())
				.hasArg(true)
				.desc(s)
				.build();
	}

	/**
	 * Tested in @see test.lib.cli.options.condition.MinBASQConditionOptionTest
	 */
	@Override
	public void process(CommandLine line) throws Exception {
    	String value = line.getOptionValue(getOpt());
    	byte minBASQ = Byte.parseByte(value);
    	if(minBASQ < 0 || minBASQ > Phred2Prob.MAX_Q) {
    		throw new IllegalArgumentException(getLongOpt().toUpperCase() + " = " + minBASQ + " not valid.");
    	}
    	for (final ConditionParameter conditionParameter : getConditionParameters()) {
    		conditionParameter.setMinBASQ(minBASQ);
    	}
	}

}
