package lib.cli.options.condition;

import java.util.List;

import lib.cli.parameter.ConditionParameter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

public class MinMAPQConditionOption extends AbstractConditionACOption {

	private static final String OPT = "m";
	private static final String LONG_OPT = "min-mapq";
	
	public MinMAPQConditionOption(final List<ConditionParameter> conditions) {
		super(OPT, LONG_OPT, conditions);
	}
	
	public MinMAPQConditionOption(final ConditionParameter condition) {
		super(OPT, LONG_OPT, condition);
	}
	
	@Override
	public Option getOption(final boolean printExtendedHelp) {
		String s = new String();

		int minMapq = -1;
		if (getConditionIndex() >= 0) {
			s = " for condition " + getConditionIndex();
			minMapq = getConditionParameter().getMinMAPQ();
		} else if (getConditionParameters().size() > 1) {
			s = " for all conditions";
			minMapq = getConditionParameters().get(0).getMinMAPQ();
		}
		s = "filter positions with MAPQ < " + getLongOpt().toUpperCase() + 
				s + "\ndefault: " + minMapq;
		
		return Option.builder(getOpt())
				.longOpt(getOpt())
				.argName(getLongOpt().toUpperCase())
				.hasArg(true)
		        .desc(s)
		        .build();
	}

	/**
	 * Tested in @see test.lib.cli.options.condition.MinMAPQConditionOptionTest
	 */
	@Override
	public void process(CommandLine line) throws IllegalArgumentException {
    	final String value = line.getOptionValue(getOpt());
    	final int minMapq = Integer.parseInt(value);
    	if(minMapq < 0 | minMapq > 255) {
    		throw new IllegalArgumentException(getLongOpt().toUpperCase() + " = " + minMapq + " not valid.");
    	}

    	for (final ConditionParameter condition : getConditionParameters()) {
    		condition.setMinMAPQ(minMapq);
    	}
	}

}
