package jacusa.cli.options;

import jacusa.cli.parameters.AbstractParameters;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

public class FilterModusOption extends AbstractACOption {

	final private AbstractParameters<?> parameters;
	
	public FilterModusOption(final AbstractParameters<?> parameters) {
		super("s", "separate");
		this.parameters = parameters;
	}
	
	@Override
	public void process(CommandLine line) throws Exception {
		if(line.hasOption(getOpt())) {
			parameters.setSeparate(true);
	    }
	}

	@SuppressWarnings("static-access")
	@Override
	public Option getOption() {
		return OptionBuilder.withLongOpt(getLongOpt())
				.hasArg(false)
		        .withDescription("Put feature-filtered results in to a separate file (= RESULT-FILE.filtered)")
		        .create(getOpt());
	}

}