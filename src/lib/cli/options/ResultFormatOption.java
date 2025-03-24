package lib.cli.options;

import java.util.Arrays;
import java.util.Map;

import lib.cli.parameter.GeneralParameter;
import lib.data.has.HasProcessCommandLine;
import lib.io.InputOutput;
import lib.io.ResultFormat;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

public class ResultFormatOption 
extends AbstractProcessingOption {

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

		for (final char resultFormatID : resultFormats.keySet()) {
			final ResultFormat resultFormat = resultFormats.get(resultFormatID);
			if (parameter.getResultFormat() != null && 
					resultFormat.getID() == parameter.getResultFormat().getID()) {
				sb.append("<*>");
			} else {
				sb.append("< >");
			}
			sb.append(" " + resultFormatID);
			sb.append(": ");
			sb.append(resultFormat.getDesc());
			sb.append("\n");
		}
		
		// TODO printExtendedHelp
		
		return Option.builder(getOpt())
				.argName(getLongOpt().toUpperCase())
				.hasArg(true)
				.required(true)
				.desc("Choose output format:\n" + sb.toString())
				.build(); 
	}

	@Override
	public void process(final CommandLine cmd) throws IllegalArgumentException {
		final String line = cmd.getOptionValue(getOpt());

		// TODO put this somewhere else
		if((line.contains("insertion_ratio") && !cmd.hasOption('i')) || (line.contains("deletion_ratio") && !cmd.hasOption('D')) || (line.contains("modification_count") && !cmd.hasOption('M'))){
			throw new IllegalArgumentException("put options -i, -D, or -M to calculate insertion-, deletion-ratio, or modification-count");
		}

		/* FIXME enforce modification_count triggers reading mods
		if(s.contains("modification_count")){
			modificationOutputRequest = true;
		}
		*/ 

		ResultFormat resultFormat = null;
		
		if (line.indexOf(Character.toString(InputOutput.WITHIN_FIELD_SEP)) >= 0) {
			final String[] nameWithArgs = line.split(Character.toString(InputOutput.WITHIN_FIELD_SEP));
			final String[] args = Arrays.copyOfRange(nameWithArgs, 1, nameWithArgs.length);
			resultFormat = resultFormats.get(nameWithArgs[0].charAt(0));
			((HasProcessCommandLine)resultFormat).getProcessCommandLine().process(args);
		} else {
			resultFormat = resultFormats.get(line.charAt(0));
		}
		
		parameter.setResultFormat(resultFormat);
	}

}