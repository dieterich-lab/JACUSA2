package lib.util;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public abstract class Util {

	
	
	public static String printAlpha(double a[]) {
		DecimalFormat df = new DecimalFormat("0.0000"); 
		StringBuilder sb = new StringBuilder();

		sb.append(df.format(a[0]));
		for (int i = 1; i < a.length; ++i) {
			sb.append("  ");
			sb.append(df.format(a[i]));
		}
		return sb.toString();
	}
	
	public static void formatStr(final StringBuilder sb, final String str, final String prefix, final int width) {
		// container for output lines - should be < width
		final List<String> outputLines = new ArrayList<String>();
		for (final String line : str.split("\n")) {
			// builder for one output line - should be < width 
			final StringBuilder lb = new StringBuilder();
			for (final String word : line.split(" ")) {
				if (lb.length() + word.length() < width) { // add new output line as long as < width
					if (lb.length() > 0) {
						lb.append(' ');
					}
					lb.append(word);
				} else {
					if (lb.length() > 0) { // add current line to output and reset line builder
						outputLines.add(lb.toString());
						lb.setLength(0);
					}
					if (word.length() > width) { // word does not fit in line -> split
						// split and add to output
						int index = 0;
						while (index < word.length()) {
							outputLines.add(word.substring(index, Math.min(word.length(), index + width)));
							index += width;
						}
					} else { // just add word to line builder
						lb.append(word);
					}
				}
			}
			if (lb.length() > 0) { // add current line to output and reset line builder
				outputLines.add(lb.toString());
				lb.setLength(0);
			}
		}

		// add prefix and newline
		boolean first = true;
		for (final String l : outputLines) {
			if (first) {
				first = false;
			} else {
				sb.append(prefix);
			}
			sb.append(l);
			sb.append('\n');
		}
	}

	public static void adjustOption(final Option option, final Options options, final int padding) {
		adjustOption(option, options, padding, 60);
	}
	
	public static void adjustOption(final Option option, final Options options, final int padding, final int width) {
		final StringBuilder sb = new StringBuilder();

		// add option description and wrap
		char[] space = new char[padding];
		Arrays.fill(space, ' ');
		Util.formatStr(sb, option.getDescription(), "|" + new String(space), width);
		
		int max = 3;
		for (final Option o : options.getOptions()) {
			max = Math.max(max, o.getOpt().length());
		}
		
		for (final Option o : options.getOptions()) {
			sb.append("| :");
			sb.append(o.getOpt());

			space = new char[max - o.getOpt().length() + 1];
			Arrays.fill(space, ' ');		
			sb.append(space);
			
			space = new char[max + 4];
			Arrays.fill(space, ' ');
			space[0] = '|';
			
			Util.formatStr(sb, o.getDescription(), new String(space), width);
		}
		option.setDescription(sb.toString());
	}

	// TODO add comments.
	public static final char COMMENT			= '#';
	// TODO add comments.
	public static final char EMPTY_FIELD 		= '*';
	// TODO add comments.
	public static final char FIELD_SEP 			= '\t';
	// TODO add comments.
	public static final char VALUE_SEP 			= ',';
	// TODO add comments.
	public static final char WITHIN_FIELD_SEP 	= ':';
	// TODO add comments.
	public static final char SEP4 				= ';';
	// TODO add comments.
	public static final char KEY_VALUE_SEP 		= '=';

}