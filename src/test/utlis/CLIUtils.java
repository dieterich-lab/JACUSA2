package test.utlis;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;


import lib.util.Util;

public abstract class CLIUtils {

	public static Option getOptionSafe(final Options options, final String opt) {
		final Option option = options.getOption(opt);
		if (option == null) {
			throw new IllegalStateException("Option: " + opt + " cannot be found!");
		}
		return option;
	}
	
	public static void assignValue(final StringBuilder sb, final Options options, final String opt, final String value) {
		final Option option = CLIUtils.getOptionSafe(options, opt); 
		CLIUtils.assignValue(sb, option, value);
	}
	
	public static String assignValue(final Option option, final String value) {
		final StringBuilder sb = new StringBuilder();
		assignValue(sb, option, value);
		return sb.toString();
	}
	
	public static void assignValue(final StringBuilder sb, final Option option, final String value) {
		// options space
		if (sb.length() > 0) {
			sb.append(' ');
		}
		// add option
		if (option.hasLongOpt()) { // long
			sb.append("--");
			sb.append(option.getLongOpt());
		} else {
			sb.append('-'); // short
			sb.append(option.getOpt());
		}
		// add space
		if (!value.isEmpty()) {
			sb.append(' ');
			sb.append(value);
		}
	}

	// FIXME move somewhere else
	// FIXME give nice name
	public static String pr(final String longOpt, final String value) {
		return Util.WITHIN_FIELD_SEP + longOpt + "=" + value;
	}
	
}
