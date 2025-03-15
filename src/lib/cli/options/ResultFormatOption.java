package lib.cli.options;

import java.util.Arrays;
import java.util.Map;

import lib.cli.parameter.GeneralParameter;
import lib.io.InputOutput;
import lib.io.ResultFormat;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

public class ResultFormatOption 
extends AbstractOption {

	private final GeneralParameter parameter;
	private final Map<Character, ResultFormat> resultFormats;

	public ResultFormatOption(final GeneralParameter parameter, 
			final Map<Character, ResultFormat> resultFormats) {

		super("f", "output-format");
		this.parameter 		= parameter;
		this.resultFormats 	= resultFormats;
	}

	@Override
	public Option getOption(final boolean printExtendedHelp) {
		StringBuilder sb = new StringBuilder();

		boolean required = true;
		for (char rf_id : resultFormats.keySet()) {
			ResultFormat resultFormat = resultFormats.get(rf_id);
			if (parameter.getResultFormat() != null && 
					resultFormat.getID() == parameter.getResultFormat().getID()) {
				sb.append("<*>");
				required = false;
			} else {
				sb.append("< >");
			}
			sb.append(" " + rf_id);
			sb.append(": ");
			sb.append(resultFormat.getDesc());
			sb.append("\n");
		}
		
		return Option.builder(getOpt())
				.argName(getLongOpt().toUpperCase())
				.hasArg(true)
				.required(required)
				.desc("Choose output format:\n" + sb.toString())
				.build(); 
	}

	@Override
	public void process(final CommandLine cmdLine) throws IllegalArgumentException {
		final String s = cmdLine.getOptionValue(getOpt());

		if((s.contains("insertion_ratio") && !cmdLine.hasOption('i')) || (s.contains("deletion_ratio") && !cmdLine.hasOption('D')) || (s.contains("modification_count") && !cmdLine.hasOption('M'))){
			throw new IllegalArgumentException("put options -i, -D, or -M to calculate insertion-, deletion-ratio, or modification-count");
		}

		/* FIXME enforce modification_count triggers reading mods
		if(s.contains("modification_count")){
			modificationOutputRequest = true;
		}
		*/ 

		//go through command line input
		for (int i = 0; i < s.length(); ++i) {
			final char c = s.charAt(i);
			if (! resultFormats.containsKey(c)) {
				//if character is no result format, check if it's a ':'
				if (c == ':') {
					//create string with just options after ':' and call processCLI() with it
					final String[] t = s.split(Character.toString(InputOutput.WITHIN_FIELD_SEP));
					String parsedLine = String.join("", Arrays.copyOfRange(t, 1, t.length));
					resultFormats.get(t[0].charAt(0)).processCLI(parsedLine);
					break;
				} else {
					//if character is unexpected
					throw new IllegalArgumentException("Unknown output format: " + c);
				}
			}
			//set result format put into command line (B,V,X)
			parameter.setResultFormat(resultFormats.get(c));
		}
	}

}