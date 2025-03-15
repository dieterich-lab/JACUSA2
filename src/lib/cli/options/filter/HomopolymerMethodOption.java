package lib.cli.options.filter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import htsjdk.samtools.util.StringUtil;
import lib.cli.options.AbstractOption;
import lib.cli.options.filter.has.HasHomopolymerMethod;
import lib.cli.options.filter.has.HasHomopolymerMethod.HomopolymerMethod;

public class HomopolymerMethodOption extends AbstractOption {

	private final HasHomopolymerMethod hasHomopolymerMethod;
	
	public HomopolymerMethodOption(final HasHomopolymerMethod hasHomopolymerMethod) {
		super(null, "method");
		this.hasHomopolymerMethod = hasHomopolymerMethod;
	}
	
	/**
	 * Tested in @see test.lib.cli.options.filter.HomopolymerMethodOptionTest
	 */
	@Override
	public void process(CommandLine line) throws Exception {
		final HomopolymerMethod method = HomopolymerMethod.valueOf(line.getOptionValue(getLongOpt()));
		if (method == null) {
			throw new IllegalArgumentException("Invalid argument for " + getLongOpt() + ": " + method);
		}
		hasHomopolymerMethod.setHomopolymerMethod(method);
	}
	
	@Override
	public Option getOption(boolean printExtendedHelp) {
		final HasHomopolymerMethod.HomopolymerMethod defaultMethod = 
				hasHomopolymerMethod.getHomopolymerMethod();
		final String s = StringUtil.join(", ", HomopolymerMethod.values());
		final StringBuilder sb = new StringBuilder();
		sb.append("Choose how to compute homopolymers: ");
		sb.append(s);
		sb.append(". Default: " + defaultMethod);
		return Option.builder()
				.longOpt(getLongOpt())
				.argName(getLongOpt().toUpperCase())
				.hasArg()
				.desc(sb.toString())
				.build();
	}

}
