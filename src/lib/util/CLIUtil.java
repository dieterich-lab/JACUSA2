package lib.util;

import java.util.Arrays;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import lib.io.InputOutput;

public final class CLIUtil {

	private CLIUtil() {
		throw new AssertionError();
	}

	public static CommandLine processCLI(String line, final Options options) throws ParseException {
		/*
		if (options.getOptions().size() == 0) { // TODO do we need this|| line == null || line.isEmpty()
			return null;
		}
		*/
	
		final String[] args = line.split("\\s+");
		final CommandLineParser parser = new DefaultParser();
		
		CommandLine cmd = parser.parse(options, args);
		return cmd;
	}

	public static void adjustOption(final org.apache.commons.cli.Option option, final Options options, final int padding) {
		adjustOption(option, options, padding, 70);
	}
	
	public static void adjustOption(final Option option, final Options options, final int padding, final int width) {
		final StringBuilder sb = new StringBuilder();
	
		// add option description and wrap
		char[] space = new char[padding];
		Arrays.fill(space, ' ');
		InputOutput.formatStr(sb, option.getDescription(), "|" + new String(space), width);
		
		int max = 3;
		for (final org.apache.commons.cli.Option o : options.getOptions()) {
			max = Math.max(max, o.getLongOpt().length());
		}
		
		for (final org.apache.commons.cli.Option o : options.getOptions()) {
			sb.append("| :");
			sb.append(o.getLongOpt());
	
			space = new char[max - o.getLongOpt().length() + 1];
			Arrays.fill(space, ' ');		
			sb.append(space);
			
			space = new char[max + 4];
			Arrays.fill(space, ' ');
			space[0] = '|';
			
			String s = o.getDescription();
			if (o.isRequired()) {
				s += " (Required)";
			}
			InputOutput.formatStr(sb, s, new String(space), width);
		}
		option.setDescription(sb.toString());
	}

}