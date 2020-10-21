package lib.cli.options;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import lib.cli.options.filter.has.BaseSub;
import lib.cli.options.filter.has.HasReadTag;

/**
 * This option enables data stratification by chosen read tags. 
 * Observed data, e.g.: variants will be stratified by mismatches vs. the reference
 * in each read.
 */
public class StratifyByReadTagOption extends AbstractACOption {

	public static final char SEP = ',';
	
	private final HasReadTag hasReadTag;
	
	public StratifyByReadTagOption(final HasReadTag hasReadTag) {
		super("B", "read-tag");
		this.hasReadTag = hasReadTag;
	}
	
	/**
	 * Tested in @see test.lib.cli.options.CollectReadSubstitutionOptionTest
	 */
	@Override
	public Option getOption(final boolean printExtendedHelp) {
		return Option.builder(getOpt())
				.argName(getLongOpt().toUpperCase())
				.hasArg(true)
				.desc(
						"Tag reads by base substitution.\n" + 
						"Count non-reference base substitution per read and stratify.\n" +
						"Requires stranded library type.\n" +
						"(Format for T to C mismatch: " + 
						"T" + BaseSub.SEP + "C; " +
						"use '" + SEP + "' to separate substitutions)\n"+
						"Default: none" )
				.build();
	}

	@Override
	public void process(CommandLine line) throws Exception {
		final String v = line.getOptionValue(getOpt());
		for (final String s : v.split(Character.toString(SEP))) {
			final BaseSub baseSub = BaseSub.string2enum(s);
	    	hasReadTag.addReadTag(baseSub);
		}
	}

}