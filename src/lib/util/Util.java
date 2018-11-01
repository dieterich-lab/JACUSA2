package lib.util;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import htsjdk.samtools.SAMSequenceDictionary;
import htsjdk.samtools.SamReader;
import htsjdk.samtools.SamReaderFactory;
import htsjdk.samtools.ValidationStringency;

public final class Util {
	
	private Util() {
		throw new AssertionError();
	}
	
	/**
	 * 
	 * @param recordFilename
	 * @return
	 * @throws IOException 
	 * @throws Exception
	 */
	public static SAMSequenceDictionary getSAMSequenceDictionary(final String recordFilename) throws IOException {
		final File file = new File(recordFilename);
		final SamReader reader = SamReaderFactory
				.make()
				.setOption(htsjdk.samtools.SamReaderFactory.Option.CACHE_FILE_BASED_INDEXES, false)
				.setOption(htsjdk.samtools.SamReaderFactory.Option.DONT_MEMORY_MAP_INDEX, true) // disable memory mapping
				.validationStringency(ValidationStringency.LENIENT)
				.open(file);
		final SAMSequenceDictionary sequenceDictionary = reader.getFileHeader().getSequenceDictionary();
		reader.close();
		return sequenceDictionary;
	}
	
	public static String printAlpha(final double a[]) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < a.length; ++i) {
			if (i > 0) {
				sb.append('\t');
			}
			sb.append(a[i]);
		}
		return sb.toString();
	}
	
	public static String printAlpha2(double a[]) {
		DecimalFormat df = new DecimalFormat("0.0000"); 
		StringBuilder sb = new StringBuilder();

		sb.append(df.format(a[0]));
		for (int i = 1; i < a.length; ++i) {
			sb.append("  ");
			sb.append(df.format(a[i]));
		}
		return sb.toString();
	}
	
	public static String printMatrix(final double m[][]) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < m.length; ++i) {
			for (int j = 0; j < m[i].length; ++j) {
				if (j > 0) {
					sb.append('\t');
				}
				sb.append(m[i][j]);
			}
			sb.append('\n');
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

	static public CommandLine processCLI(String line, final Options options) throws MissingOptionException{
		if (options.getOptions().size() == 0 || line == null || line.isEmpty()) {
			return null;
		}

		line = line.replaceAll(Character.toString(Util.WITHIN_FIELD_SEP), Character.toString(Util.WITHIN_FIELD_SEP)+"--");
		final String[] args = line.split(Character.toString(Util.WITHIN_FIELD_SEP));
		final CommandLineParser parser = new DefaultParser();
		
		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);
		} catch (final ParseException e) {
			e.printStackTrace();
		}
		return cmd;
	}
	
	public static void adjustOption(final org.apache.commons.cli.Option option, final Options options, final int padding) {
		adjustOption(option, options, padding, 60);
	}
	
	public static void adjustOption(final org.apache.commons.cli.Option option, final Options options, final int padding, final int width) {
		final StringBuilder sb = new StringBuilder();

		// add option description and wrap
		char[] space = new char[padding];
		Arrays.fill(space, ' ');
		Util.formatStr(sb, option.getDescription(), "|" + new String(space), width);
		
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
			Util.formatStr(sb, s, new String(space), width);
		}
		option.setDescription(sb.toString());
	}

	public static final String BASE_FIELD 		= "bases";
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

}