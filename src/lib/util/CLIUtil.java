package lib.util;

import java.util.Arrays;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import lib.io.InputOutput;

@Deprecated
public final class CLIUtil {

	private CLIUtil() {
		throw new AssertionError();
	}

	@Deprecated
	public static CommandLine processCLI(String line, final Options options) throws ParseException {
		final String[] args = line.split("\\s+");
		final CommandLineParser parser = new DefaultParser();
		
		return parser.parse(options, args);
	}

	public static void adjustOption(final Option option, final Options options, final int padding) {
		adjustOption(option, options, padding, 70);
	}
	
	public static void adjustOption(final Option option, final Options options, final int padding, final int width) {
		final StringBuilder sb = new StringBuilder();
	
		// add option description and wrap
		char[] space = new char[padding];
		Arrays.fill(space, ' ');
		InputOutput.formatStr(sb, option.getDescription(), "|" + new String(space), width);
		
		int max = 3;
		for (final Option o : options.getOptions()) {
			max = Math.max(max, o.getLongOpt().length());
		}
		
		for (final Option o : options.getOptions()) {
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
	
	public static String adjustOptionDescriptions(final Options options, final int padding, final String prefix) {
		final StringBuilder sb = new StringBuilder();
	
		// add option description and wrap
		char[] space = new char[padding];
		Arrays.fill(space, ' ');
		
		int max = 3;
		for (final Option o : options.getOptions()) {
			max = Math.max(max, o.getLongOpt().length());
		}
		
		for (final Option o : options.getOptions()) {
			sb.append("| " + prefix);
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
			InputOutput.formatStr(sb, s, new String(space), 70);
		}
		
		return sb.toString();
	}
	

}
