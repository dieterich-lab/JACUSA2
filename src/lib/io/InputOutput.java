package lib.io;

import java.util.ArrayList;
import java.util.List;

public final class InputOutput {

	public static final String BASE_FIELD 		= "bases";
	public static final String DELETION_FIELD 	= "deletions";
	public static final String INSERTION_FIELD 	= "insertions";
	public static final String ARREST_BASES 	= "arrest_bases";
	public static final String THROUGH_BASES 	= "through_bases";
	
	// TODO add comments.
	public static final char COMMENT			= '#';
	public static final String HEADER			= "##";
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
	public static final char AND 				= '&';

	private InputOutput() {
		new AssertionError();
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

}
