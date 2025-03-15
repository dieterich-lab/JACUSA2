package lib.cli.options.filter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import lib.cli.options.AbstractOption;
import lib.cli.options.filter.has.HasHomopolymerLength;

public class HomopolymerLengthOption extends AbstractOption {

	private final HasHomopolymerLength hasHomopolymerLength;
	
	public HomopolymerLengthOption(final HasHomopolymerLength hasHomopolymerLength) {
		super(null, "length");
		this.hasHomopolymerLength = hasHomopolymerLength;
	}

	/**
	 * Tested in @see test.lib.cli.options.filter.HomopolymerLengthOptionTest
	 */
	@Override
	public void process(CommandLine line) throws Exception {
		final int length = Integer.parseInt(line.getOptionValue(getLongOpt()));
		if (length <= 0) {
			throw new IllegalArgumentException("Invalid argument for " + getLongOpt() + ": " + length);
		}
		hasHomopolymerLength.setHomopolymerLength(length);
	}

	@Override
	public Option getOption(boolean printExtendedHelp) {
		final int defaultHomopolymerLength = hasHomopolymerLength.getHomopolymerLength();
		return Option.builder()
				.longOpt(getLongOpt())
				.argName(getLongOpt().toUpperCase())
				.hasArg()
				.desc("must be > 0. Default: " + defaultHomopolymerLength)
				.build();
	}

}
