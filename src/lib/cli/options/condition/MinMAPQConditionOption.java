package lib.cli.options.condition;

import java.util.List;

import lib.cli.parameters.AbstractConditionParameter;
import lib.data.AbstractData;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

public class MinMAPQConditionOption<T extends AbstractData> extends AbstractConditionACOption<T> {

	private static final String OPT = "m";
	private static final String LONG_OPT = "min-mapq";
	
	public MinMAPQConditionOption(final List<AbstractConditionParameter<T>> conditions) {
		super(OPT, LONG_OPT, conditions);
	}
	
	public MinMAPQConditionOption(final int conditionIndex, final AbstractConditionParameter<T> condition) {
		super(OPT, LONG_OPT, conditionIndex, condition);
	}
	
	@Override
	public Option getOption() {
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
				.longOpt(getLongOpt())
				.argName(getLongOpt().toUpperCase())
				.hasArg(true)
		        .desc(s)
		        .build();
	}

	@Override
	public void process(CommandLine line) throws IllegalArgumentException {
		if (line.hasOption(getOpt())) {
	    	final String value = line.getOptionValue(getOpt());
	    	final int minMapq = Integer.parseInt(value);
	    	if(minMapq < 0) {
	    		throw new IllegalArgumentException(getLongOpt().toUpperCase() + " = " + minMapq + " not valid.");
	    	}

	    	for (final AbstractConditionParameter<T> condition : getConditionParameters()) {
	    		condition.setMinMAPQ(minMapq);
	    	}
	    }
	}

}
