package lib.cli.options.condition;

import java.util.List;

import lib.cli.parameters.AbstractConditionParameter;
import lib.data.AbstractData;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

public class MinBASQConditionOption<T extends AbstractData> extends AbstractConditionACOption<T> {

	private static final String OPT = "q";
	private static final String LONG_OPT = "min-basq";
	
	public MinBASQConditionOption(final int conditionIndex, final AbstractConditionParameter<T> conditionParameter) {
		super(OPT, LONG_OPT, conditionIndex, conditionParameter);
	}
	
	public MinBASQConditionOption(final List<AbstractConditionParameter<T>> conditionParameters) {
		super(OPT, LONG_OPT, conditionParameters);
	}
	
	@Override
	public Option getOption() {
		String s = new String();
		
		byte minBasq = getConditionParameter().getMinBASQ();
		if (getConditionIndex() >= 0) {
			s = " for condition " + getConditionIndex();
		} else if (getConditionParameters().size() > 1) {
			s = " for all conditions";
		}
		s = "filter positions with base quality < " + getLongOpt().toUpperCase() +
				s + "\n default: " + minBasq;

		return Option.builder(getOpt())
				.longOpt(getLongOpt())
				.argName(getLongOpt().toUpperCase())
				.hasArg(true)
				.desc(s)
				.build();
	}

	@Override
	public void process(CommandLine line) throws Exception {
		if(line.hasOption(getOpt())) {
	    	String value = line.getOptionValue(getOpt());
	    	byte minBASQ = Byte.parseByte(value);
	    	if(minBASQ < 0) {
	    		throw new IllegalArgumentException(getLongOpt().toUpperCase() + " = " + minBASQ + " not valid.");
	    	}
	    	for (final AbstractConditionParameter<T> conditionParameter : getConditionParameters()) {
	    		conditionParameter.setMinBASQ(minBASQ);
	    	}
		}
	}

}
