package jacusa.cli.options.condition;

import java.util.List;

import jacusa.cli.parameters.ConditionParameters;
import jacusa.data.AbstractData;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

public class MinBASQConditionOption<T extends AbstractData> extends AbstractConditionACOption<T> {

	private static final String OPT = "q";
	private static final String LONG_OPT = "min-basq";
	
	public MinBASQConditionOption(final int conditionIndex, final ConditionParameters<T> condition) {
		super(OPT, LONG_OPT, conditionIndex, condition);
	}
	
	public MinBASQConditionOption(final List<ConditionParameters<T>> conditions) {
		super(OPT, LONG_OPT, conditions);
	}
	
	@SuppressWarnings("static-access")
	@Override
	public Option getOption() {
		String s = new String();
		
		ConditionParameters<T> template = new ConditionParameters<T>(null); 
		if (getConditionIndex() >= 0) {
			s = " for condition " + getConditionIndex();
		} else if (getConditions().size() > 1) {
			s = " for all conditions";
		}
		s = "filter positions with base quality < " + getLongOpt().toUpperCase() +
				s + "\n default: " + template.getMinBASQ();

		return OptionBuilder.withLongOpt(getLongOpt())
			.withArgName(getLongOpt().toUpperCase())
			.hasArg(true)
	        .withDescription(s)
	        .create(getOpt());
	}

	@Override
	public void process(CommandLine line) throws Exception {
		if(line.hasOption(getOpt())) {
	    	String value = line.getOptionValue(getOpt());
	    	byte minBASQ = Byte.parseByte(value);
	    	if(minBASQ < 0) {
	    		throw new IllegalArgumentException(getLongOpt().toUpperCase() + " = " + minBASQ + " not valid.");
	    	}
	    	for (final ConditionParameters<T> condition : getConditions()) {
	    		condition.setMinBASQ(minBASQ);
	    	}
		}
	}

}
