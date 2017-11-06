package jacusa.cli.options;

import jacusa.cli.parameters.AbstractParameters;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

/**
 * 
 * @author Michael Piechotta
 *
 */
public class ShowReferenceOption extends AbstractACOption {

	final private AbstractParameters<?> parameters;

	public ShowReferenceOption(final AbstractParameters<?> parameters) {
		super("R", "show-ref");
		this.parameters = parameters;
	}

	@SuppressWarnings("static-access")
	@Override
	public Option getOption() {
				return OptionBuilder.withLongOpt(getLongOpt())
			.withArgName(getLongOpt().toUpperCase())
			.hasArg(false)
	        .withDescription("Add reference base to output. BAM file(s) must have MD field!")
	        .create(getOpt());
	}

	@Override
	public void process(CommandLine line) throws Exception {
		if(line.hasOption(getOpt())) {
	    	parameters.setShowReferenceBase(true);
	    }
	}

}