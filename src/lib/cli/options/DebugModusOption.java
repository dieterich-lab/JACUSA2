package lib.cli.options;

import lib.cli.parameters.AbstractParameters;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

public class DebugModusOption extends AbstractACOption {

	final private AbstractParameters<?> parameters;
	
	public DebugModusOption(final AbstractParameters<?> parameters) {
		super("x", "debug");
		this.parameters = parameters;
	}

	@Override
	public Option getOption() {
		return Option.builder(getOpt())
				.longOpt(getLongOpt())
		        .desc("turn on Debug modus")
		        .build();
	}

	@Override
	public void process(final CommandLine line) throws Exception {
		if(line.hasOption(getOpt())) {
			parameters.setDebug(true);
			parameters.getMethodFactory().debug();
	    }
	}
	
}