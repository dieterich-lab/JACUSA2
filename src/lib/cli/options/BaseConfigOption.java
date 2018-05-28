package lib.cli.options;

import lib.cli.parameter.AbstractParameter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import htsjdk.samtools.util.SequenceUtil;

@Deprecated
public class BaseConfigOption extends AbstractACOption {

	final private AbstractParameter<?, ?> parameters;

	public BaseConfigOption(final AbstractParameter<?, ?> parameters) {
		super("C", "base-config");
		this.parameters = parameters;
	}

	@Override
	public Option getOption() {
		StringBuilder sb = new StringBuilder();
		for (final byte c : parameters.getBases()) {
			sb.append((byte)c);
		}

		return Option.builder(getOpt())
				.longOpt(getLongOpt())
				.argName(getLongOpt().toUpperCase())
				.hasArg(true)
				.desc("Choose what bases should be considered for variant calling: TC or AG or ACGT or AT...\n default: " + sb.toString())
				.build();
	}

	@Override
	public void process(final CommandLine line) throws IllegalArgumentException {
		if (line.hasOption(getOpt())) {
			final char[] values = line.getOptionValue(getOpt()).toCharArray();
	    	if (values.length < 2 || values.length > SequenceUtil.VALID_BASES_LOWER.length) {
	    		throw new IllegalArgumentException("Possible values for " + getLongOpt().toUpperCase() + ": TC, AG, ACGT, AT...");
	    	}
	    	final byte[] bases = new byte[values.length];
	    	for (int i = 0; i < values.length; ++i) {
	    		bases[i] = (byte)values[i];
	    	}
	    	parameters.setBases(bases);
	    }
	}

}