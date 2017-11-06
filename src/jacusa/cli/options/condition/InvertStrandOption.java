package jacusa.cli.options.condition;

import java.util.List;

import jacusa.cli.parameters.ConditionParameters;
import jacusa.data.AbstractData;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

public class InvertStrandOption<T extends AbstractData> extends AbstractConditionACOption<T> {

	private static final String OPT = "i";
	private static final String LONG_OPT = "invert-strand";
	
	public InvertStrandOption(final List<ConditionParameters<T>> conditions) {
		super(OPT, LONG_OPT, conditions);
	}

	public InvertStrandOption(final int conditionIndex , final ConditionParameters<T> condition) {
		super(OPT, LONG_OPT, conditionIndex, condition);
	}

	@Override
	public void process(CommandLine line) throws Exception {
		if(line.hasOption(getOpt())) {
			for (final ConditionParameters<T> condition : getConditions()) {
				condition.setInvertStrand(true);
			}
	    }
	}

	@SuppressWarnings("static-access")
	@Override
	public Option getOption() {
		String s = new String();

		ConditionParameters<T> template = new ConditionParameters<T>(null);
		if (getConditionIndex() >= 0) {
			s = " of condition " + getConditionIndex();
		} else if (getConditions().size() > 1) {
			s = " of all conditions";
		}
		s = "Invert strand information" + s + "\ndefault: " + Boolean.toString(template.isInvertStrand());

		return OptionBuilder.withLongOpt(getLongOpt())
				.withArgName(getLongOpt().toUpperCase())
				.hasArg(false)
		        .withDescription(s)
		        .create(getOpt());
	}

}
