package jacusa.cli.options;

import jacusa.cli.parameters.AbstractParameters;
import jacusa.data.BaseCallConfig;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

public class BaseConfigOption extends AbstractACOption {

	final private AbstractParameters<?> parameters;

	public BaseConfigOption(final AbstractParameters<?> parameters) {
		super("C", "base-config");
		this.parameters = parameters;
	}

	@SuppressWarnings("static-access")
	@Override
	public Option getOption() {
		StringBuilder sb = new StringBuilder();
		for(char c : parameters.getBases()) {
			sb.append(c);
		}

		return OptionBuilder.withLongOpt(getLongOpt())
			.withArgName(getLongOpt().toUpperCase())
			.hasArg(true)
	        .withDescription("Choose what bases should be considered for variant calling: TC or AG or ACGT or AT...\n default: " + sb.toString())
	        .create(getOpt());
	}

	@Override
	public void process(CommandLine line) throws Exception {
		if(line.hasOption(getOpt())) {
	    	char[] values = line.getOptionValue(getOpt()).toCharArray();
	    	if(values.length < 2 || values.length > BaseCallConfig.BASES.length) {
	    		throw new IllegalArgumentException("Possible values for " + getLongOpt().toUpperCase() + ": TC, AG, ACGT, AT...");
	    	}
	    	parameters.setBases(values);
	    }
	}

}