package lib.cli.options;

import lib.cli.parameters.AbstractParameter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

public class ShowReferenceOption extends AbstractACOption {

	final private AbstractParameter<?, ?> parameters;

	public ShowReferenceOption(final AbstractParameter<?, ?> parameters) {
		super("S", "show-ref");
		this.parameters = parameters;
	}

	@Override
	public Option getOption() {
		return Option.builder(getOpt())
				.longOpt(getLongOpt())
				.argName(getLongOpt().toUpperCase())
				.hasArg(false)
				.desc("Add reference base to output")
				.build();
	}

	@Override
	public void process(CommandLine line) throws Exception {
		if(line.hasOption(getOpt())) {
	    	parameters.setShowReferenceBase(true);
	    }
	}

}