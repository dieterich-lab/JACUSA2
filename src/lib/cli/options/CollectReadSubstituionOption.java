package lib.cli.options;

import lib.cli.options.has.HasReadSubstitution;
import lib.cli.options.has.HasReadSubstitution.BaseSubstitution;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

public class CollectReadSubstituionOption extends AbstractACOption {

	private final static char SEP = ',';
	
	private final HasReadSubstitution hasReadReadSubstitution;

	public CollectReadSubstituionOption(final HasReadSubstitution hasReadReadSubstitution) {
		super("B", "read-substitution");
		this.hasReadReadSubstitution = hasReadReadSubstitution;
	}

	@Override
	public Option getOption(final boolean printExtendedHelp) {
		return Option.builder(getOpt())
				.argName(getLongOpt().toUpperCase())
				.hasArg(true)
				.desc(
						"Count non-reference base substitution per read.\n" +
						"Requires stranded library type.\n" +
						"(Format for T to C mismatch: " + 
						"T" + HasReadSubstitution.BaseSubstitution.SEP + "C; " +
						"use '" + SEP + "' to separate substitutions)\n"+
						"Default: none" )
				.build();
	}

	@Override
	public void process(CommandLine line) throws Exception {
		final String v = line.getOptionValue(getOpt());
		for (final String s : v.split(Character.toString(SEP))) {
			final BaseSubstitution baseSubstitution = BaseSubstitution.string2enum(s);
	    	hasReadReadSubstitution.addReadSubstitution(baseSubstitution);
		}
	}

}