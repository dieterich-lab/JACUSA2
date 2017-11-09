package lib.cli.options;

import lib.cli.parameters.AbstractParameter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

public class FilterModusOption extends AbstractACOption {

	final private AbstractParameter<?> parameters;
	
	public FilterModusOption(final AbstractParameter<?> parameters) {
		super("s", "separate");
		this.parameters = parameters;
	}
	
	@Override
	public Option getOption() {
		return Option.builder(getOpt())
				.longOpt(getLongOpt())
				.hasArg(false)
		        .desc("Put feature-filtered results in to a separate file (= RESULT-FILE.filtered)")
		        .build();
	}
	
	@Override
	public void process(final CommandLine line) throws Exception {
		if (line.hasOption(getOpt())) {
			parameters.setSeparate(true);
	    }
	}

}