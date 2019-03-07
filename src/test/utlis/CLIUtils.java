package test.utlis;

import java.util.List;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import lib.cli.options.AbstractACOption;

public final class CLIUtils {

	private CLIUtils() {
		throw new AssertionError();
	}
	
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

	public static Options getOptions(final AbstractACOption acOption) {
		final Options options = new Options();
		options.addOption(acOption.getOption(false));
		return options;
	}
	
	public static Options getOptions(final List<AbstractACOption> acOptions) {
		final Options options = new Options();
		for (final AbstractACOption acOption : acOptions) {
			options.addOption(acOption.getOption(false));
		}
		return options;
	}
	
}
